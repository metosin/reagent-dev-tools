(ns example.core
  (:require [devcards.core :as dc :refer-macros [defcard defcard-rg deftest]]
            [reagent.core :as r]
            [reagent-dev-tools.core :as dev-tools]))

(enable-console-print!)

(def state (r/atom {:hello "world"
                    :foo "bar"}))

(def dev-state (r/atom nil))

(defcard-rg foo
  "Reagent dev tools"
  [dev-tools/panel state dev-state])
