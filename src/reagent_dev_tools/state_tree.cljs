(ns reagent-dev-tools.state-tree
  (:require [reagent.core :as r]
            [reagent-dev-tools.state :as state]))

(defonce state-atoms (r/atom {}))

(defn- toggle [v ks open?]
  (if (or (not (get-in v ks))
          open?)
    (assoc-in v ks {})
    (assoc-in v ks nil)))

(defn- key->string [k]
  (cond
    (keyword? k)
    (let [s (namespace k)
          n (name k)]
      (str ":" (if s
                 (str s "/" n)
                 n)))

    (string? k)
    (str \" k \")

    :else
    k))

(defn type->class [v]
  (cond
    (keyword? v) "reagent-dev-tools__keyword"
    (string? v) "reagent-dev-tools__string"
    (number? v) "reagent-dev-tools__number"
    (nil? v) "reagent-dev-tools__nil"))

(defn collection-desc [v]
  (cond
    (map? v) (str "{" (count v) " keys}")
    (vector? v) (str "[" (count v) " items]")
    (set? v) (str "#{" (count v) " items}")
    (list? v) (str "(" (count v) " items)")) )

(defn- toggle-item [open open-fn v ks]
  [:span.reagent-dev-tools__li-toggle.reagent-dev-tools__li-toggle--active.reagent-dev-tools__pre
   {:title "Toggle collection items"
    :on-click (fn [_]
                ;; if one is closed, open all
                ;; else close all
                (let [open-all? (some nil? (vals open))]
                  (doseq [[k _] (if (map? v)
                                  v
                                  (zipmap (range) v))]
                    (open-fn (conj ks k) open-all?))))
    :on-mouse-down (fn [e]
                     ;; Disable text select after double click
                     (when (> (.-detail e) 1)
                       (.preventDefault e)))}
   (collection-desc v)])

(defn- tree [open open-fn v ks]
  (if (coll? v)
    [:ul.reagent-dev-tools__ul
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
          :class (when (coll? v)
                   "reagent-dev-tools__li-toggle--active")}
         (when (coll? v)
           [:span.reagent-dev-tools__li-toggle-icon
            (if open "-" "+")])
         [:strong
          {:class (type->class k)}
          (key->string k)]

         " "]
        (when (coll? v)
          [toggle-item open open-fn v ks])
        (when (or (not (coll? v)) open)
          [tree open open-fn v ks])])]

    [:pre.reagent-dev-tools__pre
     {:class (type->class v)}
     (pr-str v)]))

(defn state-tree-panel []
  [:div
   (doall
     (for [[name state-atom] @state-atoms
           :let [open (get-in @state/dev-state [:state-tree name :open])
                 open-fn (fn [ks open?]
                           (swap! state/dev-state update-in [:state-tree name :open] toggle ks open?))]]
       [:div {:key name}
        [:strong
         name
         (let [v @state-atom]
           [toggle-item open open-fn v []])]
        [tree
         open
         open-fn
         @state-atom
         []]]))])

(defn register-state-atom [atom-name state-atom]
  (swap! state-atoms assoc atom-name state-atom))
