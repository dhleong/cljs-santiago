(defproject net.dhleong/santiago "0.1.0-SNAPSHOT"
  :description "A library for people who love forms"
  :url "https://github.com/dhleong/cljs-santiago"

  ;; this is optional, add what you want or remove it
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  ;; always use "provided" for Clojure(Script)
  [[org.clojure/clojurescript "1.10.520" :scope "provided"]
   [reagent "0.9.1"]]

  :source-paths
  ["src/main"])
