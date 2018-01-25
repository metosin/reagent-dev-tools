(ns reagent-dev-tools.state-tree
  (:require [reagent-dev-tools.styles :as s]
            [reagent.core :as r]))

(defonce state-tree (r/atom {}))

(defn- toggle [v ks open?]
  (if (or (not (get-in v ks))
          open?)
    (assoc-in v ks {})
    (assoc-in v ks nil)))

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
         {:on-click #(open-fn ks false)
          :class (if (coll? v)
                   "reagent-dev-tools__li-toggle--active")}
         (if (coll? v)
           [:span.reagent-dev-tools__li-toggle-icon
            (if (get-in open ks) "-" "+")])
         [:strong
          {:class (type->class k)}
          (key->string k)]

         " "]
        [:span.reagent-dev-tools__li-toggle.reagent-dev-tools__li-toggle--active.reagent-dev-tools__pre
         {:on-click (fn [_]
                      (let [open-all? (some nil? (vals (get-in open ks)))]
                        (doseq [[k _] (if (map? v)
                                        v
                                        (zipmap (range) v))]
                          (open-fn (conj ks k) open-all?))))}
         (if (coll? v)
           (cond
             (map? v) "{}"
             (vector? v) "[]"
             (set? v) "#{}"
             (list? v) "()"))]
        (if (or (not (coll? v)) (get-in open ks))
          [tree open open-fn v ks])])]

    (nil? v)     [:i "nil"]
    :default     [:pre.reagent-dev-tools__pre
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
         (fn [ks open?]
           (swap! state-tree update-in [name :open] toggle ks open?))
         @atom
         []]]))])

(defn register-state-atom [atom-name atom]
  (swap! state-tree assoc-in [atom-name :atom] atom))
