(ns L5.core
  (:gen-class)
  (:use L5
        clojure.contrib.server-socket))

(def *server-socket* (create-repl-server 12345 25))

(defn -main [& args]
  (if (= "export" (first args))
    (export (or (second args) "run.clj"))
    (start (or (first args) "run.clj"))))
