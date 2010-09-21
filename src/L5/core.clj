(ns L5.core
  (:gen-class)
  (:use L5))

(defn -main [& args]
  (if (first args)
    (start (first args))
    (select-file))
  (println "Welcome to L5!"))
