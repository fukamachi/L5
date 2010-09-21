(ns L5.slide
  (:import [java.awt Graphics2D Font RenderingHints GraphicsEnvironment]
           [java.awt.font LineBreakMeasurer TextAttribute TextLayout]
           [java.awt.geom AffineTransform GeneralPath]
           [java.text AttributedString]
           [javax.imageio ImageIO]
           [java.io File]))

(defn- enable-anti-alias [#^Graphics2D g]
  (doto g
    (.setRenderingHint
     RenderingHints/KEY_ANTIALIASING
     RenderingHints/VALUE_ANTIALIAS_ON)
    (.setRenderingHint
     RenderingHints/KEY_TEXT_ANTIALIASING
     RenderingHints/VALUE_TEXT_ANTIALIAS_ON)))

(defn- get-gdev []
  (.. GraphicsEnvironment
      getLocalGraphicsEnvironment
      getDefaultScreenDevice))

(defn fullscreen-off [context]
  (let [frame @(:frame context)]
    (.hide frame)
    (.removeNotify frame)
    (.setUndecorated frame false)
    (.show frame)
    (.setFullScreenWindow (get-gdev) nil)))

(defn fullscreen-on [context]
  (let [frame @(:frame context)]
    (.hide frame)
    (.removeNotify frame)
    (.setUndecorated frame true)
    (.show frame)
    (.setFullScreenWindow (get-gdev) frame)))

(defn toggle-fullscreen [context]
  (let [gdev (get-gdev)]
    (if (.getFullScreenWindow gdev)
      (fullscreen-off context)
      (fullscreen-on context))))

(defn affine-transform [[horizontal vertical] bounds width height]
  [(case horizontal
         :right (- (.width bounds) width)
         :center 0
         (.width bounds))
   (case vertical
         :bottom (- height (.height bounds))
         :middle (/ (- height (.height bounds)) 2)
         (.height bounds))])

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
  (let [[horizontal vertical] align
        [x y] (affine-transform align bounds width height)]
    (AffineTransform/getTranslateInstance (- x (:left padding)) y)))

(defn affine-fitted-text [bounds, width, height, padding]
  (let [w (- width (:left padding) (:right padding))
        h (- height (:top padding) (:bottom padding) 22)
        scaling (double (min (/ w (.width bounds))
                             (/ h (.height bounds))))
        affine (AffineTransform.)]
    (doto (AffineTransform.)
      (.translate (double (- (/ (- w (* scaling (.width bounds))) 2)
                             (* scaling (.x bounds))))
                  (double (- (/ (- h (* scaling (.height bounds))) 2)
                             (* scaling (.y bounds)))))
      (.scale scaling scaling))))

(defn draw [#^Graphics2D g, body, attr, width, height]
  (if (instance? File body) (.drawImage g (ImageIO/read body)
                                        (:left (:padding attr))
                                        (:top (:padding attr)))
      (let [font (Font. (:font-family attr) 0 (or (:font-size attr) 300))
            padding (:padding attr)
            text-shape (build-str-shape g body font width (:text-align attr))
            bounds (.getBounds text-shape)
            affine (cond
                    (nil? (:font-size attr))
                    (affine-fitted-text bounds width height padding)
                    (or (:text-align attr) (:vertical-align attr))
                    (affine-aligned-text [(:text-align attr) (:vertical-align attr)]
                                         bounds width height padding)
                    :else (AffineTransform/getTranslateInstance 0 0))]
        (let [next-y (draw-text-shape g text-shape affine padding)]
          (if (nil? (:font-size attr))
            (- next-y (-> attr :padding :bottom))
            next-y)))))

(defn- get-next-attr [attr y]
  (let [padding (:padding attr)]
    (assoc attr :padding (assoc padding :top (+ y (or (:top padding) 0))))))

(defn- normalize-attribute [context attr]
  (merge attr
         {:padding (merge (:padding context) {:top 0 :bottom 0} (:padding attr))
          :font-family (or (:font-family attr) (:font-family context))
          :font-size (if (contains? attr :font-size)
                       (:font-size attr)
                       (:font-size context))}))

;; NOTE: I want to put this at L5.clj, but it refers to this namespace.
;;       Need a namespace for utilities?
(defn normalize-element [context {body :body attr :attr}]
  {:body (if (vector? body) body [body])
   :attr (normalize-attribute context attr)})

(defn draw-slide [context idx]
  (let [slides @(:slides context)]
    (when (and @(:g context) slides (get slides idx))
      (let [y (ref (-> context :padding :top))]
        (doseq [elem (get slides idx)]
          (let [elem (normalize-element context elem)
                elem-y (draw @(:g context)
                             (:body elem)
                             (get-next-attr (:attr elem) @y)
                             (:width context) (:height context))]
            (dosync
             (ref-set y elem-y))))))))

(defn current-slide [context]
  (let [idx @(:current context)]
    (draw-slide context idx)))

(defn next-slide [context]
  (let [slides @(:slides context)
        idx (+ @(:current context) 1)]
    (when (> (count slides) idx)
      (println "NEXT")
      (draw-slide context idx)
      (dosync (alter (:current context) inc)))))

(defn prev-slide [context]
  (let [idx (- @(:current context) 1)]
    (when (>= idx 0)
      (println "PREV")
      (draw-slide context idx)
      (dosync (alter (:current context) dec)))))
