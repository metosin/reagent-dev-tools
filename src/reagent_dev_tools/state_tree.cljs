(ns reagent-dev-tools.state-tree
  (:require [reagent-dev-tools.styles :as s]
            [reagent.core :as r]))

(defonce state-tree (r/atom {}))

(defn- toggle [v ks]
  (if (get-in v ks)
    (assoc-in v ks nil)
    (assoc-in v ks {})))

(defn- key->string [k]
  (if (keyword? k)
    (let [s (namespace k)
          n (name k)]
      (str ":" (if s
                 (str s "/" n)
                 n)))
    k))

(defn type->class [v]
  (cond
    (keyword? v) "reagent-dev-tools__keyword"
    (string? v) "reagent-dev-tools__string"
    (number? v) "reagent-dev-tools__number"))

(defn- tree [open open-fn v ks]
  (cond
    (coll? v)
    [:ul
     (for [[k v] (if (map? v)
                   v
                   (zipmap (range) v))
           :let [ks (conj ks k)]]
       [:li.reagent-dev-tools__li
        {:key (key->string k)}
        [:span.reagent-dev-tools__li-toggle
         {:on-click #(open-fn ks)
          :class (if (coll? v)
                   "reagent-dev-tools__li-toggle--active")}
         (if (coll? v)
           [:span.reagent-dev-tools__li-toggle-icon
            (if (get-in open ks) "-" "+")])
         [:strong
          {:class (type->class k)}
          (key->string k)]
         " "]
        (if (or (not (coll? v)) (get-in open ks))
          [tree open open-fn v ks])])]

    (string? v) [:pre.reagent-dev-tools__pre.reagent-dev-tools__string
                 "\"" v "\""]
    (nil? v)    [:i "nil"]
    (fn? v)     [:span "function"]
    :default    [:span
                 {:class (type->class v)}
                 (pr-str v)]))

(defn state-tree-panel []
  [:div
   (doall
     (for [[name {:keys [atom open]}] @state-tree]
       [:div {:key name}
        [:strong name]
        [tree
         open
         (fn [ks]
           (swap! state-tree update-in [name :open] toggle ks))
         @atom
         []]]))])

(defn register-state-atom [atom-name atom]
  (swap! state-tree assoc-in [atom-name :atom] atom))
