(ns L5.layout
  (:use [clojure.contrib.string :only [split trim blank?]]
        L5)
  (:require [clojure.contrib.string :as str]
            [L5.slide :as slide])
  (:import java.awt.Font))

(defn normalize-strings [& strs]
  (remove blank?
          (flatten
           (map #(split #"[ \t]*\n[ \t]*" (trim %1)) strs))))

(defn get-next-padding [y]
  (assoc (:padding (context)) :top (+ (-> (context) :padding :top) y)))

(defmulti normalize-padding class)

(defmethod normalize-padding clojure.lang.PersistentVector [padding]
  (zipmap [:top :right :bottom :left] padding))

(defmethod normalize-padding clojure.lang.PersistentArrayMap [padding]
  (merge (:padding (context)) padding))

(defmethod normalize-padding :default [padding]
  (merge (:padding (context)) {:top padding}))

(defmacro with-local-context [params & body]
  `(binding [~'*context* (ref (merge (context) ~params))] ~@body))

(defmacro with-current-y [& body]
  (let [y (gensym)]
    `(let [~y (ref (-> (context) :padding :top))]
       ~@(map (fn [b]
                `(with-local-context {:padding (assoc (:padding (context)) :top @~y)}
                   (dosync (ref-set ~y ~b))))
              body))))

(defmacro with [params & body]
  `(with-local-context ~params (with-current-y ~@body)))

(defmacro with-size [size & body]
  `(with {:font (Font. (-> (context) :font .getFontName) 0 ~size)} ~@body))

(defmacro with-padding [padding & body]
  `(with {:padding (normalize-padding ~padding)} ~@body))

(defmacro title-page [& strs]
  `(with-size (int (* 1.5 (-> (context) :font .getSize)))
     (align [:center :middle] ~@strs)))

(defmacro with-title [title & body]
  `(with-current-y
     (with-size (* 1.3 (-> (context) :font .getSize))
       (align [:center] ~title))
     (with-padding (get-next-padding 5) ~@body)))

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

(defmacro align [align & strs]
  `(let [{g# :g font# :font width# :width height# :height padding# :padding} (context)]
     (slide/draw-aligned-text ~align @g# [~@strs] font# width# height# padding#)))

(defmacro t [& strs]
  `(with-padding [100 100 100 100]
     (fit ~@strs)))
