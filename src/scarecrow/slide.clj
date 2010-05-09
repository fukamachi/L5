(ns scarecrow.slide
  (:import [java.awt Graphics2D]
           [java.awt.font LineBreakMeasurer TextAttribute]
           [java.text AttributedString]))

(defn draw-slide [player idx]
  ((get @(:slides player) idx)))

(defn next-slide [player]
  (let [slides @(:slides player)
        idx (+ @(:current player) 1)]
    (when (> (count slides) idx)
      (draw-slide player idx)
      (dosync (alter (:current player) inc)))))

(defn prev-slide [player]
  (let [idx (- @(:current player) 1)]
    (when (>= idx 0)
      (draw-slide player idx)
      (dosync (alter (:current player) dec)))))

(defn current-slide [player]
  (let [idx @(:current player)]
    (draw-slide player idx)))

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

(defn draw-text-with-player [player str]
  (let [g (-> player :panel .getGraphics)
        font (:font player)
        width (:width player)
        padding (:padding player)]
    (draw-text g str font width padding)))
