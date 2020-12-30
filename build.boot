(set-env!
  :source-paths #{"example-src/cljs" "example-src/html"}
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.10.1" :scope "provided"]
                  [org.clojure/clojurescript "1.10.764" :scope "provided"]
                  [adzerk/boot-cljs "2.1.5" :scope "test"]
                  [adzerk/boot-reload "0.6.0" :scope "test"]
                  [metosin/boot-alt-http "0.2.0" :scope "test"]

                  ;; No need to depend on Reagent, it is presumed
                  ;; the application provides it.
                  [reagent "1.0.0" :scope "provided"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-reload    :refer [reload]]
  '[metosin.boot-alt-http :refer [serve]])

(def +version+ "0.3.1")

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
    (repl :server true)
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
