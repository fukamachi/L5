(ns scarecrow.player
  (:use clojure.contrib.singleton)
  (:import [java.awt Color Dimension]
           [java.awt.image BufferedImage]
           [java.awt.event KeyListener KeyEvent ActionListener]
           [javax.swing JPanel JFrame Timer]
           [java.util.concurrent ConcurrentLinkedQueue]))

(declare prev-slide next-slide get-player)

(def player (ref nil))

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
          (keyPressed [e] ((:send-event (get-player)) (.getKeyCode e)))
          (keyReleased [e])
          (keyTyped [e]))]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel))))

(defn make-player [params & slides]
  (let [width (:width params)
        height (:height params)
        event-queue (ConcurrentLinkedQueue.)
        timer-handler
        (proxy [ActionListener] []
          (actionPerformed [e]
            (dispatch-event (.poll event-queue))))
        timer (Timer. 20 timer-handler)]
    (.start timer)
    {:panel (get-panel width height)
     :slides slides
     :current (ref 0)
     :width width
     :height height
     :send-event #(.add event-queue %)
     :timer timer}))

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
