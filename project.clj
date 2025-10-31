(defproject metosin/reagent-dev-tools "1.1.0"
  ;; :url ""
  :license {:name "Eclipse Public License"}
  :description "Reagent dev tools"

  :dependencies []

  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-doo "0.1.11"]
            [lein-codox "0.10.8"]
            [lein-figwheel "0.5.20"]]

  :source-paths ["src"]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.12.3"]
                                  [org.clojure/clojurescript "1.11.132"]
                                  [figwheel "0.5.20"]
                                  [figwheel-sidecar "0.5.20"]

                                  [reagent "2.0.0"]

                                  [frankiesardo/linked "1.3.0" :scope "test"]
                                  [cljsjs/react "18.3.1-1"]
                                  [cljsjs/react-dom "18.3.1-1"]]
                   :source-paths ["example-src/cljs"]
                   :resource-paths ["example-src/html" "target/cljsbuild/client"]}}

  :clean-targets ^{:protect false} [:target-path :compile-path "out"]

  :repl-options {:init (require '[figwheel-sidecar.repl-api :refer :all])}

  :figwheel {:http-server-root "public" ;; assumes "resources"
             :css-dirs ["site/public/css"]
             :repl true
             :nrepl-port 27397}

  :cljsbuild
  {:builds
   [{:id "client"
     :source-paths ["example-src/cljs"]
     :watch-paths ["src" "example-src/cljs"]
     :figwheel true
     :compiler {:parallel-build true
                :optimizations :none
                :main "example.main"
                :output-dir "target/cljsbuild/client/public/out"
                :output-to "target/cljsbuild/client/public/main.js"
                :npm-deps false
                :asset-path "out"
                :checked-arrays :warn
                :infer-externs true}}]})

