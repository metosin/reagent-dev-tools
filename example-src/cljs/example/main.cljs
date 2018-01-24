(ns example.main
  (:require [devcards.core :as dc :refer-macros [defcard defcard-rg deftest]]
            [reagent.core :as r]
            [reagent-dev-tools.core :as dev-tools]
            [reagent-dev-tools.state-tree :as dev-state]
            [reagent-dev-tools.preload :as dev-tools-preload]))

(def state (r/atom {:hello "world"
                    :foo {:bar "bar"
                          :items [{:id "1" :name "a"}
                                  {:id "2" :name "b"}]}}))

(def users (r/atom [{:id "1" :name "a"}
                    {:id "2" :name "b"}]))

(dev-state/register-state-atom "Users" users)

(defcard-rg foo
  [:h1 "hello"])

(defn dev-panels []
  {:example {:label "Example panel"
             :fn (fn []
                   [:div [:h1 "Hello"]])}})

(defn restart! []
  (dc/start-devcard-ui!)
  (dev-tools-preload/start))

(restart!)
