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
  (fn [_]
    [:h1 "Not finding component"]))

(defwidget :widget/body
  (fn [{:keys [:body/content]} _]
    [:.container-fluid.row
     "Body Content Begins in a Container-fluid"]))


(defwidget :widget/workout
  (fn [{:keys [:db/id
               :workout/sets
               :workout/time-start
               :workout/time-stop
               :workout/notes]}]
    [:.container
     [:.workout
      "Workout"]]))

(defwidget :widget/set
  (fn [{:keys [:db/id :set/type :set/reps :set/time :set/notes]}]
    [:.container "SET"
     [:.set "SET"]]))

(defwidget :widget/exercise
  (fn [{:keys [:db/id :exercise/name]}]
    [:.col-md-6
     [:.input-group
      [:span.input-group-addon "ID"]
      [:input.form-control {:value id :disabled "true"}]]
     [:.input-group
      [:span.input-group-addon "Exercise"]
      [:input.form-control
       {:placeholder name
        :onKeyUp (partial a/track-input id :exercise/name)}]]
     [:button {:onClick #(req/post "/api/transact"
                                   {:tx [{:db/id id
                                          :exercise/name name}]}
                                   identity)}
      "Submit"]
     [:button {:onClick #(req/post "/api/transact"
                                   {:tx [[:db.fn/retractEntity id]]}
                                   identity)}
      "Remove"]]))

(defwidget :widget/note
  (fn [{:keys [:db/id :note/text]}]
    [:.col-md-6
     [:.input-group
      [:span.input-group-addon "ID"]
      [:input.form-control {:value id :disabled "true"}]]
     [:.input-group
      [:span.input-group-addon "Name"]
      [:input.form-control
       {:placeholder text
        :onKeyUp (partial a/track-input id :note/text)}]]
     [:button {:onClick #(req/post "/api/transact"
                                   {:tx [{:db/id id
                                          :note/text text}]}
                                   identity)}
      "Submit"]
     [:button {:onClick #(req/post "/api/transact"
                                   {:tx [[:db.fn/retractEntity id]]}
                                   identity)}
      "Remove"]]))

(defn user [{:keys [:db/id :user/name :user/password :user/email]} _]
  [:.col-md-4
   [:.input-group
    [:span.input-group-addon "ID"]
    [:input.form-control {:value id :disabled "true"}]]
   [:.input-group
    [:span.input-group-addon "Name"]
    [:input.form-control
     {:placeholder name
      :onKeyUp (partial a/track-input id :user/name)}]]
   [:.input-group
    [:span.input-group-addon "Email"]
    [:input.form-control
     {:placeholder email
      :onKeyUp (partial a/track-input id :user/email)}]]
   [:.input-group
    [:span.input-group-addon "Password"]
    [:input.form-control
     {:placeholder password
      :onKeyUp (partial a/track-input id :user/password)}]]
   [:button {:onClick #(req/post "/api/transact"
                                 {:tx [{:db/id id
                                        :user/name name
                                        :user/email email
                                        :user/password password}]}
                                 identity)}
    "Submit"]
   [:button {:onClick #(req/post "/api/transact"
                                 {:tx [[:db.fn/retractEntity id]]}
                                 identity)}
    "Remove"]])

(defwidget :widget/add-exercise
  (fn [{:keys [:db/id
               :add-exercise/name]} _]
    [:.col.md-4
     [:.input-group
      [:span.input-group-addon "Exercise"]
      [:input.form-control
       {:placeholder "Exercise"}]]
     [:button {:onClick #(req/post "/api/add"
                                   {:exercise/name name}
                                   identity)}
      "Add Exercise"]]))

(defwidget :widget/add-note
  (fn [{:keys [:db/id
               :add-note/text]} owner]

    [:.col-md-4
     [:.input-group
      [:span.input-group-addon "Text"]
      [:input.form-control
       {:placeholder "Text"
        :onKeyUp (partial a/track-input id :add-note/text)}]]
     [:button {:onClick #(req/post "/api/add"
                                   {:note/text text}
                                   identity)}
      "Add Note"]]))

(defwidget :widget/user user)

(defwidget :widget/rep
  (fn [{:keys [:db/id :rep/exercise :rep/weight :rep/time :rep/notes]}]
    [:.container "REP" [:br]
     [:.rep
      [:h2 exercise]
      [:h4 weight]
      [:h4 time]
      [:h4 (make-refs notes)]]]))

(defwidget :widget/reps
  (fn [{:keys [:db/id :reps/content]}]
    [:.col-md-12
     [:.row [:h3 "Reps"]]
     [:.row (make-refs content)]])
  om/IDidMount
  (did-mount
   [this]
   (req/set-att!
    {:att :rep/exercise}
    (db/get-widget :widget/reps)
    :reps/content
    :widget/rep)))

(defwidget :widget/notes
  (fn [{:keys [:db/id :notes/content]}]
    [:.col-md-12
     [:.row [:h3 "Notes"]]
     [:.row (make-refs content)]])
  om/IDidMount
  (did-mount
   [this]
   (req/set-att!
    {:att :note/text}
    (db/get-widget :widget/notes)
    :notes/content
    :widget/note)))

(defwidget :widget/users
  (fn [{:keys [:db/id :users/content]}]
    [:.col-md-12
     [:.row [:h2 "Edit Users"]]
     [:.row (make-refs content)]])
  om/IDidMount
  (did-mount
   [this]
   (req/set-att!
    {:att :user/name}
    (db/get-widget :widget/users)
    :users/content
    :widget/user)))

(defwidget :widget/exercises
  (fn [{:keys [:db/id :exercises/content]}]
    [:.col-md-12
     [:.row [:h2 "Exercises"]]
     [:.row (make-refs content)]])
  om/IDidMount
  (did-mount
   [this]
   (req/set-att!
    {:att :exercise/name}
    (db/get-widget :widget/exercises)
    :exercises/content
    :widget/exercise)))

(defn add-user [{:keys [:db/id
                        :add-user/username
                        :add-user/email
                        :add-user/password
                        :add-user/password-confirmation]} _]
  [:.card.card-block
   [:.card-title id]
   [:.input-group
    [:span.input-group-addon "Name"]
    [:input.form-control
     {:placeholder username
      :onKeyUp (partial a/track-input id :add-user/username)}]]
   [:.input-group
    [:span.input-group-addon "Email"]
    [:input.form-control
     {:placeholder email
      :onKeyUp (partial a/track-input id :add-user/email)}]]
   [:.input-group
    [:span.input-group-addon "Password"]
    [:input.form-control
     {:placeholder password
      :onKeyUp (partial a/track-input id :add-user/password)}]]
   [:button {:onClick #(do (req/post "/api/add-user"
                                     {:user/name username
                                      :user/email email
                                      :user/password password}
                                     identity))}
    "Add User"]])
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
       [:a.nav-item.nav-link {:heft "#"} "Help"]]]
     #_[:form.float-xs-right
      [:input.form-control
       {:type "text"
        :placeholder "Search"}]]]))

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
