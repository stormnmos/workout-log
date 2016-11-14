(ns workout-log.handler
  (:require [bidi.ring :refer (make-handler ->Files ->ResourcesMaybe ->Redirect)]
            [clojure.core.async :as async :refer [go >!]]
            [datomic.api :as d]
            [workout-log.db.queries :as q]
            [workout-log.db.transaction-templates :as tt]
            [workout-log.state :refer [conn tx-chan]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.util.response :as res]))

(defn index
  [request]
  (res/file-response "target/index.html"))

(defn add [request]
  (res/response
   (let [result
         (async/>!! tx-chan (tt/add (:transit-params request)))]
     (if result "true" "false"))))

(defn att [request]
  (res/response (q/pull-att (get-in request [:transit-params :att]))))

(defn echo [request]
  (res/response request))

(defn query [request]
  (res/response
   (d/query (merge (:transit-params request)
                   {:args (d/db conn)
                    :timeout 1000}))))

(defn transact [request]
  (res/response
   (str (d/transact-async conn (get-in request [:transit-params :tx])))))

(defn users [request]
  (res/response (q/pull-users)))

(def handler
  (make-handler
   ["/" [["index.html" index]
         ["api/"
          {"add"      add
           "att"      att
           "query"    query
           "transact" transact
           "users"    users}]
         ["" (->Files {:dir "target"})]]]))
