(ns scarecrow.player
  (:use clojure.contrib.singleton)
  (:import [java.awt Color Dimension Font]
           [java.awt.image BufferedImage]
           [java.awt.event KeyListener KeyEvent ActionListener]
           [javax.swing JPanel JFrame]))

(declare prev-slide next-slide get-player)

(def player (ref nil))

(def default
     {:width 640
      :height 480
      :padding [15 15 15 15]
      :font (Font. "VL Gothic" 0 20)})

(defn- dispatch-event [keyCode]
  (cond
   (or (= keyCode KeyEvent/VK_BACK_SPACE)
       (= keyCode KeyEvent/VK_LEFT)) (prev-slide)
   (or (= keyCode KeyEvent/VK_ENTER)
       (= keyCode KeyEvent/VK_SPACE)
       (= keyCode KeyEvent/VK_RIGHT)) (next-slide)))

(defn- get-panel [width height]
  (let [buf (ref (BufferedImage. width
                                 height
                                 BufferedImage/TYPE_4BYTE_ABGR))
        panel
        (proxy [JPanel KeyListener] []
          (getPreferredSize [] (Dimension. (.getWidth @buf) (.getHeight @buf)))
          (keyPressed [e] (dispatch-event (.getKeyCode e)))
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
     :slides (or slides [])
     :current (ref 0)
     :width (:width params)
     :height (:height params)
     :padding (:padding params)
     :font (:font params)}))

(defn get-player [& params]
  (when (nil? @player)
    (dosync (ref-set player (global-singleton #(make-player (first params))))))
  (@player))

(defn next-slide []
  (println "next")
  (let [player (get-player)]
    (dosync (alter (:current player) inc))))

(defn prev-slide []
  (println "previous")
  (let [player (get-player)]
    (dosync (alter (:current player) dec))))
