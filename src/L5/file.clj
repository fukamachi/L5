(ns L5.file
  (:import [javax.swing JFrame JLabel JButton JSplitPane JFileChooser]
           [java.io File]
           [java.awt.event ActionListener]))

(defn- file-dialog [action parent callback]
  (let [chooser (JFileChooser.)]
    (.setCurrentDirectory chooser (File. (System/getProperty "user.home")))
    (when (= JFileChooser/APPROVE_OPTION
             (if (= :save action)
               (.showSaveDialog chooser parent)
               (.showOpenDialog chooser parent)))
      (callback (.. chooser getSelectedFile getPath)))))

(defn choose-dialog [parent callback]
  (file-dialog :open parent callback))

(defn save-dialog [parent callback]
  (file-dialog :save parent callback))

(defn open-chooser [name callback]
  (let [frame (JFrame. name)
        split-pane (JSplitPane. JSplitPane/VERTICAL_SPLIT)
        button-action (proxy [ActionListener] []
                        (actionPerformed [e]
                          (choose-dialog frame callback)))]
    (let [button (JButton. "Open presentation")]
      (.addActionListener button button-action)
      (doto split-pane
        (.add (JLabel. "Welcome to L5"))
        (.add button)))
    (doto frame
      (.add split-pane)
      (.setSize 200 200)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))))
