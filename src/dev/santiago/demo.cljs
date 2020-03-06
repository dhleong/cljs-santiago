(ns santiago.demo
  (:require [archetype.views.error-boundary :refer [error-boundary]]
            [reagent.core :as r]
            [santiago.input :refer [input]]
            [santiago.select :refer [select]]))

(defn main []
  (r/with-let [form (r/atom {:name "Amy Santiago"
                             :rank :sergeant})]
    [:div
     [:div "Santiago"]
     [:pre (str @form)]

     [error-boundary
      [:div
       [input {:model form :key :name}]]

      [:div
       [select {:model form :key :rank}
        [:option {:key :officer} "Officer"]
        [:option {:key :detective} "Detective"]
        [:option {:key :sergeant} "Sergeant"]
        [:option {:key :lieutenant} "Lieutenant"]
        [:option {:key :captain} "Captain"]
        [:option {:key :commissioner} "Commissioner"]]]]]))

(defn ^:dev/after-load mount-root []
  (r/render [main]
            (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

