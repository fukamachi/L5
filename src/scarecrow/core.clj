(ns scarecrow.core
  (:require [scarecrow.player :as p])
  (:import [javax.swing JFrame]))

(defn -main []
  (let [player (p/get-player {:width 640 :height 480})
        frame (JFrame. "Scarecrow: Presentation with Clojure")]
    (doto frame
      (.add (:panel player))
      (.pack)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))))
