(ns example.main
  (:require [devcards.core :as dc :refer-macros [defcard defcard-rg deftest]]
            [reagent.core :as r]
            [reagent-dev-tools.core :as dev-tools]
            [reagent-dev-tools.state-tree :as dev-state]))

(def state (r/atom {:hello "world"
                    :foo {:bar "bar"
                          :items [{:id "1" :name "a"}
                                  {:id "2" :name "b"}]}}))

(dev-state/register-state-atom "App" state)

(def users (r/atom [{:id "1" :name "a"}
                    {:id "2" :name "b"}]))

(dev-state/register-state-atom "Users" users)


(defcard-rg foo
  [:h1 "hello"])

(defn restart! []
  (dc/start-devcard-ui!)
  (r/render [dev-tools/dev-tool {}] (js/document.getElementById "dev")))

(restart!)
