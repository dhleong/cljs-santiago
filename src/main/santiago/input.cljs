(ns santiago.input
  (:require [clojure.string :as str]
            [santiago.impl.context :refer-macros [defcomponent]]
            [santiago.util :refer [current-value
                                   dispatch-change
                                   remove-shared-keys]]))

(defn- input-type-symbol
  ([opts _] (input-type-symbol opts))
  ([opts]
   (when-let [t (:type opts)]
     (cond
       (symbol? t) t
       (keyword? t) (symbol (name t))
       (string? t) (symbol t)
       :else (do (println "Unexpected input type" t)
                 t)))))

(defn- input->string-value [e]
  (.-value (.-target e)))

(defn- ->number [v]
  (cond
    (number? v) v
    (string? v) (if (str/includes? v ".")
                  (js/parseFloat v)
                  (js/parseInt v))))

(def ^:private input-configs
  {'checkbox {:value-key :checked
              :get-value (fn [e] (.-checked (.-target e)))
              :default false}

   'number {:value-key :value
            :get-value (comp ->number input->string-value)
            :default 0}

   :default {:value-key :value
             :get-value input->string-value
             :default ""}})

(defn- input-config [input-type]
  (or (get input-configs input-type)
      (:default input-configs)))


; ======= public interface ================================

(defcomponent input
  [context opts]
  (let [input-type (input-type-symbol opts)
        {:keys [get-value default value-key]} (input-config input-type)
        on-change (fn [e]
                    (let [new-value (get-value e)]
                      (dispatch-change context opts new-value)))
        value (if-some [v (current-value context opts)]
                v
                (get opts :default default))]
    [:input (-> opts
                (assoc :on-change on-change
                       value-key value)
                remove-shared-keys)]))

