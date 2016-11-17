(ns workout-log.components.utils
  (:require [cljs.spec :as s :include-macros true]
            [datascript.core :as d]
            [om.core :as om :include-macros true]
            [workout-log.db :as db]
            [workout-log.state :refer [conn events]]))

(defprotocol Widget
  (remote     [this]))

(defmulti widgets
  (fn [eid _]
    (db/g :widget/type eid)))

(defn eid->entity [eid]
  {:pre  [(s/valid? :widget/ref eid)]
   :post [(s/valid? map? %)]}
  (->> eid
       (d/entity (d/db @conn))
       (d/touch)))

(defn make [f eid]
  (s/assert :widget/widget (eid->entity eid))
  (om/build f eid {:react-key eid}))

(defn make-all [f eids]
  #_{:pre [(s/valid? :widget/content eids)]}
  (map (partial make f) eids))

(defn make-refs [refs]
  (make-all widgets (map :db/id refs)))

(defn make-ref [ref]
  (make-refs [ref]))
