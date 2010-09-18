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

(defn- normalize-attribute [attr]
  ;; TODO: refactor
  (merge attr
         {:padding (merge (:padding (context)) (:padding attr))
          :font-family (if (contains? attr :font-family)
                         (:font-family attr)
                         (:font-family (context)))
          :font-size (if (contains? attr :font-size)
                       (:font-size attr)
                       (-> (context) :font .getSize))}))

(defn elem [{body :body attr :attr}]
  {:body (if (vector? body) body [body])
   :attr (normalize-attribute attr)})

(defmacro with [params & body]
  `(binding [L5/*context* (ref (merge (context) ~params))] ~@body))

(defmacro with-size [size & body]
  `(with {:font (Font. (-> (context) :font .getFontName) 0 ~size)} ~@body))

(defn img [file] (java.io.File. file))

(defmacro title [& strs]
  `(elem {:attr {:font-size (* 1.3 (-> (context) :font .getSize))
                 :text-align :center}
          :body [~@strs]}))

(defmacro lines [& strs]
  `(elem {:body [~@strs]}))

(defmacro item [& strs]
  `(lines ~@(map #(str "・" %) strs)))

(defmacro enum [& strs]
  `(lines
    ~@(map #(str %1 ". " %2) (range 1 (+ 1 (count strs))) strs)))

(defmacro t [& strs]
  `(elem {:body [~@strs]
          :attr {:font-size nil
                 :text-align :center
                 :padding {:top 100 :right 100 :bottom 100 :left 100}}}))
