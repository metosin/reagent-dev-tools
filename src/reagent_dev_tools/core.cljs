(ns reagent-dev-tools.core
  (:require [reagent.core :as r]
            [reagent-dev-tools.styles :as s]
            [reagent-dev-tools.state-tree :as state-tree]
            [komponentit.mixins :as mixins]
            [cljs.reader :as reader]))

;; Save the state (open, height, active panel) to local storage

(def storage-key (str ::state))
(def element-id (str ::dev-panel))

(defonce dev-state (doto (r/atom (merge {:height 300}
                                        (try
                                          (reader/read-string (.getItem js/localStorage storage-key))
                                          (catch :default _
                                            nil))))
                     (add-watch :local-storage
                                (fn [_ _ old v]
                                  (.setItem js/localStorage storage-key (pr-str (dissoc v :mouse)))))))

(def default-panels
  {:state-tree {:label "State"
                :fn state-tree/state-tree-panel}})

(defn dev-tool
  [{:keys [panels]
    :or {panels default-panels}}]
  (if (:open? @dev-state)
    (let [current-k       (:current @dev-state :state-tree)
          current-panel   (or (get panels current-k) (:state-tree panels))
          current-content (:fn current-panel)]
      [mixins/window-event-listener
       {:on-mouse-move (fn [e]
                         (swap! dev-state (fn [s]
                                            (if (:mouse s)
                                              (do
                                                (.preventDefault e)
                                                (assoc s :height (-> (- (.-innerHeight js/window) (.-clientY e))
                                                                     (max 50)
                                                                     (min 1000))))
                                              s))))
        :on-mouse-up (fn [e]
                       (swap! dev-state dissoc :mouse))}
       [:div {:style (merge
                       s/panel-style
                       {:height (str (:height @dev-state) "px")})}
        [:div {:style s/sizer
               :on-mouse-down (fn [e]
                                (swap! dev-state assoc :mouse true)
                                (.preventDefault e))}]
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
        (if current-content [current-content])]]])
    [:button
     {:onClick (fn [_]
                 (swap! dev-state assoc :open? true)
                 nil)
      :style (merge s/pull-right s/toggle-btn-style)}
     "dev"]))

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

    (r/render
      [dev-tool {:panels (merge default-panels
                               (if (:panels-fn opts)
                                 ((:panels-fn opts))))}]
      el)))
