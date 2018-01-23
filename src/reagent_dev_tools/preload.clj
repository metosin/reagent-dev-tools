(ns reagent-dev-tools.preload
  (:require [cljs.env]))

(defmacro read-config []
  (if cljs.env/*compiler*
    (get-in @cljs.env/*compiler* [:options :external-config :reagent-dev-tools])))
