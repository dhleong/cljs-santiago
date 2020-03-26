(ns santiago.input
  (:require [santiago.impl.context :refer-macros [defcomponent]]
            [santiago.util :refer [current-value
                                   dispatch-change
                                   remove-shared-keys]]))

(defcomponent input
  [context opts]
  (let [on-change (fn [e]
                    (let [new-value (.-value (.-target e))]
                      (dispatch-change opts new-value)))
        value (if-some [v (current-value context opts)] v "")]
    [:input (-> opts
                (assoc :on-change on-change
                       :value value)
                remove-shared-keys)]))

