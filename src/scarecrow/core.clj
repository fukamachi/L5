(ns scarecrow.core
  (:use [scarecrow.context :only [make-context add-panel start]]
        [scarecrow.layout])
  (:require [scarecrow.slide :as s])
  (:import [java.awt Font]
           [javax.swing JFrame]))

(def *context* (make-context {:width 640 :height 480}))
(add-panel *context*)

(def slides
     [(p (with {:font (Font. "VL Gothic" 0 80)}
               (lines "メロスは" "激怒" "した。")))
      (p (with {:font (Font. "VL Gothic" 0 80)}
               (lines "吾輩は" "猫である。" "名前は" "まだない。")))
      (p (with {:font (Font. "VL Gothic" 0 10)}
               (fit "あ")))
      (p (with {:font (Font. "VL Gothic" 0 30)}
               (itemize "1個目" "2個目" "3個目")))])

(defn -main []
  (let [frame (JFrame. "Scarecrow: Presentation with Clojure")]
    (doto frame
      (.add @(:panel *context*))
      (.pack)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))
    (start *context* slides)))
