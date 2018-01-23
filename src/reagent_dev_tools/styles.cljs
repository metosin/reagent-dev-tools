(ns reagent-dev-tools.styles)

(def panel-style
  {:z-index 2000
   :width "100%"
   :position "fixed"
   :background "#fff"
   :text-align "left"
   :bottom 0
   :height "30vh"
   :display "flex"
   :flex-direction "column"})

(def table-style
  {:border-collapse "collapse"
   :border-spacing 0})

(def nav-style
  {:padding "5px 0 0 10px"
   :margin 0
   :border-bottom "1px solid #ccc"
   :display "flex"
   :background "#eee"
   :flex "0 0 auto"})

(def nav-li-style
  {:margin-bottom "-1px"
   :margin-right "10px"})

(def nav-li-a-style
  {:display "inline-block"
   :padding "10px"
   :cursor "pointer"
   :background "#eee"
   :color "#666"
   :border "1px solid #ccc"
   :border-bottom-color "#ccc"})

(def active
  {:background "#fff"
   :color "#333"
   :border-bottom-color "#fff"})

(def td-style
  {:padding 0})

(def pre-style
  {:display "inline"
   :background "none"
   :border 0
   :padding 0})

(def li-style
  {:padding-left "1em"
   :text-indent "-1em"
   :list-style "none"})

(def pull-right
  {:float "right"})

(def toggle-btn-style
  (merge
    nav-li-a-style
    {:position "fixed"
     :bottom 0
     :right 0
     :z-index 2000}))

(def panel-content
  {:padding "10px"
   :overflow-y "auto"})
