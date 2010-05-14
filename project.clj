(defproject scarecrow "1.0.0-SNAPSHOT"
  :author "深町英太郎(E. Fukamachi)"
  :description "Yet Another Presentation with Clojure"
  :url "http://github.com/fukamachi/scarecrow"
  :run-aliases {:presen [scarecrow.core]}
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.0-SNAPSHOT"]
                     [lein-clojars "0.5.0"]
                     [lein-run "1.0.0-SNAPSHOT"]]
  :repositries [["clojars" "http://clojars.org/repo"]])
