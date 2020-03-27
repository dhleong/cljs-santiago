(ns santiago.group
  (:require [santiago.impl.context :refer [provider]]))

(defn group
  "A [group] acts as a meta-element, providing an implicit :model
   from which child elements can get and into which they can set their
   value, by simply providing :key."
  [opts & children]
  [provider {:opts opts}
   (into [:<>] children)])
