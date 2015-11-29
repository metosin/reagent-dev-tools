(set-env!
  :source-paths #{"example-src/cljs" "example-src/html"}
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure    "1.7.0"      :scope "provided"]
                  [org.clojure/clojurescript "1.7.170" :scope "provided"]
                  [boot/core              "2.4.2"      :scope "test"]
                  [adzerk/boot-cljs       "1.7.170-3"  :scope "test"]
                  [adzerk/boot-cljs-repl  "0.3.0"      :scope "test"]
                  [com.cemerick/piggieback "0.2.1"     :scope "test"]
                  [weasel                  "0.7.0"     :scope "test"]
                  [org.clojure/tools.nrepl "0.2.12"    :scope "test"]
                  [adzerk/boot-reload     "0.4.2"      :scope "test"]
                  [pandeiro/boot-http     "0.7.0"      :scope "test"]
                  [devcards               "0.2.0-8"    :scope "test"]

                  [reagent "0.5.1"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[pandeiro.boot-http    :refer [serve]])

(def +version+ "0.1.0")

(task-options!
  pom {:project 'metosin/reagent-dev-tools
       :version +version+
       :description "Reagent dev tools"
       :license {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}}
  cljs {:source-map true})

(deftask dev []
  (comp
    (watch)
    (reload :on-jsload 'example.main/restart!)
    (cljs-repl)
    (cljs)
    (serve :port 3002 :resource-root "")))

(deftask build-example []
  (comp
    (cljs :optimizations :advanced)
    (sift :to-resource #{#"^index\.html"})
    (sift :include #{#"^(main.js|example.css|index.html)"})))

(deftask build []
  (comp
    (pom)
    (jar)
    (install)))

(deftask deploy []
  (comp
    (build)
    (push :gpg-sign (not (.endsWith +version+ "-SNAPSHOT"))
          :repo "clojars"
          :repo-map {:username :gpg :password :gpg})))
