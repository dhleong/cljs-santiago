(ns santiago.demo
  (:require [archetype.views.error-boundary :refer [error-boundary]]
            [clojure.string :as str]
            [reagent.core :as r]
            [santiago.form :refer [form]]
            [santiago.group :refer [group]]
            [santiago.input :refer [input]]
            [santiago.select :refer [select]]))

(def ranks [:officer
            :detective
            :sergeant
            :lieutenant
            :captain
            :commissioner])

(def form-default {:name "Amy Santiago"
                   :rank :sergeant})

(defn- keys-form []
  [:<>
   [:div
    [input {:key :name}]]

   [:div
    [select {:key :rank}
     [:option {:key nil} "Civilian"]

     (for [r ranks]
       [:option {:key r} (str/capitalize (name r))])]]

   (let [id (gensym)]
     [:div
      [input {:key :coconuting? :id id
              :type 'checkbox}]
      [:label {:for id} "Coconuting"]])
   (let [id (gensym)]
     [:div
      [input {:key :coconuts
              :id id
              :min 0
              :type 'number}]
      [:label {:for id} "Coconuts"]])
   ])

(defn with-atom []
  (r/with-let [ratom (r/atom form-default)]
    [:<>
     [:h3 "Atom:"]
     [:pre (str @ratom)]

     [error-boundary
      [:div
       [input {:model ratom :key :name}]]

      [:div
       [select {:model ratom :key :rank}
        [:option {:key nil} "Civilian"]

        (for [r ranks]
          [:option {:key r} (str/capitalize (name r))])]]

      [:div
       [input {:model ratom :key :coconuting?
               :id :coconuting?
               :type 'checkbox}]
       [:label {:for :coconuting?} "Coconuting"]]

      [:div
       [input {:model ratom :key :coconuts
               :id :coconuts
               :min 0
               :type 'number}]
       [:label {:for :coconuts} "Coconuts"]]
      ]]))

(defn with-group []
  (r/with-let [ratom (r/atom form-default)]
    [:<>
     [:h3 "Group:"]

     [:pre (str @ratom)]

     [error-boundary
      [group {:model ratom}
       [keys-form]]]]))

(defn with-form []
  (r/with-let [ratom (r/atom form-default)
               submitted (r/atom nil)]
    [:<>
     [:h3 "Form:"]

     [:pre (str @ratom)]

     (when-let [data @submitted]
       [:<>
        [:h4 "Last Submitted:"]
        [:pre (str data)]])

     [error-boundary
      [form {:model ratom
             :on-submit (fn [e v]
                          (println "SUBMIT" e v)
                          (.preventDefault e)
                          (reset! submitted v))}
       [keys-form]
       [:input {:type 'submit}]]]])
  )

(defn main []
  [:div
   [:h2 "Santiago"]
   [with-atom]
   [with-group]
   [with-form]])

(defn ^:dev/after-load mount-root []
  (r/render [main]
            (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

