(ns L5.file
  (:import [java.io File]
           [javax.swing JFrame JPanel JLabel JButton JFileChooser SwingConstants]
           [javax.swing.filechooser FileNameExtensionFilter]
           [java.awt GridLayout]
           [java.awt.event ActionListener]))

(defn- file-dialog [action parent callback filter]
  (let [chooser (JFileChooser.)]
    (.setCurrentDirectory chooser (File. (System/getProperty "user.home")))
    (.setFileFilter chooser filter)
    (when (= JFileChooser/APPROVE_OPTION
             (if (= :save action)
               (.showSaveDialog chooser parent)
               (.showOpenDialog chooser parent)))
      (callback (.. chooser getSelectedFile getPath)))))

(defn choose-dialog [parent callback]
  (file-dialog :open parent callback
               (FileNameExtensionFilter. "Presentation (.clj .lgo)" (into-array ["clj" "lgo"]))))

(defn save-dialog [parent callback]
  (file-dialog :save parent callback
               (FileNameExtensionFilter. "PDF (.pdf)" (into-array ["pdf"]))))

(defn open-chooser [name callback]
  (let [frame (JFrame. name)
        panel (JPanel. (GridLayout. 3 1))
        button-action (proxy [ActionListener] []
                        (actionPerformed [e]
                          (choose-dialog frame callback)))]
    (let [button (JButton. "Open")]
      (.addActionListener button button-action)
      (doto panel
        (.add (JLabel. "<html><p style=\"font-weight: bold; padding-left: 20px;\">Welcome to L5!</p></html>"))
        (.add button)
        (.add (doto (JLabel. "Copyright (c) 2010 深町英太郎")
                (.setHorizontalAlignment SwingConstants/CENTER)))))
    (doto frame
      (.add panel)
      (.setSize 250 150)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))))
