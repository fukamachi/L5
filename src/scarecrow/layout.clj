(ns scarecrow.layout
  (:require [scarecrow.slide :as slide]))

(defmacro get-next-padding [y]
  `(let [pad# (-> ~'*context* :padding)]
     (vec (cons (+ ~y (get pad# 0)) (rest pad#)))))

(defmacro with-local-context [params & body]
  `(binding [~'*context* (merge ~'*context* ~params)] ~@body))

(defmacro with-current-y [& body]
  (let [y (gensym)]
    `(let [~y (ref 0)]
       ~@(map (fn [b]
                `(with-local-context {:padding (get-next-padding @~y)}
                       (dosync (ref-set ~y ~b))))
              body))))

(defmacro with [params & body]
  `(with-local-context ~params (with-current-y ~@body)))

(defmacro wrap [str]
  `(let [g# (.getGraphics @(:panel ~'*context*))
         font# (:font ~'*context*)
         width# (:width ~'*context*)
         padding# (:padding ~'*context*)]
     (slide/draw-wrapped-text g# ~str font# width# padding#)))

(defmacro fit [str]
  `(let [g# (.getGraphics @(:panel ~'*context*))
         font# (:font ~'*context*)
         width# (:width ~'*context*)
         height# (:height ~'*context*)
         padding# (:padding ~'*context*)]
     (slide/draw-fitted-text g# ~str font# width# height# padding#)))

(defmacro lines [& lines]
  `(let [g# (.getGraphics @(:panel ~'*context*))
         font# (:font ~'*context*)
         width# (:width ~'*context*)
         padding# (:padding ~'*context*)]
     (slide/draw-lines g# (list ~@lines) font# width# padding#)))

(defmacro itemize [& lines]
  `(lines ~@(map #(str "・" %) lines)))

(defmacro p [& body]
  `(fn [] (with-current-y ~@body)))
