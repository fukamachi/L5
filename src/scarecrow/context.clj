(ns scarecrow.context
  (:require [scarecrow.slide :as slide])
  (:import [java.awt Color Dimension Font]
           [java.awt.image BufferedImage]
           [java.awt.event KeyListener KeyEvent ActionListener]
           [javax.swing JPanel JFrame]))

(def default
     {:width 640
      :height 480
      :padding [15 15 15 15]
      :font (Font. "VL Gothic" 0 20)})

(defn dispatch-event [context keyCode]
  (cond
   (or (= keyCode KeyEvent/VK_BACK_SPACE)
       (= keyCode KeyEvent/VK_LEFT)) (slide/prev-slide context)
   (or (= keyCode KeyEvent/VK_ENTER)
       (= keyCode KeyEvent/VK_SPACE)
       (= keyCode KeyEvent/VK_RIGHT)) (slide/next-slide context)))

(defn add-panel [context]
  (let [buf (BufferedImage. (:width context)
                            (:height context)
                            BufferedImage/TYPE_4BYTE_ABGR)
        panel
        (proxy [JPanel KeyListener] []
          (getPreferredSize [] (Dimension. (.getWidth buf) (.getHeight buf)))
          (keyPressed [e] (dispatch-event context (.getKeyCode e)))
          (keyReleased [e])
          (keyTyped [e]))]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel))
    (dosync (ref-set (:panel context) panel))))

(defn make-context [params & slides]
  (let [width (:width params)
        height (:height params)
        params (merge default params)]
    {:panel (ref nil)
     :slides (ref (or slides []))
     :current (ref 0)
     :width (:width params)
     :height (:height params)
     :padding (:padding params)
     :font (:font params)}))

(defn context-set! [context key val]
  (dosync (ref-set (get context key) val)))

(defn start [context slides]
  (context-set! context :slides slides)
  (slide/current-slide context))
