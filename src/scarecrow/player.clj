(ns scarecrow.player
  (:import [java.awt Color Dimension]
           [java.awt.image BufferedImage]
           [java.awt.event KeyListener KeyEvent ActionListener]
           [javax.swing JPanel JFrame Timer]
           [java.util.concurrent ConcurrentLinkedQueue]))

(defn next-slide [player]
  (println "next")
  (dosync (alter (:current player) inc)))

(defn prev-slide [player]
  (println "previous")
  (dosync (alter (:current player) dec)))

(defn draw-current-slide [player])

(defn notify-event [player keyCode]
  (cond
   (or (= keyCode KeyEvent/VK_BACK_SPACE)
       (= keyCode KeyEvent/VK_LEFT)) (prev-slide player)
   (or (= keyCode KeyEvent/VK_ENTER)
       (= keyCode KeyEvent/VK_SPACE)
       (= keyCode KeyEvent/VK_RIGHT)) (next-slide player)))

(defn make-player [params & slides]
  (let [player {:panel (ref nil)
                :slides slides
                :current (ref 0)
                :width (:width params)
                :height (:height params)}
        event-queue (ConcurrentLinkedQueue.)
        timer-handler
        (proxy [ActionListener] []
          (actionPerformed [e]
            (notify-event player (.poll event-queue))))
        timer (Timer. 20 timer-handler)]
    (.start timer)
    (merge player
           {:send-event #(.add event-queue %)
            :timer timer})))

(defn add-panel [player]
  (let [buf (ref (BufferedImage. (:width player)
                                 (:height player)
                                 BufferedImage/TYPE_4BYTE_ABGR))
        panel
        (proxy [JPanel KeyListener] []
          (getPreferredSize [] (Dimension. (.getWidth @buf) (.getHeight @buf)))
          (keyPressed [e] ((:send-event player) (.getKeyCode e)))
          (keyReleased [e])
          (keyTyped [e]))]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel))
    (assoc player :panel panel)
    panel))

