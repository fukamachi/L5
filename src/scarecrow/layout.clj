(ns scarecrow.layout
  (:require [scarecrow.slide :as slide]))

(defmacro get-next-padding [y]
  `(vec (cons ~y (-> ~'*context* :padding rest))))

(defmacro with-local-context [params & body]
  `(binding [~'*context* (merge ~'*context* ~params)] ~@body))

(defmacro with-current-y [& body]
  (let [y (gensym)]
    `(let [~y (ref (-> ~'*context* :padding (get 0)))]
       ~@(map (fn [b]
                `(with-local-context {:padding (get-next-padding @~y)}
                   (dosync (ref-set ~y (+ ~b 15)))))
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
