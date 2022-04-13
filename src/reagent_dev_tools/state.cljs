(ns reagent-dev-tools.state
  (:require [reagent.core :as r]
            [cljs.reader :as reader]))

(def storage-key (str :reagent-dev-tools.core/state))

(defonce dev-state
  (r/atom (merge {:height 300
                  :width 300}
                 (try
                   (reader/read-string (.getItem js/localStorage storage-key))
                   (catch :default _
                     nil)))))

;; Save the state (open, height, active panel) to local storage
(add-watch dev-state :local-storage
           (fn [_ _ _old v]
             (.setItem js/localStorage storage-key (pr-str (dissoc v :mouse)))))
