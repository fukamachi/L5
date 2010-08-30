(ns L5.slide
  (:import [java.awt Graphics2D RenderingHints GraphicsEnvironment]
           [java.awt.font LineBreakMeasurer TextAttribute TextLayout]
           [java.awt.geom AffineTransform GeneralPath]
           [java.text AttributedString]
           [javax.imageio ImageIO]
           [java.io File]))

(defn draw-slide [context idx]
  (let [slides @(:slides context)]
    (when (and slides (get slides idx))
      ((get slides idx)))))

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

(defn- get-next-y [y layout]
  (+ y (.getAscent layout) (.getDescent layout) (.getLeading layout)))

(defn- get-text-layout [#^Graphics2D g, str, font]
  (TextLayout. str font (.getFontRenderContext g)))

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

(defn- to-astrs [strs font]
  (map #(doto (AttributedString. %) (.addAttribute TextAttribute/FONT font)) strs))

(defn- build-str-shape [#^Graphics2D g, strs, font, width]
  (let [text-shape (GeneralPath.)
        frc (.getFontRenderContext g)
        a-strs (to-astrs strs font)]
    (loop [y 0, layouts (map #(TextLayout. (.getIterator %) frc) a-strs)]
      (if (empty? layouts) text-shape
          (let [layout (first layouts)
                w (.getAdvance layout)
                outline (.getOutline layout
                                     ;; FIXME: always centerize this shape
                                     (AffineTransform/getTranslateInstance
                                      (double (/ (- width w) 2)) (double y)))]
            (.append text-shape outline false)
            (recur (+ y (.getAscent layout)) (rest layouts)))))))

(defn- calc-scale [bounds width height]
  (double (min (/ width (.width bounds))
               (/ height (.height bounds)))))

(defn- build-scale-affine [bounds width height padding]
  (let [w (- width (:right padding) (:left padding))
        h (- height (:top padding) (:bottom padding))
        scaling (calc-scale bounds w h)
        affine (AffineTransform.)]
    (doto (AffineTransform.)
      (.translate (double (- (+ (:left padding)
                                (/ (- w (* scaling (.width bounds))) 2))
                             (* scaling (.x bounds))))
                  (double (- (+ (:top padding)
                                (/ (- h (* scaling (.height bounds))) 2))
                             (* scaling (.y bounds)))))
      (.scale scaling scaling))))

(defn- draw-text-shape [#^Graphics2D g, #^GeneralPath text-shape, affine, padding]
  (.transform text-shape affine)
  (enable-anti-alias g)
  (.fill g text-shape)
  (.. text-shape getBounds height))

(defn draw-aligned-text [align, #^Graphics2D g, strs, font, width, height, padding]
  (let [[horizontal vertical] align
        text-shape (build-str-shape g strs font width)
        bounds (.getBounds text-shape)
        affine-x (case horizontal
                   :right (+ (.width bounds) (- width (:right padding)))
                   :center 0
                   (+ (.width bounds) (:left padding)))
        affine-y (case vertical
                   :bottom (- height (:bottom padding) (.height bounds))
                   :middle (/ height 2)
                   (+ (.height bounds) (:top padding)))]
    (double (+ affine-y
               (draw-text-shape g text-shape
                                (AffineTransform/getTranslateInstance
                                 affine-x affine-y)
                                padding)))))

(defn draw-fitted-text [#^Graphics2D g, strs, font, width, height, padding]
  (let [text-shape (build-str-shape g strs font width); (:top padding))
        affine (build-scale-affine (.getBounds text-shape) width height padding)]
    (draw-text-shape g text-shape affine padding)))

(defn draw-wrapped-text [#^Graphics2D g, str, font, width, padding]
  (let [wrap-width (- width (:right padding) (:left padding))
        astr (doto (AttributedString. str) (.addAttribute TextAttribute/FONT font))
        iter (.getIterator astr)
        measurer (LineBreakMeasurer. iter (.getFontRenderContext g))
        end-idx (.getEndIndex iter)
        x-padding (:left padding)]
    (loop [y (:top padding)]
      (if (>= (.getPosition measurer) end-idx) y
          (let [layout (.nextLayout measurer wrap-width)]
            (.draw layout g x-padding (+ y (.getAscent layout)))
            (recur (get-next-y y layout)))))))

;; TODO: rewrite with GeneralPath
(defn draw-lines [#^Graphics2D g, lines, font, width, padding]
  (loop [l lines, y (:top padding)]
    (let [line (first l)]
      (if (nil? line) y
          (let [layout (get-text-layout g line font)]
            (.draw layout g (:left padding) (+ y (.getAscent layout)))
            (recur (rest l) (+ (/ (.getSize font) 2) (get-next-y y layout))))))))

(defn draw-image [#^Graphics2D g, file, padding]
  (let [image (ImageIO/read (File. file))]
    (.drawImage g image (int (:left padding)) (int (:top padding)) nil)))
