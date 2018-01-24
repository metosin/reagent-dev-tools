(ns reagent-dev-tools.styles
  (:require [clojure.java.io :as io]))

(defmacro main-css []
  (slurp (io/resource "reagent_dev_tools/styles.css")))
