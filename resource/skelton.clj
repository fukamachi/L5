(ns L5.presentation
  (:use L5 L5.layout))

(defcontext
  {:width 640 :height 480
   :font-family "Gill Sans"
   :font-size 30})

(defslides
  ;; Title page
  [(t "タイトルを入力")
   (with {:font-size 15
          :position :fixed
          :padding {:top 360}}
     (lines "アリエル・ネットワーク"
            "深町英太郎"))]
  ;; Itemize slide
  [(title "タイトルを入力")
   (item "箇条書き1"
         "箇条書き2")]
  ;; Extra large letters
  [(t "入力")])

(start)
