(ns scarecrow.slide
  (:import [java.awt Graphics2D RenderingHints GraphicsEnvironment]
           [java.awt.font LineBreakMeasurer TextAttribute TextLayout]
           [java.awt.geom AffineTransform GeneralPath]
           [java.text AttributedString]))

(defn draw-slide [context idx]
  (let [slides @(:slides context)]
    (when (and slides (get slides idx))
      ((get slides idx))
      (.repaint @(:frame context)))))

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

(defn- get-width [width padding]
  (- width (padding 1) (padding 3)))

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

(defn toggle-fullscreen [context]
  (let [frame @(:frame context)
        gdev
        (.. GraphicsEnvironment
            getLocalGraphicsEnvironment
            getDefaultScreenDevice)]
    (.hide frame)
    (.removeNotify frame)
    (if (.getFullScreenWindow gdev)
      (do
        (.setUndecorated frame false)
        (.show frame)
        (.setFullScreenWindow gdev nil))
      (do
        (.setUndecorated frame true)
        (.show frame)
        (.setFullScreenWindow gdev frame)))))

(defn- to-astrs [strs font]
  (map #(doto (AttributedString. %) (.addAttribute TextAttribute/FONT font)) strs))

;; TODO
(defn- build-str-shape [#^Graphics2D g, strs, font, y-padding]
  (let [text-shape (GeneralPath.)
        frc (.getFontRenderContext g)
        a-strs (to-astrs strs font)]
    (loop [y y-padding, layouts (map #(TextLayout. (.getIterator %) frc) a-strs)]
      (if (empty? layouts) text-shape
          (do
            (let [layout (first layouts)
                  w (.getAdvance layout)
                  outline (.getOutline layout
                                       (AffineTransform/getTranslateInstance
                                        (double (- (/ w 2))) (double y)))]
              (.append text-shape outline false)
              (recur (+ y (.getAscent layout)) (rest layouts))))))))

(defn- calc-scale [bounds width height]
  (double (min (/ width (.width bounds))
               (/ height (.height bounds)))))

(defn- build-scale-affine [bounds width height padding]
  (let [w (- width (get padding 1) (get padding 3))
        h (- height (get padding 0) (get padding 2))
        scaling (calc-scale bounds w h)
        affine (AffineTransform.)]
    (doto (AffineTransform.)
      (.translate (double (- (+ (get padding 3)
                                (/ (- w (* scaling (.width bounds))) 2))
                             (* scaling (.x bounds))))
                  (double (- (+ (get padding 0)
                                (/ (- h (* scaling (.height bounds))) 2))
                             (* scaling (.y bounds)))))
      (.scale scaling scaling))))

;; TODO
(defn draw-aligned-text []
  "This is not implemented yet.")

(defn draw-fitted-text [#^Graphics2D g, strs, font, width, height, padding]
  (let [x-padding (get padding 3)
        y-padding (get padding 0)
        text-shape (build-str-shape g strs font y-padding)
        affine (build-scale-affine (.getBounds text-shape) width height padding)]
    (.transform text-shape affine)
    (enable-anti-alias g)
    (.fill g text-shape)
    (double (+ y-padding (.. text-shape getBounds height)))))

(defn draw-wrapped-text [#^Graphics2D g, str, font, width, padding]
  (let [wrap-width (get-width width padding)
        astr (doto (AttributedString. str) (.addAttribute TextAttribute/FONT font))
        iter (.getIterator astr)
        measurer (LineBreakMeasurer. iter (.getFontRenderContext g))
        end-idx (.getEndIndex iter)
        x-padding (get padding 3)]
    (loop [y (get padding 0)]
      (if (>= (.getPosition measurer) end-idx) y
          (let [layout (.nextLayout measurer wrap-width)]
            (.draw layout g x-padding (+ y (.getAscent layout)))
            (recur (get-next-y y layout)))))))

(defn draw-lines [#^Graphics2D g, lines, font, width, padding]
  (let [x-pad (get padding 3)
        y-pad (get padding 0)]
    (loop [l lines, y y-pad]
      (let [line (first l)]
        (if (nil? line) y
            (do (let [layout (get-text-layout g line font)]
                  (.draw layout g x-pad (+ y (.getAscent layout)))
                  (recur (rest l) (+ (/ (.getSize font) 2) (get-next-y y layout))))))))))
