(ns scarecrow.core
  (:use [scarecrow.context :only [make-context start]]
        [scarecrow.layout])
  (:require [scarecrow.slide :as s])
  (:import [java.awt Font]))

(load-file "sample.clj")

(defn -main []
  (start *context* slides))
