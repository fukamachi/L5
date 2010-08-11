(ns L5.core
  (:use [L5.context :only [make-context start]]
        L5.layout
        clojure.contrib.server-socket)
  (:require [L5.slide :as s])
  (:import [java.awt Font]))

(def *server-socket* (create-repl-server 12345 25))

(load-file "init.clj")

(defn -main []
  (start *context* slides))
