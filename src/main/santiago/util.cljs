(ns santiago.util)

(def ^:private dispatch (or (resolve 're-frame.core/dispatch)
                            (fn [_]
                              (throw (js/Error. "re-frame not found; unable to dispatch")))))

(def ^:private subscribe (or (resolve 're-frame.core/subscribe)
                            (fn [_]
                              (throw (js/Error. "re-frame not found; unable to subscribe")))))

(def ^:private err-group-context-no-key
  "Element is in a [group] context but has no :key")

(defn flatten-sequences
  "It's common to mix static elements and dynamic elements from sequences
   as children to, eg: [select]. This tool flattens thoses cases into a
   single sequence for easier consumption"
  [s]
  (mapcat (fn [sub]
            (if (vector? sub)
              [sub]
              sub))
          s))

(defn key-from-children
  "Given the children of an element and a current value, return the
   matching :key. Children are assumed to be, for example:

     [:option {:key :id} value]

   Key may be `nil` for intuitive representation of the absence of value"
  [children target-value]
  (let [k (some->> children
                   flatten-sequences
                   (some (fn [[_ attrs v]]
                           (when (= target-value v)
                             (if (contains? attrs :key)
                               (if-some [k (:key attrs)]
                                 k
                                 ::nil)
                               (:key attrs))))))]
    (case k
      nil (throw (ex-info (str "Unable to pick new key from value `" target-value "`")
                          {:children children}))
      ::nil nil

      ; normal success:
      k)))

(defn value-from-children
  "Given the children of an element and a :key, return the matching value.
   See key-from-children"
  [children target-key]
  (some->> children
           flatten-sequences
           (some (fn [[_ {child-key :key} v]]
                   (when (= target-key child-key)
                     v)))))

; ======= value get/set ===================================

(defn- key->path [k]
  (if (coll? k) k [k]))

(defn current-value
  "Resolve the 'current' value of the element. Supported modes are
   :value for a fixed value, :model for an atom/ratom, or :<sub for
   a re-frame subscription form.

   A :default value may be provided for use as a fallback if the
   value from the selected mode is `nil`"
  [context opts]
  (let [default-value (:default opts)]
    (cond
      (contains? opts :value)
      (:value opts default-value)

      (contains? opts :model)
      (let [current-model @(:model opts)]
        (or (if-let [k (:key opts)]
              (get-in current-model (key->path k))
              current-model)
            default-value))

      (contains? opts :<sub)
      (or (deref (subscribe (:<sub opts)))
          default-value)

      ; group context:
      (contains? opts :key)
      (if-let [{context-opts :opts} context]
        (get-in (current-value nil context-opts)
                (key->path (:key opts))
                default-value)

        (throw (ex-info "Element has :key but no [group] context" opts)))

      context
      (throw (ex-info err-group-context-no-key opts))

      :else
      (throw (ex-info "No current value provided to element" opts)))))

(defn- update-model [opts model new-value]
  (if-let [k (:key opts)]
    (swap! model assoc-in
           (key->path k)
           new-value)
    (reset! model new-value)))

(declare dispatch-change)

(defn- dispatch-context [context opts new-value]
  (if-let [k (:key opts)]
    (let [context-opts (:opts context)
          old-value (current-value nil context-opts)
          new-value (assoc-in old-value
                              (key->path k)
                              new-value)]
      (dispatch-change nil context-opts new-value))

    (throw (ex-info err-group-context-no-key opts))))

(defn dispatch-change
  "Dispatch an :on-change event to all registered handlers.
   This includes a re-frame event builder via :>evt, a ratom
   via :model, an explicit handler via :on-change, or any of
   the above in a containing [group] or [form] context."
  [context opts new-value]
  (let [{evt-builder :>evt
         model :model
         on-change :on-change
         :as handlers} opts]

    ; always dispatch all provided handlers
    (when on-change
      (on-change new-value))
    (when evt-builder
      (dispatch (evt-builder new-value)))
    (when model
      (update-model opts model new-value))
    (when context
      (dispatch-context context opts new-value))

    ; make sure *something* handled the change
    (when-not (or context (some identity (vals handlers)))
      (throw (ex-info "No event dispatch" opts)))))


; ======= cleanup =========================================

(defn remove-shared-keys [opts]
  (dissoc opts :<sub :>evt :model))
