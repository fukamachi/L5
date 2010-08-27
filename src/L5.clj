(ns L5
  (:use [L5.context :only [make-context]])
  (:require [L5.slide :as slide]))

(def *context* (ref nil))

(defn context [] @*context*)

(defmacro defcontext [params]
  `(if (not @*context*)
     (dosync (ref-set *context* (make-context ~params)))))

(defmacro defslides [& slides]
  `(dosync (ref-set (:slides (context)) [~@slides])))

(defn- map-set! [obj-map key val]
  (dosync (ref-set (get obj-map key) val)))

(defn repaint [] (.repaint @(:frame (context))))

(defn next-slide [& [n]]
  (dotimes [i (or n 1)]
    (slide/next-slide (context)))
  (repaint))

(defn prev-slide [& [n]]
  (dotimes [i (or n 1)]
    (slide/prev-slide (context)))
  (repaint))

(defn toggle-fullscreen [] (slide/toggle-fullscreen (context)))

(defn reload [] (load-file "init.clj"))
