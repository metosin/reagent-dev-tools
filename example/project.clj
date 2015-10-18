(defproject example "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [devcards "0.2.0-7"]
                 [reagent "0.5.1"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.4.1"]]

  :source-paths ["src"]

  :cljsbuild {:builds [{:id "devcards"
                        :source-paths ["src" "checkouts/reagent-dev-tools/src"]
                        :figwheel {:devcards true}
                        :compiler {:main       "example.main"
                                   :asset-path "out"
                                   :output-to  "target/generated/cljs-dev/public/main.js"
                                   :output-dir "target/generated/cljs-dev/public/out"
                                   :source-map-timestamp true}}
                       {:id "prod"
                        :source-paths ["src" "checkouts/reagent-dev-tools/src"]
                        :compiler {:main       "example.main"
                                   :asset-path "out"
                                   :output-to  "target/generated/cljs-prod/public/main.js"
                                   :optimizations :advanced}}]}

  :figwheel {:http-server-root "public"
             :css-dirs []
             :repl false}

  :profiles {:dev {:resource-paths ["target/generated/cljs-dev"]}})
