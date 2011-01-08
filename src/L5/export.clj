(ns L5.export
  (:require [L5.slide :as slide])
  (:import [java.io FileOutputStream]
           [com.itextpdf.text Document Rectangle]
           [com.itextpdf.text.pdf PdfWriter]
           [javax.swing JDialog JProgressBar]))

;; NOTE: Sometimes Progress bar is captured in the exported PDF.
;;   I'll disable it currentry.
;;   See <https://github.com/fukamachi/L5/issues#issue/27> for detail.
(defn jframe->pdf [filename context]
  (let [len (count @(:slides context))
        ;dialog (doto (JDialog. @(:frame context) "Exporting.." true) (.setSize 300 50))
        ;progress (doto (JProgressBar. 0 len) (.setStringPainted true))
        {width :width height :height} context
        doc (Document. (Rectangle. width (- height 22)))
        writer (PdfWriter/getInstance doc (FileOutputStream. filename))]
    ;(.add dialog progress)
    (.start
     (Thread.
      #(do
         (.open doc)
         (let [cb (.getDirectContent writer)]
          (dotimes [i len]
            (let [tp (.createTemplate cb width height)
                  g2d (.createGraphics tp width height)]
              (.update @(:frame context) g2d)
              (.dispose g2d)
              (.addTemplate cb tp 0 0))
            (slide/next-slide context)
            ;(.setValue progress (+ i 1))
            ;(.setString progress (str (int (* (/ (inc i) len) 100)) "%"))
            (.newPage doc)))
         (.close doc)
         ;(.setVisible dialog false)
         )))
    ;(.setVisible dialog true)
    ))
