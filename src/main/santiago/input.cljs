(ns santiago.input
  (:require [santiago.util :refer [current-value
                                   dispatch-change
                                   remove-shared-keys]]))

(defn input [opts]
  (let [on-change (fn [e]
                    (let [new-value (.-value (.-target e))]
                      (dispatch-change opts new-value)))]
    [:input (-> opts
                (assoc :on-change on-change
                       :value (current-value opts))
                remove-shared-keys)]))



