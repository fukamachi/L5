(ns scarecrow.core
  (:use [scarecrow.context :only [get-context start]])
  (:require [scarecrow.slide :as s])
  (:import [javax.swing JFrame]))

(def *context* (get-context {:width 640 :height 480}))

(def slides
     [(fn [] (-> *context*
                 (s/draw-text-with-context "HELLO")))])

(defn -main []
  (let [frame (JFrame. "Scarecrow: Presentation with Clojure")]
    (doto frame
      (.add (:panel *context*))
      (.pack)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))
    (start *context* slides)))
