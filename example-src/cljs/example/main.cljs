(ns example.main
  (:require [reagent.core :as r]
            [reagent.dom :as r-dom]
            [reagent-dev-tools.core :as dev-tools]
            [reagent-dev-tools.state-tree :as dev-state]
            [reagent-dev-tools.state :as rdt-state]))

(def state
  (r/atom {:hello "world"
           :fn-test (fn foo-name [])
           :anon-fn #(constantly nil)
           :number 1337
           :kw :namespace/keyword
           :nil nil
           :foo {:bar "bar"
                 :items [{:id "1" :name "a"}
                         {:id "2" :name "b"}]
                 "items" {:string-key 1}
                 nil {:nil-key 2}
                 [1 2 :foo] {:vector-key 3}
                 (js/Symbol "symbol") {:js-symbol-key 4}}}))

(def users (r/atom [{:id "1" :name "a"}
                    {:id "2" :name "b"}]))

(dev-state/register-state-atom "Users" users)
(dev-state/register-state-atom "Dev tools state" rdt-state/dev-state)

(defn dev-panels []
  {:example {:label "Example panel"
             :fn (fn []
                   [:div [:h1 "Hello"]])}})

(defn main []
  [:h1 "hello"] )

(defn restart! []
  (r-dom/render [main] (.getElementById js/document "app"))
  (dev-tools/start! {:margin-element js/document.body}))

(restart!)
