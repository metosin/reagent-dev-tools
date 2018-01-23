(ns reagent-dev-tools.core
  (:require [reagent.core :as r]
            [reagent-dev-tools.styles :as s]
            [reagent-dev-tools.state-tree :refer [state-tree-panel]]))

(defonce dev-state (r/atom {:height 300}))

(def default-panels
  {:state-tree {:label "State"
                :fn state-tree-panel}})

(defn dev-tool
  [{:keys [panels]
    :or {panels default-panels}}]
  (if (:open? @dev-state)
    (let [current-k       (:current @dev-state :state-tree)
          current-panel   (or (get panels current-k) (:state-tree panels))
          current-content (:fn current-panel)]
      [:div {:style (merge
                      s/panel-style
                      {:height (str (:height @dev-state) "px")})}
       [:div {:style s/nav-style}
        (for [[k {:keys [label]}] panels]
          [:div
           {:key (name k)
            :style s/nav-li-style}
           [:a {:style (merge
                         s/nav-li-a-style
                         (if (keyword-identical? current-k k) s/active))
                :on-click #(swap! dev-state assoc :current k)}
            label]])
        [:div {:style {:flex "1 0 0%"}}]
        [:button
         {:style (assoc s/nav-li-a-style :background "#fff")
          :on-click (fn []
                      (swap! dev-state assoc :open? false)
                      nil)}
         [:span "×"]]]
       [:div
        {:style s/panel-content}
        (if current-content [current-content])]])
    [:button
     {:onClick (fn [_]
                 (swap! dev-state assoc :open? true)
                 nil)
      :style (merge s/pull-right s/toggle-btn-style)}
     "dev"]))
