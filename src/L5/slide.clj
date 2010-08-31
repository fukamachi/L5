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
           frc (.getFontRenderContext g)
           a-strs (to-astrs strs font)]
       (loop [y 0, layouts (map #(TextLayout. (.getIterator %) frc) a-strs)]
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
               (recur (+ (/ (.getSize font) 2) (.getAscent layout) y) (rest layouts))))))))

(defn- calc-scale [bounds width height]
  (double (min (/ width (.width bounds))
               (/ height (.height bounds)))))

(defn- build-scale-affine [bounds width height]
  (let [scaling (calc-scale bounds width height)
        affine (AffineTransform.)]
    (doto (AffineTransform.)
      (.translate (double (- (/ (- width (* scaling (.width bounds))) 2)
                             (* scaling (.x bounds))))
                  (double (- (/ (- height (* scaling (.height bounds))) 2)
                             (* scaling (.y bounds)))))
      (.scale scaling scaling))))

(defn- draw-text-shape [#^Graphics2D g, #^GeneralPath text-shape, affine, padding]
  (.transform text-shape affine)
  (.transform text-shape (AffineTransform/getTranslateInstance (:left padding) (:top padding)))
  (enable-anti-alias g)
  (.fill g text-shape)
  (+ (.. text-shape getBounds height) (:top padding) 15))

(defn draw-aligned-text [align, #^Graphics2D g, strs, font, width, height, padding]
  (let [[horizontal vertical] align
        text-shape (build-str-shape g strs font width horizontal)
        [x y] (affine-transform align (.getBounds text-shape) width height)]
    (double (+ y (draw-text-shape g text-shape
                                  (AffineTransform/getTranslateInstance (- x (:left padding)) y)
                                  padding)))))

(defn draw-fitted-text [#^Graphics2D g, strs, font, width, height, padding]
  (let [text-shape (build-str-shape g strs font width)
        affine (build-scale-affine (.getBounds text-shape)
                                   (- width (:left padding) (:right padding))
                                   (- height (:top padding) (:bottom padding)))]
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

(defn draw-lines [#^Graphics2D g, strs, font, width, padding]
  (let [text-shape (build-str-shape g strs font width)]
    (draw-text-shape g text-shape (AffineTransform/getTranslateInstance 0 0) padding)))

(defn draw-image [#^Graphics2D g, file, padding]
  (let [image (ImageIO/read (File. file))]
    (.drawImage g image (int (:left padding)) (int (:top padding)) nil)))
