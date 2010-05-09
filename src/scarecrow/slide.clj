(ns scarecrow.slide
  (:import [java.awt.font LineBreakMeasurer TextAttribute]
           [java.text AttributedString]))

(defn next-slide [player]
  (let [slides (:slides player)
        idx (+ @(:current player) 1)]
    (when (>= (count slides) idx)
      ;; TODO: draw the slide
      ;(draw-slide (get idx slides))
      (dosync (alter (:current player) inc)))))

(defn prev-slide [player]
  (let [slides (:slides player)
        idx (- @(:current player) 1)]
    (when (>= idx 0)
      ;; TODO: draw the slide
      ;(draw-slide (get idx slides))
      (dosync (alter (:current player) dec)))))

(defn get-width [player]
  (let [padding (:padding player)]
    (- (:width player) (padding 1) (padding 3))))

(defn draw-text [player str]
  (let [g (-> player :panel .getGraphics)
        width (get-width player)
        font (:font player)
        astr (doto (AttributedString. str) (.addAttribute TextAttribute/FONT font))
        iter (.getIterator astr)
        measurer (LineBreakMeasurer. iter (.getFontRenderContext g))
        end-idx (.getEndIndex iter)
        padding (:padding player)
        x-padding (get padding 3)
        y-padding (get padding 0)]
    (loop [y y-padding]
      (if (>= (.getPosition measurer) end-idx)
        nil
        (let [layout (.nextLayout measurer width)
              dy (.getAscent layout)]
          (.draw layout g x-padding (+ y dy))
          (recur (+ y dy (.getDescent layout) (.getLeading layout))))))))
