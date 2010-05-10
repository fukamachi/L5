(ns scarecrow.layout
  (:require [scarecrow.slide :as slide]))

(defmacro with-context [& body]
  `(-> ~'*context* ~@body))

(defmacro with [params & body]
  `(binding [~'*context* (merge ~'*context* ~params)]
     (with-context ~@body)))

(defn wrap [context str]
  (let [g (.getGraphics @(:panel context))
        font (:font context)
        width (:width context)
        padding (:padding context)]
    (slide/draw-wrapped-text g str font width padding)))

(defn fit [context str]
  (let [g (.getGraphics @(:panel context))
        font (:font context)
        width (:width context)
        height (:height context)
        padding (:padding context)]
    (slide/draw-fitted-text g str font width height padding)))

(defn lines [context & lines]
  (let [g (.getGraphics @(:panel context))
        font (:font context)
        width (:width context)
        padding (:padding context)]
    (slide/draw-lines g lines font width padding)))

(defmacro itemize [context & lines]
  `(lines ~context ~@(map #(str "・" %) lines)))

(defmacro p [& body]
  `(fn [] ~@body))
