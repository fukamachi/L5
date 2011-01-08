(ns L5.core
  (:gen-class)
  (:use L5))

(defn -main [& args]
  (println "Welcome to L5!")
  (if (first args)
    (start (first args))
    (select-file))
  nil)
