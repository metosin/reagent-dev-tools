(ns reagent-dev-tools.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-dev-tools.styles :as s]))

(defn render-list [l]
  [:ul (for [x l]
         (if (coll? x)
           [:li (first x)
            [:ul (render-list (rest x))]]
           [:li x]))])

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

(defn- tree* [tree-atom tree-state v ks]
  (cond
    (coll? v)
    [:ul
     (for [[k v] (if (map? v)
                   v
                   (zipmap (range) v))
           :let [ks (conj ks k)]]
       [:li
        {:key (key->string k)
         :style s/li-style}
        [:span
         {:on-click #(swap! tree-atom toggle ks)
          :style {:cursor "pointer"}}
         [:span {:style {:display "inline-block"}}
          (if (coll? v)
            (if (get-in tree-state ks) "-" "+"))]
         [:strong (key->string k)]
         ": "]
        (if (or (not (coll? v)) (get-in tree-state ks))
          (tree* tree-atom tree-state v ks))])]

    (string? v) [:pre {:style s/pre-style} "\"" v "\""]
    (nil? v)    [:i "nil"]
    (fn? v) [:span "function"]
    :default [:span (pr-str v)]))

(defn tree [tree-atom tree-state v ks]
  [tree* tree-atom tree-state v ks])

(defn error-component [_ _]
  [:h2 "Error"])

(def panels
  {:state-tree
   {:label "State"
    :fn (fn [state app-state]
          [tree (reagent/cursor state [:state-tree-state]) (:state-tree-state @state) @app-state])}})

(defn panel [app-state dev-state]
  (let [open? (reagent/cursor dev-state [:open?])
        current (reagent/cursor dev-state [:current])]
    (fn []
      (if @open?
        (let [current-k       (or @current :state-tree)
              current-panel   (or (get panels @current) (:state-tree panels))
              current-content (:fn current-panel)]
          [:div {:style s/panel-style}
           [:ul {:style s/nav-style}
            (doall
              (for [[k {:keys [label]}] panels]
                ^{:key (name k)}
                [:li {:style s/nav-li-style}
                 [:a {:style (merge s/nav-li-a-style (if (keyword-identical? current-k k) s/active)) :on-click #(reset! current k)} label]]))
            [:li {:style s/pull-right}
             [:button
              {:onClick #(do (reset! open? false) nil)}
              [:span "×"]]]]
           (current-content dev-state app-state)])
        [:button
         {:onClick #(do (swap! open? not) nil)
          :style (merge s/pull-right s/toggle-btn-style)}
         "dev"]))))
