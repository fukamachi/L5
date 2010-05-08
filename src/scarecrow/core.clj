(ns scarecrow.core
  (:require [scarecrow.player :as p])
  (:import [javax.swing JFrame]))

(defn -main []
  (let [player (p/make-player {:width 640 :height 480})
        panel (p/add-panel player)
        frame (JFrame. "Scarecrow: Presentation with Clojure")]
    (doto frame
      (.add panel)
      (.pack)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))))
