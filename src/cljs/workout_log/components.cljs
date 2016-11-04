(ns workout-log.components
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom]
            [datascript.core :as d]
            [datascript.transit :as dt]
            [kioo.om :as k]
            [workout-log.actions :as a]
            [workout-log.db :as db]
            [workout-log.util :as u]
            [workout-log.requests :as req]
            [workout-log.state :refer [conn events]]
            [workout-log.spec :as spec]
            [workout-log.components.stateless :as st]
            [workout-log.components.bootstrap :as bs]
            [workout-log.components.templates :as t]
            [cljs.core.async :as async
             :refer [<! >! chan put! take! tap offer!]]
            [cljs.spec :as s :include-macros true]
            [clojure.string :as sring])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]
   [sablono.core :refer [html]]
   [kioo.om :refer [deftemplate]]
   [workout-log.components :refer [defwidget]]))

(defn eid->entity [eid]
  {:pre  [(s/valid? :widget/ref eid)]
   :post [(s/valid? map? %)]}
  (->> eid
       (d/entity (d/db @conn))
       (d/touch)))

(defprotocol Widget
  (remote     [this]))

(defmulti widgets
  (fn [eid _]
    (db/g :widget/type eid)))

(defn make [f eid]
  (s/assert :widget/widget (eid->entity eid))
  (om/build f eid {:react-key eid}))

(defn make-all [f eids]
  #_{:pre [(s/valid? :widget/content eids)]}
  (map (partial make f) eids))

(defn make-refs [refs]
  (make-all widgets (map :db/id refs)))

(defwidget :default
  (fn [{:keys [:db/id]}]
    [:h1 "Not finding component: " id]))

(defwidget :widget/workout
  (fn [{:keys [:db/id :workout/sets :workout/time-start
               :workout/time-stop :workout/notes]}]
    [:.col-md-6.card
     [:form
      (st/form-id id)
      (make-refs sets)
      (st/form-track id :workout/time-start {:type "number" :placeholder time-start})
      (st/form-track id :workout/time-stop {:type "time" :placeholder time-stop})
      (make-refs notes)]]))

(defwidget :widget/set
  (fn [{:keys [:db/id :set/type :set/reps :set/time :set/notes]}]
    [:.col-md-6.card
     [:form
      (st/form-id id)
      (st/form-track id :set/type {:type "text" :placeholder type})
      (st/form-track id :set/reps {:type "number" :placeholder reps})
      (st/form-track id :set/time {:type "time" :placeholder time})
      (make-refs notes)]]))

(defwidget :widget/exercise
  (fn [{:keys [:db/id :exercise/name]}]
    [:.col-md-7.card
     [:.card-title "Edit Exercise"]
     [:form
      (st/form-id id)
      (st/form-track id :exercise/name {:type "text" :placeholder name})]
     (st/transact-button {:db/id id :exercise/name name})
     (st/remove-button id)]))

(defwidget :widget/note
  (fn [{:keys [:db/id :note/text]}]
    [:.col-md-6.card
     [:.card-title "Edit Note"]
     [:form
      (st/form-id id)
      (st/form-track id :note/text {:type "text" :placeholder text})]
     (st/transact-button {:db/id id :note/text text})
     (st/remove-button id)]))

(defwidget :widget/rep
  (fn [{:keys [:db/id :rep/exercise :rep/weight :rep/time :rep/notes]}]
    [:.col-md-6.card
     [:.card-title "Edit Rep"]
     [:form.row
      (st/form-id id)
      (st/form-track id :rep/weight {:type "number" :placeholder weight})
      (st/form-track id :rep/time {:type "time" :placeholder time})
      #_ (make-refs exercise)
      (make-refs notes)]]))

(defn user [{:keys [:db/id :user/name :user/password :user/email]} _]
  [:.col-md-7.card
   [:.card-title "Edit User"]
   [:form
    (st/form-id id)
    (st/form-track id :user/name {:type "text" :placeholder name})
    (st/form-track id :user/email {:type "email" :placeholder email})
    (st/form-track id :user/password {:type "password" :placeholder password})]
   (st/transact-button {:db/id id :user/name name
                        :user/email email :user/password password})
   (st/remove-button id)])

(defwidget :widget/add-exercise
  (fn [{:keys [:db/id add-exercise/name]} _]
    [:.col.md-4
     [:form
      (st/form-track id :add-exercise/name {:type "text" :placeholder name})]
     (st/add-button {:exercise/name name})]))

(defwidget :widget/add-note
  (fn [{:keys [:db/id :add-note/text]} owner]
    [:.col-md-4
     [:.card
      [:.card-title "Add Note"]
      [:form (st/form-track id :add-note/text {:type "text" :placeholder text})]]
     (st/add-button {:note/text text})]))

(defwidget :widget/user user)

(defwidget :widget/reps
  (fn [{:keys [:db/id :reps/content]}]
    [:.col-md-12
     [:.row [:h3 "Reps"]]
     [:.row (make-refs content)]])
  om/IDidMount
  (did-mount [this]
    (req/set-att! {:att :rep/exercise} (db/get-widget :widget/reps)
                  :reps/content :widget/rep)))

(defwidget :widget/notes
  (fn [{:keys [:db/id :notes/content]}]
    [:.col-md-12
     [:.row [:h3 "Notes"]]
     [:.row (make-refs content)]])
  om/IDidMount
  (did-mount [this]
   (req/set-att! {:att :note/text} (db/get-widget :widget/notes)
                 :notes/content :widget/note)))

(defwidget :widget/users
  (fn [{:keys [:db/id :users/content :users/add-user]}]
    [:.col-md-12
     [:.row [:h2 "Edit Users"]]
     [:.row (make-refs content)]
     [:button "Add User"]])
  om/IDidMount
  (did-mount [this]
   (req/set-att! {:att :user/name} (db/get-widget :widget/users)
                 :users/content :widget/user)))

(defwidget :widget/exercises
  (fn [{:keys [:db/id :exercises/content]}]
    [:.col-md-12
     [:.row [:h2 "Exercises"]]
     [:.row (make-refs content)]])
  om/IDidMount
  (did-mount [this]
   (req/set-att! {:att :exercise/name} (db/get-widget :widget/exercises)
    :exercises/content :widget/exercise)))

(defn add-user
  [{:keys [:db/id :add-user/username :add-user/email
           :add-user/password :add-user/password-confirmation]} _]
  [:.card
   [:.card-title "Add User"]
   [:form
    (st/form-id id)
    (st/form-track id :add-user/username {:type "text" :placeholder username})
    (st/form-track id :add-user/email {:type "email" :placeholder email})
    (st/form-track id :add-user/password {:type "password" :placeholder password})
    (st/form-track id :add-user/password-confirmation {:type "password"})]
   (st/add-button {:user/name username :user/email email :user/password password})])
(defwidget :widget/add-user add-user)

(defwidget :widget/footer
  (fn [{:keys [:header/footer]} _]
    [:footer
     [:.footer-strip
      [:.container
       [:p.text-left.col-md-5
        "Copyright © 2016 Jeremy Storm. All rights reserved"]
       [:p.text-center.col-md-2
        [:a {:href "#top"}
         [:i.fa.fa-angle-up
          "Back to top"]]]]]]))

(defwidget :widget/header
  (fn [{:keys [:header/content]} _]
    [:nav.navbar.navbar-dark.navbar-fixed-top.bg-inverse
     [:button.navbar-toggler.hidden-sm-up
      {:type "button"
       :data-toggle "collapse"
       :data-target "#navbar"
       :aria-expanded "false"
       :aria-controls "navbar"
       :aria-label "Toggle navigation"}]
     [:a.navbar-brand {:href "#"} "Workout Log"]
     [:div {:id "navbar"}
      [:nav.nav.navbar-nav.float-xs-left
       [:a.nav-item.nav-link {:href "#"} "Dashboard"]
       [:a.nav-item.nav-link {:href "#"} "Settings"]
       [:a.nav-item.nav-link {:href "#"} "Profile"]
       [:a.nav-item.nav-link {:heft "#"} "Help"]]]]))

(defwidget :widget/page
  (fn [{:keys [:page/content]} _]
    [:.container
     [:.row
      [:h1 "Workout Log"]
      [:h3 "BETA"]]
     [:.row
      [:.col-xs-2]
      (make-refs content)]]))

(defn widget [_]
  (reify
    om/IRender
    (render [this]
      (make widgets (db/get-widget :widget/page)))))
