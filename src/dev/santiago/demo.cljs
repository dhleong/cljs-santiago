(ns santiago.demo
  (:require [archetype.views.error-boundary :refer [error-boundary]]
            [clojure.string :as str]
            [reagent.core :as r]
            [santiago.input :refer [input]]
            [santiago.select :refer [select]]))

(def ranks [:officer
            :detective
            :sergeant
            :lieutenant
            :captain
            :commissioner])

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
        [:option {:key nil} "Civilian"]

        (for [r ranks]
          [:option {:key r} (str/capitalize (name r))])]]]]))

(defn ^:dev/after-load mount-root []
  (r/render [main]
            (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

