(ns reagent-dev-tools.context
  (:require ["react" :as react]))

(defonce panel-context (react/createContext nil))

(def panel-context-provider (.-Provider panel-context))
(def panel-context-consumer (.-Consumer panel-context))
