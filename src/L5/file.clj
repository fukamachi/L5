(ns L5.file
  (:require [clojure.contrib.io :as io])
  (:import [java.io File]
           [javax.swing JFrame JPanel JLabel JButton JFileChooser SwingConstants]
           [javax.swing.filechooser FileNameExtensionFilter]
           [java.awt GridLayout]
           [java.awt.event ActionListener]))

(def presen-filter (FileNameExtensionFilter. "Presentation (.clj .lgo)" (into-array ["clj" "lgo"])))
(def pdf-filter (FileNameExtensionFilter. "PDF (.pdf)" (into-array ["pdf"])))

(defn- file-dialog [action parent callback filter]
  (let [chooser (JFileChooser.)]
    (.setCurrentDirectory chooser (.. (File. ".") getAbsoluteFile getParentFile))
    (.setFileFilter chooser filter)
    (when (= JFileChooser/APPROVE_OPTION
             (if (= :save action)
               (.showSaveDialog chooser parent)
               (.showOpenDialog chooser parent)))
      (callback (.. chooser getSelectedFile getPath)))))

(defn choose-dialog [parent callback]
  (file-dialog :open parent callback
               (FileNameExtensionFilter. "Presentation (.clj .lgo)" (into-array ["clj" "lgo"]))))

(defn save-dialog [parent callback filter]
  (file-dialog :save parent callback filter))

(defn open-file [file]
  (try
   ;; open with the specifiled program
   (.open (java.awt.Desktop/getDesktop) file)
   (catch java.io.IOException e
     ;; open with $EDITOR
     (let [editor (System/getenv "EDITOR")]
       (when editor
         (.exec (Runtime/getRuntime)
                (format "%s %s" editor (.getAbsolutePath file))))))))

(defn- get-create-button [frame]
  (let [button (JButton. "Create")
        action (proxy [ActionListener] []
                 (actionPerformed [e]
                   (save-dialog frame
                                #(let [file (File. %)]
                                   (io/copy (File. "skelton.clj") file)
                                   (open-file file))
                                presen-filter)))]
    (.addActionListener button action)
    button))

(defn open-chooser [name callback]
  (let [frame (JFrame. name)
        panel (JPanel. (GridLayout. 4 1))
        open-action (proxy [ActionListener] []
                        (actionPerformed [e]
                          (choose-dialog frame callback)))]
    (let [create-button (get-create-button frame)
          open-button (JButton. "Open")]
      (.addActionListener open-button open-action)
      (doto panel
        (.add (JLabel. "<html><p style=\"font-weight: bold; padding-left: 20px;\">Welcome to L5!</p></html>"))
        (.add create-button)
        (.add open-button)
        (.add (doto (JLabel. "Copyright (c) 2010 深町英太郎")
                (.setHorizontalAlignment SwingConstants/CENTER)))))
    (doto frame
      (.add panel)
      (.setSize 250 180)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))))
