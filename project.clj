(defproject L5 "1.2.0-SNAPSHOT"
  :author "深町英太郎(E. Fukamachi)"
  :description "Yet Another Presentation with Clojure"
  :url "http://github.com/fukamachi/L5"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"
            :distribution :repo}
  :main L5.core
  :run-aliases {:presen [L5.core -main]}
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [com.itextpdf/itextpdf "5.0.4"
                  :exclusions [org.bouncycastle/bcmail-jdk14
                               org.bouncycastle/bcprov-jdk14
                               org.bouncycastle/bctsp-jdk14]]]
  :dev-dependencies [[lein-run "1.0.0-SNAPSHOT"]
                     [lein-clojars "0.5.0"]]
  :repositories {"clojars" "http://clojars.org/repo"
                 "itextpdf" "http://maven.itextpdf.com/"}
  :uberjar-name "L5.jar")
