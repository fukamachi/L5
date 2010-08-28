(ns L5.layout
  (:use [clojure.contrib.string :only [split trim blank?]]
        L5)
  (:require [clojure.contrib.string :as str]
            [L5.slide :as slide])
  (:import java.awt.Font))

(defmacro with-gensyms [names & body]
  `(let ~(vec (mapcat (fn [n] [n `(gensym)]) names))
     ~@body))

(defn normalize-strings [& strs]
  (remove blank?
          (flatten
           (map #(split #"[ \t]*\n[ \t]*" (trim %1)) strs))))

(defn get-next-padding [y]
  (assoc (:padding (context)) :top y))

(defmulti normalize-padding class)

(defmethod normalize-padding clojure.lang.PersistentVector [padding]
  (zipmap [:top :right :bottom :left] padding))

(defmethod normalize-padding clojure.lang.PersistentArrayMap [padding] padding)

(defmethod normalize-padding :default [padding]
  (get-next-padding (+ (-> (context) :padding :top) padding)))

(defmacro with-local-context [params & body]
  `(binding [~'*context* (ref (merge (context) ~params))] ~@body))

(defmacro with-current-y [& body]
  (let [y (gensym)]
    `(let [~y (ref (-> (context) :padding :top))]
       ~@(map (fn [b]
                `(with-local-context {:padding (get-next-padding @~y)}
                   (dosync (ref-set ~y ~b))))
              body))))

(defmacro with [params & body]
  `(with-local-context ~params (with-current-y ~@body)))

(defmacro with-size [size & body]
  `(with {:font (Font. (-> (context) :font .getFontName) 0 ~size)} ~@body))

(defmacro with-padding [padding & body]
  `(with {:padding (normalize-padding ~padding)} ~@body))
;(defmacro with-padding [y & body]
;  `(with {:padding (get-next-padding (+ ((:padding (context)) 0) ~y))} ~@body))

(defmacro p [& body]
  `(fn [] (with-current-y ~@body)))

(defmacro img [file]
  `(let [{g# :g padding# :padding} (context)]
     (slide/draw-image @g# ~file padding#)))

(defmacro txt [& strs]
  `(let [{g# :g font# :font width# :width padding# :padding} (context)]
     (slide/draw-wrapped-text @g# (str ~@strs) font# width# padding#)))

(defmacro fit [& strs]
  `(let [{g# :g font# :font width# :width height# :height padding# :padding} (context)]
     (slide/draw-fitted-text @g# [~@strs] font# width# height# padding#)))

(defmacro lines [& strs]
  `(let [{g# :g font# :font width# :width padding# :padding} (context)]
     (slide/draw-lines @g# [~@strs] font# width# padding#)))

(defmacro item [& strs]
  `(lines ~@(map #(str "・" %) (apply normalize-strings strs))))

(defmacro enum [& strs]
  `(lines
    ~@(for [[n l] (map list (range 0 (count strs)) (apply normalize-strings strs))]
        `(str ~n ". " ~l))))

(defmacro center [& strs]
  (with-gensyms [g font width height]
    `(let [{~g :g ~font :font ~width :width ~height :height} (context)]
       (with-current-y
         ~@(map
             (fn [s] `(slide/draw-aligned-text @~g ~s ~font ~width (:padding (context))))
             strs)))))

(defmacro th [& strs]
  `(with-padding [100 100 100 100]
     (fit ~@strs)))
