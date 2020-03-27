(ns santiago.impl.context)

(defmacro defcomponent
  "Declare a component that can accept context. This works like a fancier
   defn, but requires that the first param be the `context` object. This
   will NOT need to be provided by consumers of the component, and is
   instead provided using React Context"
  [name-sym & fdecl]
  (let [[doc fdecl] (if (string? (first fdecl))
                      [(first fdecl) (next fdecl)]
                      [nil fdecl])
        params (first fdecl)
        body (rest fdecl)
        variadic? (some '#{&} params)

        impl-name-sym (symbol (str (name name-sym)
                                   "-impl"))]
    `(do
       (defn- ~impl-name-sym
         ~params
         ~@body)


       ~(if variadic?
          ; Common, but unfortunate, case. The component is variadic,
          ; and clojure currently drops :arglists meta for variadic
          ; defns. So, shim the "real" argslist into the doc
          `(defn ~name-sym
             ~(str (vec (next params)) "\n\n" doc)
             [& ~'args]
             [santiago.impl.context/consumer
              (fn [context#]
                (into [~impl-name-sym context#] ~'args))])

          ; Nice case where we can fake the argslist
          `(defn ~name-sym
             {:arglists (quote ~(list (vec (next params))))
              :doc ~doc}
             ~(vec (next params))
             [santiago.impl.context/consumer
              (fn [context#]
                [~impl-name-sym context# ~@(next params)])])
          ))))
