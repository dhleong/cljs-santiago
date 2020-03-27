(ns santiago.impl.context
  (:require [react :as react]
            [reagent.core :as r]))

(defonce group-context (react/createContext nil))

(def Provider (.-Provider group-context))
(def Consumer (.-Consumer group-context))

(defn consumer [render]
  [:> Consumer {}
   (fn [context]
     (r/as-element (render context)))])

(defn provider [value children]
  (r/create-element
    Provider #js {:value value}
    (r/as-element children)))
