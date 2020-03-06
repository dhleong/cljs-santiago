(ns santiago.select
  (:require [santiago.util :refer [current-value
                                   key-from-children
                                   remove-shared-keys
                                   value-from-children
                                   dispatch-change]]))

(defn select
  "This <select> element operates on the :key of the option elements, rather
   than their string value. The nitty-gritty is abstracted away so you just
   set the appropriate `{:key}` attrs in the options, provide the key that
   should be selected in `:value` or a subscription vector in `:<sub`, and
   changes to the selected key will be dispatched via `:on-change`, or
   `:>evt` will be called to build an event vector to dispatch."
  [opts & children]
  (let [on-change (fn [e]
                    (let [new-value (.-value (.-target e))
                          new-key (key-from-children children new-value)]
                      (println "new-key = " new-key)
                      (dispatch-change opts new-key)))

        value-key (current-value opts)]

    (into [:select (-> opts
                       (assoc :on-change on-change
                              :value (value-from-children children value-key))
                       (remove-shared-keys)
                       (dissoc :selected :<sub :>evt))]

          children)))
