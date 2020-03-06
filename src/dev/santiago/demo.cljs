(ns santiago.demo
  (:require [archetype.views.error-boundary :refer [error-boundary]]
            [reagent.core :as r]
            [santiago.core :as f]))

(defn main []
  (r/with-let [form (r/atom {})]
    [:div
     [:div "Santiago"]
     [:pre (str @form)]

     [error-boundary
      [:div
       [f/input {:model form :key :name}]]]]))

(defn ^:dev/after-load mount-root []
  (r/render [main]
            (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

