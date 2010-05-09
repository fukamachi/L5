(ns scarecrow.slide
  (:import [java.awt Graphics2D]
           [java.awt.font LineBreakMeasurer TextAttribute]
           [java.text AttributedString]))

(defn draw-slide [context idx]
  ((get @(:slides context) idx)))

(defn next-slide [context]
  (let [slides @(:slides context)
        idx (+ @(:current context) 1)]
    (when (> (count slides) idx)
      (draw-slide context idx)
      (dosync (alter (:current context) inc)))))

(defn prev-slide [context]
  (let [idx (- @(:current context) 1)]
    (when (>= idx 0)
      (draw-slide context idx)
      (dosync (alter (:current context) dec)))))

(defn current-slide [context]
  (let [idx @(:current context)]
    (draw-slide context idx)))

(defn- get-width [width padding]
  (- width (padding 1) (padding 3)))

(defn draw-text [#^Graphics2D g, str, font, width, padding]
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
          (recur (+ y dy (.getDescent layout) (.getLeading layout))))))))

(defn draw-text-with-context [context str]
  (let [g (-> context :panel .getGraphics)
        font (:font context)
        width (:width context)
        padding (:padding context)]
    (draw-text g str font width padding)))
