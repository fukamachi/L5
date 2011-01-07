(ns L5.slide
  (:import [java.awt Graphics2D Font RenderingHints GraphicsEnvironment Color Point]
           [java.awt.font LineBreakMeasurer TextAttribute TextLayout]
           [java.awt.geom AffineTransform GeneralPath]
           [java.text AttributedString]
           [java.awt.image BufferedImage]
           [java.io File]))

(defn- enable-anti-alias [#^Graphics2D g]
  (doto g
    (.setRenderingHint
     RenderingHints/KEY_ANTIALIASING
     RenderingHints/VALUE_ANTIALIAS_ON)
    (.setRenderingHint
     RenderingHints/KEY_TEXT_ANTIALIASING
     RenderingHints/VALUE_TEXT_ANTIALIAS_ON)))

(defn- gdev-contain? [gdev bounds]
  (let [gdev-bounds (.. gdev getDefaultConfiguration getBounds)]
    (some #(.contains gdev-bounds (Point. (first %) (second %)))
          [[(.getCenterX bounds) (.getCenterY bounds)]
           [(.getMinX bounds) (.getMinY bounds)]
           [(.getMaxX bounds) (.getMinY bounds)]
           [(.getMaxX bounds) (.getMaxY bounds)]
           [(.getMinX bounds) (.getMaxY bounds)]])))

(defn- get-screen-devices []
  (.getScreenDevices (GraphicsEnvironment/getLocalGraphicsEnvironment)))

(defn- detect-gdev [frame]
  (let [bounds (.getBounds frame)]
    (first
     (filter #(gdev-contain? % bounds) (get-screen-devices)))))

(defn get-fullscreen-gdev []
  (let [gdevs (filter #(.getFullScreenWindow %) (get-screen-devices))]
    (if (empty? gdevs) nil (first gdevs))))

(defn fullscreen-off [context]
  (let [frame @(:frame context)
        gdev (get-fullscreen-gdev)]
    (when gdev
      (.hide frame)
      (.removeNotify frame)
      (.setUndecorated frame false)
      (.show frame)
      (.setFullScreenWindow gdev nil))))

(defn fullscreen-on [context]
  (let [frame @(:frame context)]
    (.hide frame)
    (.removeNotify frame)
    (.setUndecorated frame true)
    (.show frame)
    (.setFullScreenWindow (detect-gdev frame) frame)))

(defn toggle-fullscreen [context]
  (let [gdev (get-fullscreen-gdev)]
    (if gdev
      (fullscreen-off context)
      (fullscreen-on context))))

(defn affine-transform [[horizontal vertical] bounds width height padding]
  [(case horizontal
         :right (- width (:left padding) (:right padding))
         :center (- (:left padding))
         0)
   (case vertical
         :bottom (- height (.height bounds) (:top padding))
         :middle (- (/ height 2) (:top padding))
         0)])

(defn- build-str-shape
  ([#^Graphics2D g, strs, font, width] (build-str-shape g strs font width :left))
  ([#^Graphics2D g, strs, font, width, h-align]
     (let [text-shape (GeneralPath.)
           frc (.getFontRenderContext g)]
       (loop [y 0, layouts (map #(TextLayout. % font frc) strs)]
         (if (empty? layouts) text-shape
             (let [layout (first layouts)
                   x (case h-align
                           :right (- (.getAdvance layout))
                           :center (/ (- width (.getAdvance layout)) 2)
                           0)
                   outline (.getOutline layout
                                        (AffineTransform/getTranslateInstance
                                         (double x) (double y)))]
               (.append text-shape outline false)
               (recur (+ (/ (.getSize font) 2) ;; line height is 150%
                         (.getAscent layout)
                         y)
                      (rest layouts))))))))

(defn- draw-text-shape [#^Graphics2D g, #^GeneralPath text-shape, affine, padding]
  (.transform text-shape affine)
  (.transform text-shape (AffineTransform/getTranslateInstance (:left padding) (:top padding)))
  (enable-anti-alias g)
  (.fill g text-shape)
  (+ (.getTranslateY affine) (.. text-shape getBounds height) (:top padding) (:bottom padding)))

(defn affine-aligned-text [align, bounds, width, height padding]
  (let [[x y] (affine-transform align bounds width height padding)]
    (AffineTransform/getTranslateInstance x y)))

(defn affine-fitted-text [bounds, width, height, padding]
  (let [w (- width (:left padding) (:right padding))
        h (- height (:top padding) (:bottom padding))
        scaling (double (min (/ w (.width bounds))
                             (/ h (.height bounds))))
        affine (AffineTransform.)]
    (doto (AffineTransform.)
      (.translate (double (- (/ (- w (* scaling (.width bounds))) 2)
                             (* scaling (.x bounds))))
                  (double (- (/ (- h (* scaling (.height bounds))) 2)
                             (* scaling (.y bounds)))))
      (.scale scaling scaling))))

(defn draw [#^Graphics2D g, body, attr]
  (if (instance? BufferedImage body) (.drawImage g body
                                                 (int (:left (:padding attr)))
                                                 (int (:top (:padding attr)))
                                                 nil)
      (let [font (Font. (:font-family attr) 0 (or (:font-size attr) 300))
            padding (merge-with + (:padding attr) (select-keys (:global-padding attr) [:left :right :bottom]))
            text-shape (build-str-shape g body font (:width attr) (:text-align attr))
            bounds (.getBounds text-shape)
            affine (cond
                    (nil? (:font-size attr))
                    (affine-fitted-text bounds (:width attr) (:height attr) padding)
                    (or (:text-align attr) (:vertical-align attr))
                    (affine-aligned-text [(:text-align attr) (:vertical-align attr)]
                                         bounds (:width attr) (:height attr) padding)
                    :else (AffineTransform/getTranslateInstance 0 0))
            default-color (.getColor g)]
        (when (:color attr) (.setColor g (:color attr)))
        (let [next-y (draw-text-shape g text-shape affine padding)]
          (.setColor g default-color)
          (if (nil? (:font-size attr))
            (- next-y (-> attr :global-padding :bottom))
            next-y)))))

(defn- get-next-attr [attr y]
  (let [padding (:padding attr)]
    (assoc attr :padding (assoc padding :top (+ y (or (:top padding) 0))))))

(defn- normalize-attribute [context attr]
  (merge (select-keys context
                      [:width :height
                       :font-family :font-size
                       :position :text-align
                       :global-padding])
         attr
         {:padding (merge (:padding context)
                          (if (= :fixed (:position attr)) {:top 0 :bottom 0})
                          (:padding attr))}))

;; NOTE: I want to put this at L5.clj, but it refers to this namespace.
;;       Need a namespace for utilities?
(defn normalize-element [context elem]
  (if (map? elem)
    (let [{body :body attr :attr} elem]
      {:body (if (or (vector? body) (instance? BufferedImage body))
               body [body])
       :attr (normalize-attribute context attr)})
    (normalize-element context {:body elem})))

(defn current-slide [context]
  (let [slides @(:slides context)
        idx @(:current context)]
    (println
     (format "%d / %d %s"
             (+ 1 idx) (count slides) (:body (first (get slides idx)))))
    (when (and @(:g context) slides (get slides idx))
      (let [y (ref (-> context :global-padding :top))]
        (doseq [elem (get slides idx)]
          (let [elem (normalize-element context elem)
                elem-y (draw @(:g context)
                             (:body elem)
                             (if (= :fixed (-> elem :attr :position))
                               (:attr elem)
                               (get-next-attr (:attr elem) @y)))]
            (dosync
             (ref-set y elem-y))))))))

(defn repaint [context] (.repaint @(:frame context)))

(defn next-slide [context]
  (let [slides @(:slides context)
        idx (+ @(:current context) 1)]
    (when (> (count slides) idx)
      (dosync (alter (:current context) inc))
      (repaint context))))

(defn prev-slide [context]
  (let [slides @(:slides context)
        idx (- @(:current context) 1)]
    (when (>= idx 0)
      (dosync (alter (:current context) dec))
      (repaint context))))
