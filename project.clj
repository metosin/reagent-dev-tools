(defproject metosin/reagent-dev-tools "0.1.1-SNAPSHOT"
  :description "Reagent dev tools"
  :url "https://github.com/metosin/reagent-dev-tools"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src"]
  :dependencies [[reagent "0.5.1"]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0" :scope "provided"]
                                  [org.clojure/clojurescript "1.7.48" :scope "provided"]]}})
