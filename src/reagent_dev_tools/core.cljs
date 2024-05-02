(ns reagent-dev-tools.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [reagent-dev-tools.styles :as s]
            [reagent-dev-tools.state-tree :as state-tree]
            [reagent-dev-tools.state :as state]
            [reagent-dev-tools.utils :refer [window-event-listener]]
            [reagent-dev-tools.context :as ctx]))

(def element-id (str ::dev-panel))

(def state-tree state-tree/state-tree-panel)
(def collection-info-handler state-tree/collection-info-handler)
(def register-collection-info-handler! state-tree/register-collection-info-handler)

(defn create-default-panels [options]
  (if (:state-atom options)
    [{:key ::default
      :label (:state-atom-name options "State")
      :view [state-tree
             {:k :state-atom
              :ratom (:state-atom options)}]}]
    (if (nil? (:panels options))
      [{:key ::default
        :label (:state-atom-name options "State")
        :view [:div [:p "Configure either `:state-atom` or `:panels`."]]}]
      [])))

(defn default-toggle-btn [open-fn]
  [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__toggle-btn
   {:on-click open-fn}
   "dev"])

(defn dev-tool
  #_:clj-kondo/ignore
  [{:keys [panels]
    :as options}]
  (let [mouse-state (r/atom nil)]
    (fn [{:keys [panels margin-element toggle-btn]}]
      (let [{:keys [open? place width height]} @state/dev-state

            toggle-btn (or toggle-btn default-toggle-btn)
            panels (keep identity panels)
            id->panel (into {} (map (juxt :key identity) panels))]
        (when margin-element
          (set! (.. margin-element -style -marginRight) (when (and open? (= :right place))
                                                          (str width "px")))

          (set! (.. margin-element -style -marginBottom) (when (and open? (= :bottom place))
                                                           (str height "px"))))
        [:<>
         [:style (s/main-css)]
         (if open?
           (let [current-k       (:current @state/dev-state ::default)
                 current-panel   (or (get id->panel current-k)
                                     (::default id->panel))]
             [window-event-listener
              {:on-mouse-move (when @mouse-state
                                (fn [e]
                                  (.preventDefault e)
                                  (swap! state/dev-state
                                         (fn [v]
                                           (case place
                                             :right  (assoc v :width (-> (- (.-innerWidth js/window) (.-clientX e))
                                                                         (max 250)
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
                          :left 0
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
                {:style {:display "flex"
                         :flex-direction "column"
                         :flex "1 0 auto"
                         :width "100%"
                         :height "100%"}}
                [:div.reagent-dev-tools__nav
                 [:div.reagent-dev-tools__nav-panels
                  (for [panel panels]
                    [:div.reagent-dev-tools__nav-li
                     {:key (name (:key panel))}
                     [:a.reagent-dev-tools__nav-li-a
                      {:class (when (keyword-identical? current-k (:key panel)) "reagent-dev-tools__nav-li-a--active")
                       :on-click #(swap! state/dev-state assoc :current (:key panel))}
                      (:label panel)]])]

                 ;; Just diplay the button to toggle to the other state.
                 (if (= :right place)
                   [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__nav-li-a--option-button
                    {:on-click #(swap! state/dev-state assoc :place :bottom)}
                    [:div.reagent-dev-tools__bottom-icon]]
                   [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__nav-li-a--option-button
                    {:on-click #(swap! state/dev-state assoc :place :right)}
                    [:div.reagent-dev-tools__right-icon]])

                 [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__nav-li-a--close-button
                  {:on-click #(swap! state/dev-state assoc :open? false)}
                  [:div.reagent-dev-tools__close-icon]]]

                ;; Allow the panel component to access panel-options through React context
                ;; E.g. to access the panel :key or :label
                [:div.reagent-dev-tools__panel-content
                 [:r> ctx/panel-context-provider
                  #js {:value current-panel}
                  (:view current-panel)]]]]])
           [:f> toggle-btn
            (fn [_]
              (swap! state/dev-state assoc :open? true)
              nil)])]))))

(def ^:private panels-fn-warning
  (delay (js/console.warn "Reagent dev tools option `:panels-fn` is deprecated. Use `:panels` instead.")))

;; NOTE: sync the option changes to README.
(defn start!
  "Start Reagent dev tool.

  Options:

  - `:el` (optional) The element to render the dev-tool into. If the property is given,
  but is nil, dev tool is not enabled. If not given, new div is created and used.
  - `:margin-element` (optional) Element where to set margin-bottom/right if the panel is open.
  This is helpful so that the dev tool isn't displayed over the application content.
  - `:state-atom` This options adds default `state-tree` panel displaying tree for the given RAtom.
  - `:state-atom-name` (optional) Overrides the name for default `state-tree` panel.
  - `:panels` List of panel maps to display. This is appended to the default panels, if you
  don't want to include default panels, leave out :state-atom option and define all panels here.
  - `:toggle-btn` (optional) Reagent component to render the open button. Takes `open-fn` as parameter.
  Rendered as functional component so the component can also use hooks.

  Panel options:
  - `:key` (Required) React key
  - `:label` (Required) Label for tab bar
  - `:view` (Required) Reagent Hiccup form to display the panel content

  Built-in panel component options:

  - `reagent-dev-tools.core/state-tree`
      - `:ratom` (Required) The RAtom to display
      - `:label` (Optional) Label to display for atom root node, will default to panel :label."
  [opts]
  (when (:panels-fn opts)
    @panels-fn-warning)

  (doseq [panel (:panels opts)
          :when (some? panel)]
    (assert (:key panel) "Panel :key is required")
    (assert (vector? (:view panel)) "Panel :view is required and must an vector"))

  (when-let [el (if (contains? opts :el)
                  (:el opts)
                  (or (.getElementById js/document element-id)
                      (let [el (.createElement js/document "div")]
                        (set! (.-id el) element-id)
                        (.appendChild (.-body js/document) el)
                        el)))]

    (rdom/render
      [dev-tool {:margin-element (:margin-element opts)
                 :toggle-btn (:toggle-btn opts)
                 :panels (into (create-default-panels opts)
                               (:panels opts))}]
      el)))
