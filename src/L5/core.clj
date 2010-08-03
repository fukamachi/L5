(ns L5.core
  (:use [L5.context :only [make-context start]]
        [L5.layout])
  (:require [L5.slide :as s])
  (:import [java.awt Font]))

(load-file "init.clj")

(defn -main []
  (start *context* slides))
