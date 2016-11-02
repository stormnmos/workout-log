(ns workout-log.db.queries
  (:require [datomic.api :as d]
            [workout-log.state :refer [conn]]
            [workout-log.util :as u]))

(defn pba-e [att v]
  "Pull entity by attribute"
  (d/q '[:find ?e .
         :in $ ?att ?v
         :where [?e ?att ?v ]]
       (d/db conn) att v))

(defn pba-es [att v]
  (d/q '[:find ?e
         :in $ ?att ?v
         :where [?e ?att ?v]]
       (d/db conn) att v))

(defn pull-schema []
  (d/q '[:find (pull ?e [*]) .
         :where [:db.part/db :db.install/attribute ?p]
                [?p :db/ident ?e]]
       (d/db conn)))

(defn pull-att [att]
  (d/q '[:find (pull ?e [*])
         :in $ ?attribute
         :where [?e ?attribute]]
       (d/db conn) att))

(defn pull-users []
  (d/q '[:find (pull ?e [*])
         :in $
         :where [?e :user/name]]
       (d/db conn)))

(defn pull-user-by-name [name]
  (d/q '[:find (pull ?e [*]) .
         :in $ ?name
         :where [?e :user/name ?name]]
       (d/db conn) name))
