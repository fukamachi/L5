(ns L5
  (:require [L5.context :as context]
            [L5.export :as export]
            [L5.slide :as slide]))

(def *context* (ref nil))

(defn context [] @*context*)

(defmacro aif [expr then & [else]]
  `(let [~'it ~expr]
     (if ~'it ~then ~else)))

(defmacro defcontext [params]
  `(if (not @*context*)
     (dosync (ref-set *context* (context/make-context ~params)))))

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

(defn reload [] (load-file "run.clj"))

(defn attach-event [code f]
  (let [actions (:actions (context))
        new-action (conj (or (get @actions code) []) f)]
    (dosync
     (ref-set actions
              (assoc @actions code new-action)))))

(defn detach-event [code f]
  (let [actions (:actions (context))
        new-action (aif (get @actions code)
                        (remove #(= %1 f) it)
                        [])]
    (dosync
     (ref-set actions
              (assoc @actions code new-action)))))

(defn doelem [elem]
  (slide/normalize-element (context) elem))

(defn export [& [output]]
  (reload)
  (export/jframe->pdf (or output "output.pdf") (context))
  (System/exit 0))

(defn start []
  (reload)
  (context/start (context)))
