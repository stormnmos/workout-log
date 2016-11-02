(ns workout-log.state
  (:require
   [cljs.core.async :as async :refer [<! >! chan put! take! mult]]
   [cljs.spec :as s]
   [datascript.core :as d]
   [workout-log.db.mock-data :as m]
   [workout-log.util :as u])
  (:require-macros [mount.core :refer [defstate]]))

(defn create-db []
  (d/create-conn m/schema))

#_(defn populate-db! [conn]
  (if-let [stored (js/localStorage.getItem "workout-log/DB")]
    (let [stored-db (u/string->db stored)]
      (d/reset-conn! conn stored-db))
    (d/transact! conn m/fixtures)))

(defn populate-db! [conn]
  (d/transact! conn m/fixtures))

(defstate conn
  :start (let [conn (create-db)]
           (populate-db! conn)
           conn))

(defstate events
  :start (chan 10))
