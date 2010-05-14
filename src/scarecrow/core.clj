(ns scarecrow.core
  (:use [scarecrow.context :only [make-context start]]
        [scarecrow.layout])
  (:require [scarecrow.slide :as s])
  (:import [java.awt Font]))

(def *context* (make-context {:width 640 :height 480
                              :font (Font. "VL Gothic" 0 40)}))

(defn -main []
  (start *context* slides))
