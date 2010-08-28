(ns L5.core
  (:use [L5.context :only [start]]
        L5
        clojure.contrib.server-socket))

(def *server-socket* (create-repl-server 12345 25))

(defn -main []
  (load-file "run.clj")
  (start @L5/*context*))
