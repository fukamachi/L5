(ns L5.core
  (:use [L5.context :only [make-context start]]
        clojure.contrib.server-socket))

(def *server-socket* (create-repl-server 12345 25))

(def *context* (ref nil))

(defmacro defcontext [params]
  `(if (not (deref ~'*context*))
     (dosync (ref-set ~'*context* (make-context ~params)))))

(defmacro defslides [& slides]
  `(dosync (ref-set (:slides (deref ~'*context*)) [~@slides])))

(defn -main []
  (load-file "init.clj")
  (start @*context*))
