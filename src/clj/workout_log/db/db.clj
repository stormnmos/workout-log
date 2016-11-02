(ns workout-log.db.db
  (:require
   [workout-log.db.db-data :as dbd]
   [workout-log.db.queries :as q]
   [workout-log.db.transaction-templates :as tt]
   [workout-log.state :refer [conn]]
   [datomic.api :as d]
   [environ.core :refer [env]]
   [clojure.core.async
    :as async :refer [chan sliding-buffer
                      <! >! <!!
                      >!! put! take!]]))

(defn load-schema [schema-tx]
  (d/transact conn schema-tx))

(defmulti validate-tx
  "Ingest a transaction into Datomic DB"
  :type)

(defmethod validate-tx :add-user [{tx :tx}]
  tx)

(defmethod validate-tx :add-note [{tx :tx}]
  tx)

(defmethod validate-tx :default [{tx :tx}]
  tx)
