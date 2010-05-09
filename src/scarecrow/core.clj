(ns scarecrow.core
  (:use [scarecrow.context :only [make-context add-panel start]]
        [scarecrow.layout])
  (:require [scarecrow.slide :as s])
  (:import [java.awt Font]
           [javax.swing JFrame]))

(def *context* (make-context {:width 640 :height 480}))
(add-panel *context*)

(def slides
     [(fn [] (with-font (Font. "VL Gothic" 0 80)
               (s/draw-lines "メロスは" "激怒" "した。")))
      (fn [] (with-font (Font. "VL Gothic" 0 80)
               (s/draw-lines "吾輩は" "猫である。" "名前は" "まだない。")))])

(defn -main []
  (let [frame (JFrame. "Scarecrow: Presentation with Clojure")]
    (doto frame
      (.add @(:panel *context*))
      (.pack)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))
    (start *context* slides)))
