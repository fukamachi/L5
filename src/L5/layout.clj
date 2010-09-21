(ns L5.layout
  (:use [clojure.contrib.string :only [split trim blank?]]
        L5)
  (:require [clojure.contrib.string :as str]
            [L5.slide :as slide]))

(defn normalize-strings [& strs]
  (remove blank?
          (flatten
           (map #(split #"[ \t]*\n[ \t]*" (trim %1)) strs))))

(defmacro with [params & body]
  `(binding [L5/*context* (ref (merge (context) ~params))]
     ~@(map (fn [e] `(doelem ~e)) body)))

(defmacro with-size [size & body]
  `(with {:font-size ~size} ~@body))

(defn img [file] (java.io.File. file))

(defmacro title [& strs]
  `{:attr {:font-size (* 1.3 (:font-size (context)))
           :text-align :center
           :padding {:bottom 20}}
    :body [~@strs]})

(defmacro lines [& strs]
  `{:body [~@strs]})

(defmacro item [& strs]
  `(lines ~@(map #(str "・" %) strs)))

(defmacro enum [& strs]
  `(lines
    ~@(map #(str %1 ". " %2) (range 1 (+ 1 (count strs))) strs)))

(defmacro t [& strs]
  `{:body [~@strs]
    :attr {:font-size nil
           :text-align :center
           :padding {:top 100 :right 100 :bottom 100 :left 100}}})
