(ns workout-log.requests
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :as async :refer [<! >!]]
   [workout-log.state :refer [events]]
   [workout-log.components.templates :as t])
  (:require-macros
   [cljs.core.async.macros :refer [go]]))

(defn post
  ([uri param-map]
   (http/post uri {:transit-params param-map
                   :channel (async/chan 1 (comp (map :body)))}))
  ([uri param-map template]
   (async/pipe
    (http/post
     uri
     {:channel (async/chan 1 (comp (map :body) (map template)))
      :transit-params param-map})
    @events
    false)))

(defn set-att! [param-map id attribute type]
  (post "/api/att" param-map (partial t/sync-att id attribute type)))

(defn add [tx-map]
  #(post "/api/add" tx-map identity))

(defn transact [tx]
  #(post "/api/transact" {:tx [tx]} identity))

(defn remove-id [id]
  #(post "/api/transact" {:tx [[:db.fn/retractEntity id]]} identity))
