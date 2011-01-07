(ns L5
  (:use [clojure.contrib.string :only [replace-re]]
        clojure.contrib.server-socket)
  (:require [L5.context :as context]
            [L5.export :as export]
            [L5.slide :as slide]
            [L5.file :as file])
  (:import [java.awt.event KeyEvent]))

(def *run-file* (ref nil))
(def *context* (ref nil))
(def *server-socket* (ref nil))

(defn context [] @*context*)

(defmacro aif [expr then & [else]]
  `(let [~'it ~expr]
     (if ~'it ~then ~else)))

(defmacro defcontext [params]
  `(if (context)
     (if (not (= ~params (:raw-context-params (context))))
       (let [current# @(:current (context))
             frame# @(:frame (context))]
         (dosync (ref-set *context* (context/make-context (assoc ~params :current current#))))
         (.setVisible @(:frame (context)) true)
         (.dispose frame#)))
     (dosync (ref-set *context* (context/make-context ~params)))))

(defmacro defslides [& slides]
  `(do
     (if (not (context)) (defcontext {}))
     (dosync (ref-set (:slides (context)) [~@slides]))))

(defn- map-set! [obj-map key val]
  (dosync (ref-set (get obj-map key) val)))

(defn repaint [] (.repaint @(:frame (context))))

(defn next-slide [& [n]]
  (dotimes [i (or n 1)]
    (slide/next-slide (context))))

(defn prev-slide [& [n]]
  (dotimes [i (or n 1)]
    (slide/prev-slide (context))))

(defn current-slide []
  (get @(:slides (context)) @(:current (context))))

(defn toggle-fullscreen [] (slide/toggle-fullscreen (context)))

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

(defn reload [] (load-file @*run-file*))

(defn go [n]
  (dosync (ref-set (:current (context)) n))
  (repaint))

(defn export [& [output]]
  (reload)
  (go 0)
  (slide/fullscreen-off (context))
  (.setSize @(:frame (context)) (:width (context)) (:height (context)))
  (export/jframe->pdf (file/ensure-file-ext output ["pdf"]) (context))
  (go 0))

(defn start
  ([] (start *file*))
  ([file]
     (when (not @*server-socket*)
       (dosync (ref-set *server-socket* (create-repl-server 12345 25))))

     (dosync (ref-set *run-file* file))

     (binding [start #()]
       (reload))

     (attach-event KeyEvent/VK_R #(reload))
     (attach-event KeyEvent/VK_E (fn [] (file/save-dialog @(:frame (context)) #(export %) file/pdf-filter)))
     (attach-event KeyEvent/VK_Q #(System/exit 0))

     (context/start (context))))

(defn select-file []
  (file/open-chooser "L5: Presentation with Clojure"
                     #(do (start %)
                          (println "Presentation is started. Good Luck!"))))
