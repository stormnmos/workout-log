(ns workout-log.components.note
  (:require [workout-log.components.stateless :as st]
            [workout-log.components.utils :as u]))


(defn note [{:keys [:db/id :note/text]} _]
  [:.col-md-6.card
   [:.card-title "Edit Note"]
   [:form
    (st/form-id id)
    (st/form-track id :note/text {:type "textarea" :rows 3 :placeholder text})]
   (st/transact-button {:db/id id :note/text text})
   (st/remove-button id)])

(defn add-note [{:keys [:db/id :add-note/text]} _]
  [:.col-md-4
   [:.card
    [:.card-title "Add Note"]
    [:form (st/form-track id :add-note/text {:type "textarea" :rows 3 :placeholder text})]]
   (st/add-button {:note/text text})])

(defn notes [{:keys [:db/id :notes/content]} _]
  [:.col-md-12
   [:.row [:h3 "Notes"]]
   [:.row (u/make-refs content)]])
