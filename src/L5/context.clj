(ns L5.context
  (:require [L5.slide :as slide])
  (:import [java.awt Dimension Font]
           [java.awt.event KeyListener KeyEvent ComponentListener]
           [javax.swing JPanel JFrame]))

(defn map-set! [obj-map key val]
  (dosync (ref-set (get obj-map key) val)))

(def default
     {:width 640
      :height 480
      :padding {:top 20, :right 20, :bottom 20, :left 20}
      :font (Font. "VL Gothic" 0 20)})

(defn dispatch-event [context keyCode]
  (let [actions (get @(:actions context) keyCode)]
    (when (not (empty? actions))
      (doseq [act actions] (act))
      (.repaint @(:frame context)))))

(defn build-context [params & slides]
  (let [params (merge default params)
        context {:g (ref nil)
                 :frame (ref nil)
                 :slides (ref (or slides []))
                 :background-image (:background-image params)
                 :color (:color params)
                 :background-color (:background-color params)
                 :current (ref 0)
                 :width (:width params)
                 :height (:height params)
                 :padding (:padding params)
                 :font (:font params)
                 :actions (ref nil)}]
      (dosync (ref-set (:actions context)
                       { KeyEvent/VK_F5         [#(slide/toggle-fullscreen context)]
                         KeyEvent/VK_ESCAPE     [#(slide/fullscreen-off context)]
                         KeyEvent/VK_R          [#(load-file "init.clj")]
                         KeyEvent/VK_BACK_SPACE [#(slide/prev-slide context)]
                         KeyEvent/VK_LEFT       [#(slide/prev-slide context)]
                         KeyEvent/VK_ENTER      [#(slide/next-slide context)]
                         KeyEvent/VK_SPACE      [#(slide/next-slide context)]
                         KeyEvent/VK_RIGHT      [#(slide/next-slide context)] }))
      context))

(defn build-panel [context]
  (let [zoom (ref 1.0)
        x (ref 0)
        y (ref 0)
        panel
        (proxy [JPanel KeyListener ComponentListener] []
          (getPreferredSize []
                            (Dimension.
                             (* @zoom (:width context))
                             (* @zoom (:height context))))
          (paintComponent [g]
                          (when (:g context)
                            (proxy-super paintComponent g)
                            (let [img (:background-image context)]
                              (when img
                                (let [scale-width (/ (.getWidth this) (.getWidth img))
                                      scale-height (/ (.getHeight this) (.getHeight img))
                                      scale (max scale-width scale-height)]
                                  ; TODO: refactoring
                                  (.drawImage g img 0 0 (* scale (.getWidth img)) (* scale (.getHeight img)) nil))))
                            (.scale g @zoom @zoom)
                            (.translate g @x @y)
                            (map-set! context :g g)
                            (slide/current-slide context)))
          (keyPressed [e] (dispatch-event context (.getKeyCode e)))
          (keyReleased [e])
          (keyTyped [e])
          (componentResized [e]
                            (let [width (.getWidth this)
                                  height (.getHeight this)
                                  scale (min (double (/ width (:width context)))
                                             (double (/ height (:height context))))
                                  width-diff (- width (* scale (:width context)))
                                  height-diff (- height (* scale (:height context)))]
                              (dosync
                               (ref-set zoom scale)
                               (ref-set x (/ width-diff 2 scale))
                               (ref-set y (/ height-diff 2 scale))))
                            (slide/current-slide context)))]
    (doto panel
      (.setFocusable true)
      (.setForeground (:color context))
      (.setBackground (:background-color context))
      (.addKeyListener panel)
      (.addComponentListener panel))))

(defn build-frame [panel]
  (doto (JFrame. "L5: Presentation with Clojure")
    (.add panel)
    (.pack)
    (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)))

(defn make-context [params]
  (let [context (build-context params)
        frame (-> context build-panel build-frame)]
    (map-set! context :frame frame)
    context))

(defn start [context]
  (doto @(:frame context)
    (.repaint)
    (.setVisible true)))
