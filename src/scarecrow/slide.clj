(ns scarecrow.slide
  (:import [java.awt Graphics2D RenderingHints]
           [java.awt.font LineBreakMeasurer TextAttribute TextLayout]
           [java.awt.geom AffineTransform GeneralPath]
           [java.text AttributedString]))

(defn draw-slide [context idx]
  (let [panel @(:panel context)]
    (.paintComponent panel (.getGraphics panel))
    ((get @(:slides context) idx))))

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

(defn draw-fitted-text [#^Graphics2D g, str, font, width, height, padding]
  (let [astr (doto (AttributedString. str) (.addAttribute TextAttribute/FONT font))
        text-shape (GeneralPath.)
        iter (.getIterator astr)
        x-padding (get padding 3)
        y-padding (get padding 0)
        layout (TextLayout. iter (.getFontRenderContext g))]
    ;; build text-shape
    (let [w (.getAdvance layout)
          outline (.getOutline layout
                               (AffineTransform/getTranslateInstance
                                (double (- (/ w 2))) (double y-padding)))]
      (.append text-shape outline false))
    ;; scaling
    (let [bounds (.getBounds text-shape)
          w (- width (get padding 1) (get padding 3))
          h (- height (get padding 0) (get padding 2))
          scaling (double (min (/ w (.width bounds))
                               (/ h (.height bounds))))
          affine (AffineTransform.)]
      (.translate affine
                  (double (+ x-padding
                             (/ (- w (* scaling (.width bounds))) 2)))
                  (double (+ y-padding
                             (/ (- h (* scaling (.height bounds))) 2))))
      (.scale affine scaling scaling)
      (.translate affine
                  (double (- (.x bounds)))
                  (double (- (.y bounds))))
      (.transform text-shape affine)
      (enable-anti-alias g)
      (.fill g text-shape))))

(defn draw-wrapped-text [#^Graphics2D g, str, font, width, padding]
  (let [wrap-width (get-width width padding)
        astr (doto (AttributedString. str) (.addAttribute TextAttribute/FONT font))
        iter (.getIterator astr)
        measurer (LineBreakMeasurer. iter (.getFontRenderContext g))
        end-idx (.getEndIndex iter)
        x-padding (get padding 3)
        y-padding (get padding 0)]
    (loop [y y-padding]
      (if (>= (.getPosition measurer) end-idx)
        nil
        (let [layout (.nextLayout measurer wrap-width)
              dy (.getAscent layout)]
          (.draw layout g x-padding (+ y dy))
          (recur (get-next-y y layout)))))))

(defn draw-lines [#^Graphics2D g, lines, font, width, padding]
  (let [x-pad (get padding 3)
        y-pad (get padding 0)]
    (loop [l lines, y y-pad]
      (let [line (first l)]
        (when (not (nil? line))
          (let [layout (get-text-layout g line font)]
            (.draw layout g x-pad (+ y (.getAscent layout)))
            (recur (rest l) (get-next-y y layout))))))))
