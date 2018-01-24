(ns reagent-dev-tools.preload
  (:require-macros [reagent-dev-tools.preload :refer [read-config]])
  (:require [reagent-dev-tools.core :as core]
            [reagent-dev-tools.state-tree :as state-tree]))

;; Wait until JS is loaded, because config will refer to namespaces
;; that will be loaded after this preload ns.
;; Those namesapces will be available after document is "interactive".
;; Start right-away, if document is already loaded.

(defn start []
  (let [opts (read-config)]
    (core/start! opts)))

(if (#{"interactive" "complete"} (.. js/document -readyState))
  (start)
  (.addEventListener js/document "DOMContentLoaded" #(start)))
