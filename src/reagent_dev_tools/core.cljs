(ns reagent-dev-tools.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [reagent-dev-tools.styles :as s]
            [reagent-dev-tools.state-tree :as state-tree]
            [reagent-dev-tools.state :as state]
            [reagent-dev-tools.utils :refer [window-event-listener]]))

;; Save the state (open, height, active panel) to local storage

(def element-id (str ::dev-panel))

(def default-panels
  {:state-tree {:label "State"
                :fn state-tree/state-tree-panel}})

(def dev-state state/dev-state)

(defn dev-tool
  [{:keys [panels]
    :or {panels default-panels}}]
  (let [mouse-state (r/atom nil)]
    (fn [{:keys [panels margin-element]
          :or {panels default-panels}}]
      (let [{:keys [open? place width height]} @dev-state]
        (when margin-element
          (set! (.. margin-element -style -marginRight) (when (and open? (= :right place))
                                                          (str width "px")))

          (set! (.. margin-element -style -marginBottom) (when (and open? (= :bottom place))
                                                           (str height "px"))))
        [:<>
         [:style (s/main-css)]
         (if open?
           (let [current-k       (:current @dev-state :state-tree)
                 current-panel   (or (get panels current-k) (:state-tree panels))
                 current-content (:fn current-panel)]
             [window-event-listener
              {:on-mouse-move (when @mouse-state
                                (fn [e]
                                  (.preventDefault e)
                                  (swap! dev-state (fn [v]
                                                     (case place
                                                       :right  (assoc v :width (-> (- (.-innerWidth js/window) (.-clientX e))
                                                                                   (max 50)
                                                                                   (min 1000)))
                                                       ;; Bottom
                                                       (assoc v :height (-> (- (.-innerHeight js/window) (.-clientY e))
                                                                            (max 50)
                                                                            (min 1000))))))))
               :on-mouse-up (when @mouse-state
                              (fn [_e]
                                (reset! mouse-state nil)))}
              [:div.reagent-dev-tools__panel
               {:style (case place
                         :right  {:width (str width "px")
                                  :top 0
                                  :right 0
                                  :height "100%"
                                  :flex-direction "row"}
                         ;; bottom
                         {:height (str height "px")
                          :width "100%"
                          :bottom 0
                          :flex-direction "column"})}
               [:div.reagent-dev-tools__sizer
                {:style  (case place
                           :right  {:width "5px"
                                    :cursor "ew-resize"}
                           ;; bottom
                           {:height "5px"
                            :cursor "ns-resize"})
                 :on-mouse-down (fn [e]
                                  (reset! mouse-state true)
                                  (.preventDefault e))}]
               [:div
                {:style {:flex "1 0 auto"}}
                [:div.reagent-dev-tools__nav
                 (for [[k {:keys [label]}] panels]
                   [:div.reagent-dev-tools__nav-li
                    {:key (name k)}
                    [:a.reagent-dev-tools__nav-li-a
                     {:class (when (keyword-identical? current-k k) "reagent-dev-tools__nav-li-a--active")
                      :on-click #(swap! dev-state assoc :current k)}
                     label]])
                 [:div.reagent-dev-tools__spacer]
                 [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__nav-li-a--option-button
                  {:on-click #(swap! dev-state assoc :place :bottom)}
                  [:div.reagent-dev-tools__bottom-icon]]
                 [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__nav-li-a--option-button
                  {:on-click #(swap! dev-state assoc :place :right)}
                  [:div.reagent-dev-tools__right-icon]]
                 [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__nav-li-a--close-button
                  {:on-click #(swap! dev-state assoc :open? false)}
                  [:span "×"]]]
                [:div.reagent-dev-tools__panel-content
                 (when current-content [current-content])]]]])
           [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__toggle-btn
            {:on-click (fn [_]
                         (swap! dev-state assoc :open? true)
                         nil)}
            "dev"])]))))

(defn start!
  "Start Reagent dev tool.

  Options:

  :el (optional) The element to render the dev-tool into. If the property is given,
  but is nil, dev tool is not enabled. If not given, new div is created and
  used.

  :state-atom (optional) The Reagent atom to add to state-tree panel with \"App state\" name.

  :panels-fn (optional) Function which returns map of additional panels to display.

  :margin-element (optional) Element where to set margin-bottom/right if the panel is open."
  [opts]
  (when-let [el (if (contains? opts :el)
                  (:el opts)
                  (or (.getElementById js/document element-id)
                      (let [el (.createElement js/document "div")]
                        (set! (.-id el) element-id)
                        (.appendChild (.-body js/document) el)
                        el)))]

    (when (:state-atom opts)
      (state-tree/register-state-atom "App state" (:state-atom opts)))

    (rdom/render
      [dev-tool {:margin-element (:margin-element opts)
                 :panels (merge default-panels
                                (when (:panels-fn opts)
                                  ((:panels-fn opts))))}]
      el)))

(defn register-state-atom
  "Add Reagent atom to the state panel with given name"
  [atom-name state-atom]
  (state-tree/register-state-atom atom-name state-atom))
