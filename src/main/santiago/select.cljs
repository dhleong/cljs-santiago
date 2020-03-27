(ns santiago.select
  (:require [santiago.impl.context :refer-macros [defcomponent]]
            [santiago.util :refer [current-value
                                   key-from-children
                                   remove-shared-keys
                                   value-from-children
                                   dispatch-change]]))

(defcomponent select
  "This <select> element operates on the :key of the option elements, rather
   than their string value. The nitty-gritty is abstracted away so you just
   set the appropriate `{:key}` attrs in the options, provide the key that
   should be selected in `:value` or a subscription vector in `:<sub`, and
   changes to the selected key will be dispatched via `:on-change`, or
   `:>evt` will be called to build an event vector to dispatch."
  [context opts & children]
  (let [on-change (fn [e]
                    (let [new-value (.-value (.-target e))
                          new-key (key-from-children children new-value)]
                      (dispatch-change context opts new-key)))

        value-key (current-value context opts)]

    (into [:select (-> opts
                       (assoc :on-change on-change
                              :value (value-from-children children value-key))
                       (remove-shared-keys)
                       (dissoc :selected))]

          children))
  )
