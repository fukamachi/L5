(ns L5.core
  (:gen-class)
  (:use L5))

(defn -main [& args]
  (if (= "export" (first args))
    (export (or (second args) "run.clj"))
    (start (or (first args) "run.clj"))))
