(set-env!
  :source-paths #{"example-src/cljs" "example-src/html"}
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure    "1.9.0"      :scope "provided"]
                  [org.clojure/clojurescript "1.9.946" :scope "provided"]
                  [adzerk/boot-cljs       "2.1.4"  :scope "test"]
                  [adzerk/boot-cljs-repl  "0.3.3"      :scope "test"]
                  [com.cemerick/piggieback "0.2.2"     :scope "test"]
                  [weasel                  "0.7.0"     :scope "test"]
                  [org.clojure/tools.nrepl "0.2.13"    :scope "test"]
                  [adzerk/boot-reload     "0.5.2"     :scope "test"]
                  [devcards               "0.2.4" :scope "test"
                   :exclusions [cljsjs/react cljsjs/react-dom]]
                  [metosin/boot-alt-http "0.2.0" :scope "test"]

                  [reagent "0.9.1"]
                  [metosin/komponentit "0.3.9"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[metosin.boot-alt-http :refer [serve]])

(def +version+ "0.3.0-SNAPSHOT")

(task-options!
  pom {:project 'metosin/reagent-dev-tools
       :version +version+
       :description "Reagent dev tools"
       :license {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}}
  cljs {:source-map true})

(deftask dev []
  (comp
    (watch)
    (reload :on-jsload 'example.main/restart!
            :asset-path "public")
    (cljs-repl)
    (cljs)
    (serve :port 3002)))

(deftask build []
  (comp
    (pom)
    (jar)
    (install)))

(deftask deploy []
  (comp
    (build)
    (push :gpg-sign (not (.endsWith +version+ "-SNAPSHOT"))
          :repo "clojars")))
