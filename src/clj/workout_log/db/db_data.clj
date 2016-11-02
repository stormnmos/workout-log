(ns workout-log.db.db-data
  (:require
   [workout-log.util :as u]
   [workout-log.db.transaction-templates :as tt]
   [workout-log.db.queries :as q]
   [workout-log.state :refer [conn tx-chan]]
   [datomic.api :as d]
   [environ.core :refer [env]]
   [clojure.string :as str :only split]
   [clojure.core.async
    :as async :refer [chan sliding-buffer
                      <! >! <!!
                      >!! put! take!]]
   [environ.core :refer [env]]))


(def schema-tx (read-string (slurp (env :schema-file))))
