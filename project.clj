(defproject clj-tetris "0.1.0-SNAPSHOT"
  :description "Clojure implementation of the Tetris game"
  :license {:name "MIT licence"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :main ^:skip-aot clj-tetris.swing
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
