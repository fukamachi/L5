(ns L5.core
  (:gen-class)
  (:use L5
        clojure.contrib.server-socket))

(def *server-socket* (create-repl-server 12345 25))

(defn -main [& [cmd]]
  (if (= cmd "export")
    (export)
    (start)))
