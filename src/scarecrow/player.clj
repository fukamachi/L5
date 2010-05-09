(ns scarecrow.player
  (:use clojure.contrib.singleton)
  (:require [scarecrow.slide :as slide])
  (:import [java.awt Color Dimension Font]
           [java.awt.image BufferedImage]
           [java.awt.event KeyListener KeyEvent ActionListener]
           [javax.swing JPanel JFrame]))

(declare get-player)

(def player (ref nil))

(def default
     {:width 640
      :height 480
      :padding [15 15 15 15]
      :font (Font. "VL Gothic" 0 20)})

(defn dispatch-event [player keyCode]
  (cond
   (or (= keyCode KeyEvent/VK_BACK_SPACE)
       (= keyCode KeyEvent/VK_LEFT)) (slide/prev-slide player)
   (or (= keyCode KeyEvent/VK_ENTER)
       (= keyCode KeyEvent/VK_SPACE)
       (= keyCode KeyEvent/VK_RIGHT)) (slide/next-slide player)))

(defn- get-panel [width height]
  (let [buf (BufferedImage. width
                            height
                            BufferedImage/TYPE_4BYTE_ABGR)
        panel
        (proxy [JPanel KeyListener] []
          (getPreferredSize [] (Dimension. (.getWidth buf) (.getHeight buf)))
          (keyPressed [e] (dispatch-event (get-player) (.getKeyCode e)))
          (keyReleased [e])
          (keyTyped [e]))]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel))))

(defn make-player [params & slides]
  (let [width (:width params)
        height (:height params)
        params (merge default params)]
    {:panel (get-panel width height)
     :slides (ref (or slides []))
     :current (ref 0)
     :width (:width params)
     :height (:height params)
     :padding (:padding params)
     :font (:font params)}))

(defn get-player [& params]
  (when (nil? @player)
    (dosync (ref-set player (global-singleton #(make-player (first params))))))
  (@player))

(defn start [player slides]
  (dosync (ref-set (:slides player) slides))
  (slide/current-slide player))
