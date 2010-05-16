(ns scarecrow.context
  (:require [scarecrow.slide :as slide])
  (:import [java.awt Color Dimension Font]
           [java.awt.image BufferedImage]
           [java.awt.event KeyListener KeyEvent ActionListener ComponentListener]
           [javax.swing JPanel JFrame]))

(defn map-set! [context key val]
  (dosync (ref-set (get context key) val)))

(def default
     {:width 640
      :height 480
      :padding [15 15 15 15]
      :font (Font. "VL Gothic" 0 20)})

(defn dispatch-event [context keyCode]
  (cond
   (= keyCode KeyEvent/VK_F5) (slide/toggle-fullscreen context)
   (or (= keyCode KeyEvent/VK_BACK_SPACE)
       (= keyCode KeyEvent/VK_LEFT)) (slide/prev-slide context)
   (or (= keyCode KeyEvent/VK_ENTER)
       (= keyCode KeyEvent/VK_SPACE)
       (= keyCode KeyEvent/VK_RIGHT)) (slide/next-slide context)))

(defn build-context [params & slides]
  (let [params (merge default params)]
    {:g (ref nil)
     :frame (ref nil)
     :slides (ref (or slides []))
     :current (ref 0)
     :width (:width params)
     :height (:height params)
     :padding (:padding params)
     :font (:font params)}))

(defn build-panel [context]
  (let [zoom (ref 1.0)
        panel
        (proxy [JPanel KeyListener ComponentListener] []
          (getPreferredSize []
                            (Dimension.
                             (* @zoom (:width context))
                             (* @zoom (:height context))))
          (paintComponent [g]
                          (when (:g context)
                          (proxy-super paintComponent g)
                          (.scale g @zoom @zoom)
                          (map-set! context :g g)
                          (slide/current-slide context)))
          (keyPressed [e] (dispatch-event context (.getKeyCode e)))
          (keyReleased [e])
          (keyTyped [e])
          (componentResized [e]
                            (let [scale
                                  (min (double (/ (.getWidth this) (:width context)))
                                       (double (/ (.getHeight this) (:height context))))]
                              (dosync (ref-set zoom scale)))
                            (slide/current-slide context)
                            (.repaint this)))]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel)
      (.addComponentListener panel))))

(defn build-frame [panel]
  (doto (JFrame. "Scarecrow: Presentation with Clojure")
    (.add panel)
    (.pack)
    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
    (.setVisible true)))

(defn make-context [params]
  (let [context (build-context params)
        frame (-> context build-panel build-frame)]
    (map-set! context :frame frame)
    context))

(defn start [context slides]
  (map-set! context :slides slides)
  (.repaint @(:frame context)))
