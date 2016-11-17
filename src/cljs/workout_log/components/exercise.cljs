(ns workout-log.components.exercise
  (:require [workout-log.components.stateless :as st]
            [workout-log.components.utils :as u]))

(defn exercise [{:keys [:db/id :exercise/name]} _]
  [:.col-md-7.card
   [:.card-title "Edit Exercise"]
   [:form
    (st/form-id id)
    (st/form-track id :exercise/name {:type "text" :placeholder name})]
   (st/transact-button {:db/id id :exercise/name name})
   (st/remove-button id)])

(defn add-exercise [{:keys [:db/id add-exercise/name]} _]
  [:.col.md-4
   [:form
    (st/form-track id :add-exercise/name {:type "text" :placeholder name})]
   (st/add-button {:exercise/name name})])

(defn exercises [{:keys [:db/id :exercises/content]} _]
  [:.col-md-12
   [:.row [:h2 "Exercises"]]
   [:.row (u/make-refs content)]])
