(defproject dictionator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [ring/ring-core "1.4.0"]
                 [ring-transit "0.1.4" :exclusions [[com.cognitect/transit-clj]]]
                 [http-kit "2.1.19"]
                 [cheshire "5.5.0"]
                 [bidi "1.21.1"]
                 [com.stuartsierra/component "0.3.0"]
                 [com.cognitect/transit-clj "0.8.285"]
                 [com.cognitect/transit-cljs "0.8.232"]
                 [org.omcljs/om "1.0.0-alpha25"]]
  :source-paths ["src/clj"]
  :plugins [[lein-figwheel "0.5.0-2"]]
  :cljsbuild [{:id "dev"
               :source-paths ["src/cljs"]
               :figwheel true
               :compiler {:main "dictionator.core"
                          :asset-path "js/out"
                          :output-to "resources/public/js/main.js"
                          :output-dir "resources/public/js/out"}}]
  :main ^:skip-aot dictionator.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:source-paths ["src/dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :repl-options {:init-ns dictionator.dev}}})
