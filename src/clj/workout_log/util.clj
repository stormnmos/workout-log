(ns workout-log.util
  (:require [datomic.api :as d]))

(defn get-db [comp]
  (d/db (:connection comp)))
