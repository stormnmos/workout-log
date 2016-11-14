(ns workout-log.components)

(defmacro defwidget
  "Docstring for macro"
  [key f & body]
  `(defmethod workout-log.components.utils/widgets ~key [eid# owner#]
     (reify
       workout-log.components.utils/Widget
       ~@body
       om/IDisplayName
       (~(symbol "display-name") [this#]
        eid#)
       om/IInitState
       (~(symbol "init-state") [this#]
        {:listener (async/chan (async/dropping-buffer 1))})
       om/IRender
       (~(symbol "render") [this#]
        (~(symbol "html")
         (~f (d/touch (d/entity (d/db @~(symbol "conn")) eid#)) owner#)))
       om/IWillMount
       (~(symbol "will-mount") [this#]
        (let [listener# (om/get-state owner# :listener)]
          (d/listen! @~(symbol "conn") eid# #(~(symbol "offer!") listener# %))))
       om/IWillUnmount
       (~(symbol "will-unmount") [this#]
        (d/unlisten! @~(symbol "conn") eid#))
       om/IShouldUpdate
       (~(symbol "should-update") [this# _# _#]
        (when-let [tx-report# (async/poll! (om/get-state owner# :listener))]
          (not (== (d/touch (d/entity (:db-before tx-report#) eid#))
                   (d/touch (d/entity (d/db @~(symbol "conn")) eid#)))))))))

(defmacro deftemplate
  "Docstring for macro"
  [name & fields]
  `(defn name [id ~@fields]
     {:db/id id}))
