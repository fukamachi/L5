(ns scarecrow.slide
  (:import [java.awt Graphics2D]
           [java.awt.font LineBreakMeasurer TextAttribute TextLayout]
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

(defn get-layout [#^Graphics2D g, str, font]
  (TextLayout. str font (.getFontRenderContext g)))

(defn- get-next-y [layout]
  (+ (.getAscent layout)
     (.getDescent layout)
     (.getLeading layout)))

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
          (recur (+ y (get-next-y layout))))))))

(defn draw-text-with-context [context str]
  (let [g (.getGraphics @(:panel context))
        font (:font context)
        width (:width context)
        padding (:padding context)]
    (draw-text g str font width padding)))

(defn draw-lines [context & lines]
  (let [g (-> context :panel deref .getGraphics)
        font (:font context)
        width (:width context)
        padding (:padding context)
        x-pad (get padding 3)
        y-pad (get padding 0)]
    (loop [l lines, y y-pad]
      (let [line (first l)]
        (when (not (nil? line))
          (let [layout (get-layout g line font)]
          (.draw layout g x-pad (+ y (.getAscent layout)))
          (recur (rest l) (+ y (get-next-y layout)))))))))
