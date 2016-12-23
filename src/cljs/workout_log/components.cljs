(ns workout-log.components
  (:require [om.core :as om :include-macros true]
            [datascript.core :as d]
            [sablono.core :as sab]
            [workout-log.db :as db]
            [workout-log.requests :as req]
            [workout-log.state :refer [conn events]]
            [workout-log.components.statefull :as full]
            [workout-log.components.stateless :as st]
            [workout-log.components.utils :refer [make make-refs widgets]]
            [cljs.core.async :as async
             :refer [<! >! chan put! take! tap offer!]])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]
   [sablono.core :refer [html]]
   [workout-log.components :refer [defwidget]]))

(defwidget :default             full/default)
(defwidget :widget/exercise     full/exercise)
(defwidget :widget/exercises    full/exercises)
(defwidget :widget/add-exercise full/add-exercise)

(defwidget :widget/note         full/note)
(defwidget :widget/notes        full/notes)
(defwidget :widget/add-note     full/add-note)

(defwidget :widget/rep          full/rep)
(defwidget :widget/reps         full/reps)
(defwidget :widget/add-rep      full/add-rep)

(defwidget :wdiget/lift         full/lift)
(defwidget :widget/lifts        full/lift) ;; Create
(defwidget :widget/add-lift     full/lift) ;; Create

(defwidget :widget/user         full/user)
(defwidget :widget/users        full/users)
(defwidget :widget/add-user     full/add-user)

(defwidget :widget/workout      full/workout)
(defwidget :widget/add-workout  full/add-workout) ;; Create
(defwidget :widget/workouts     full/workout) ;; Create

(defwidget :widget/footer       full/footer)
(defwidget :widget/header       full/header)
(defwidget :widget/page         full/page
  om/IDidMount
  (did-mount [this]
    (req/set-att! {:att :user/name}
      (db/get-widget :widget/users) :users/content :widget/user)
    (req/set-att! {:att :exercise/name}
      (db/get-widget :widget/exercises) :exercises/content :widget/exercise)
    (req/set-att! {:att :rep/exercise}
      (db/get-widget :widget/reps) :reps/content :widget/rep)
    (req/set-att! {:att :note/text}
      (db/get-widget :widget/notes) :notes/content :widget/note)
    (req/set-att! {:att :workout/sets}
      (db/get-widget :widget/workout) :workouts/content :widget/workout)))

(defwidget :login/page (fn [_ _] nil))

(defn widget [_]
  (reify
    om/IRender
    (render [this]
      (make widgets (db/get-widget :widget/page)))))


(defn login [_]
  (reify
    om/IRender
    (render [this]
      (make widgets (db/get-widget :widget/login)))))
