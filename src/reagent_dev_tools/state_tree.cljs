(ns reagent-dev-tools.state-tree
  (:require [reagent-dev-tools.styles :as s]
            [reagent.core :as r]))

;; TODO: Move to panel state, so open is saved
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
    (number? v) "reagent-dev-tools__number"
    (nil? v) "reagent-dev-tools__nil"))

(defn collection-name [v]
  (cond
    (map? v) "{}"
    (vector? v) "[]"
    (set? v) "#{}"
    (list? v) "()") )

(defn- tree [open open-fn v ks]
  (if (coll? v)
    [:ul
     (for [[k v] (if (map? v)
                   v
                   (zipmap (range) v))
           :let [open (get open k)
                 ks (conj ks k)]]
       [:li.reagent-dev-tools__li
        {:key (key->string k)}
        [:span.reagent-dev-tools__li-toggle
         {:on-click #(open-fn ks false)
          :title "Toggle this collection"
          :class (if (coll? v)
                   "reagent-dev-tools__li-toggle--active")}
         (if (coll? v)
           [:span.reagent-dev-tools__li-toggle-icon
            (if open "-" "+")])
         [:strong
          {:class (type->class k)}
          (key->string k)]

         " "]
        (if (coll? v)
          [:span.reagent-dev-tools__li-toggle.reagent-dev-tools__li-toggle--active.reagent-dev-tools__pre
           {:title "Toggle collection items"
            :on-click (fn [_]
                        ;; if one is closed, open all
                        ;; else close all
                        (let [open-all? (some nil? (vals open))]
                          (doseq [[k _] (if (map? v)
                                          v
                                          (zipmap (range) v))]
                            (open-fn (conj ks k) open-all?))))}
           (collection-name v)])
        (if (or (not (coll? v)) open)
          [tree open open-fn v ks])])]

    [:pre.reagent-dev-tools__pre
     {:class (type->class v)}
     (pr-str v)]))

(defn state-tree-panel []
  [:div
   (doall
     (for [[name {:keys [state-atom open]}] @state-tree]
       [:div {:key name}
        [:strong name]
        [tree
         open
         (fn [ks open?]
           (swap! state-tree update-in [name :open] toggle ks open?))
         @state-atom
         []]]))])

(defn register-state-atom [atom-name state-atom]
  (swap! state-tree assoc-in [atom-name :state-atom] state-atom))
