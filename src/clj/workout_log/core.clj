(ns workout-log.core
  (:require
   [clojure.edn :as edn]
   [clojure.core.async :as async]
   [datomic.api :as d]
   [environ.core :refer [env]]
   [workout-log.db.db :as db]
   [workout-log.db.db-data :as data]
   [workout-log.db.queries :as q]
   [workout-log.handler :as h]
   [workout-log.state :as state :refer [conn tx-chan fail-chan success-chan]]
   [workout-log.util :as u]
   [mount.core :as mount]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.transit :as transit :only [wrap-transit-body
                                               wrap-transit-params
                                               wrap-transit]]))

(mount/start)

(def handler
  (-> h/routes
      (transit/wrap-transit-params)
      (transit/wrap-transit-response {:encoding :json})
      (wrap-session)))

;;; Main handler for transacting into datomic
(async/go
  (while true
    (let [unvalidated-tx (async/<! tx-chan)]
      (if-let [tx (db/validate-tx unvalidated-tx) ]
        (d/transact-async conn tx)
        (async/>! fail-chan unvalidated-tx)))))
