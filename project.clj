(defproject L5 "1.0.1"
  :author "深町英太郎(E. Fukamachi)"
  :description "Yet Another Presentation with Clojure"
  :url "http://github.com/fukamachi/L5"
  :run-aliases {:presen [L5.core], :export [L5.export]}
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]]
  :dev-dependencies [[lein-run "1.0.0-SNAPSHOT"]
                     [jline/jline "0.9.94"]
                     [swank-clojure "1.2.0-SNAPSHOT"]
                     [lein-clojars "0.5.0"]]
  :repositries [["clojars" "http://clojars.org/repo"]])
