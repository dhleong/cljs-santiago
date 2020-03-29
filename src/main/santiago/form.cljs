(ns santiago.form
  (:require [reagent.core :as r]
            [santiago.group :refer [group]]
            [santiago.util :refer [current-value remove-shared-keys]]))

(defn form
  "Form replaces the standard :form element, augmenting :on-submit (if
   provided) to include the value of the form as the second argument.
   It also acts as a [group], providing value context for all child
   elements.

   NOTE: that default values of child elements are not read, currently."
  [opts & children]
  (r/with-let [last-value (atom nil)]

    ; NOTE: update the events *every time* in case a client changed them
    (let [opts (-> opts
                   (update :on-submit
                           (fn [on-submit]
                             (when on-submit
                               ; if an :on-submit was provided that
                               ; accepts a second argument, pass the
                               ; current form value
                               (if (> (.-length ^js on-submit) 1)
                                 #(on-submit % (or @last-value
                                                   ; no changes; send
                                                   ; "current" value
                                                   (current-value nil opts)))
                                 on-submit))))

                   (update :on-change
                           (fn [on-change]
                             (fn [new-value]
                               (when on-change
                                 (on-change new-value))
                               (reset! last-value new-value)))))]
      [:form (-> opts
                 (dissoc :on-change)
                 (remove-shared-keys))
       (into
         [group (dissoc opts :on-submit)]
         children)])))
