(ns reagent-dev-tools.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [reagent-dev-tools.styles :as s]
            [reagent-dev-tools.state-tree :as state-tree]
            [komponentit.mixins :as mixins]
            [cljs.reader :as reader]))

;; Save the state (open, height, active panel) to local storage

(def storage-key (str ::state))
(def element-id (str ::dev-panel))

(defonce dev-state
  (r/atom (merge {:height 300}
                 (try
                   (reader/read-string (.getItem js/localStorage storage-key))
                   (catch :default _
                     nil)))))

(add-watch dev-state :local-storage
           (fn [_ _ _old v]
             (js/console.log "store to local storage")
             (.setItem js/localStorage storage-key (pr-str (dissoc v :mouse)))))

(def default-panels
  {:state-tree {:label "State"
                :fn state-tree/state-tree-panel}})

(defn dev-tool
  [{:keys [panels]
    :or {panels default-panels}}]
  (let [mouse-state (atom nil)]
    (fn [{:keys [panels]
          :or {panels default-panels}}]
      [:div
       [:style (s/main-css)]
       (if (:open? @dev-state)
         (let [current-k       (:current @dev-state :state-tree)
               current-panel   (or (get panels current-k) (:state-tree panels))
               current-content (:fn current-panel)]
           [mixins/window-event-listener
            {:on-mouse-move (fn [e]
                              (when @mouse-state
                                (.preventDefault e)
                                (swap! dev-state assoc :height (-> (- (.-innerHeight js/window) (.-clientY e))
                                                                   (max 50)
                                                                   (min 1000)))))
             :on-mouse-up (fn [_e]
                            (reset! mouse-state nil))}
            [:div.reagent-dev-tools__panel
             {:style {:height (str (:height @dev-state) "px")}}
             [:div.reagent-dev-tools__sizer
              {:on-mouse-down (fn [e]
                                (reset! mouse-state true)
                                (.preventDefault e))}]
             [:div.reagent-dev-tools__nav
              (for [[k {:keys [label]}] panels]
                [:div.reagent-dev-tools__nav-li
                 {:key (name k)}
                 [:a.reagent-dev-tools__nav-li-a
                  {:class (when (keyword-identical? current-k k) "reagent-dev-tools__nav-li-a--active")
                   :on-click #(swap! dev-state assoc :current k)}
                  label]])
              [:div.reagent-dev-tools__spacer]
              [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__nav-li-a--close-button
               {:on-click #(swap! dev-state assoc :open? false)}
               [:span "×"]]]
             [:div.reagent-dev-tools__panel-content
              (when current-content [current-content])]]])
         [:button.reagent-dev-tools__nav-li-a.reagent-dev-tools__toggle-btn
          {:on-click (fn [_]
                       (swap! dev-state assoc :open? true)
                       nil)}
          "dev"])])))

(defn start!
  "Start Reagent dev tool.

  Options:

  :el (optional) The element to render the dev-tool into. If the property is given,
  but is nil, dev tool is not enabled. If not given, new div is created and
  used.

  :state-atom (optional) The Reagent atom to add to state-tree panel with \"App state\" name.

  :panels-fn (optional) Function which returns map of additional panels to display."
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
      [dev-tool {:panels (merge default-panels
                                (when (:panels-fn opts)
                                  ((:panels-fn opts))))}]
      el)))
