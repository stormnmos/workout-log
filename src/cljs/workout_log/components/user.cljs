(ns workout-log.components.user
  (:require [workout-log.components.stateless :as st]
            [workout-log.components.utils :as u]))

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

(defn add-user
  [{:keys [:db/id :add-user/username :add-user/email
           :add-user/password :add-user/password-co3nfirmation]} _]
  [:.card
   [:.card-title "Add User"]
   [:form
    (st/form-id id)
    (st/form-track id :add-user/username {:type "text" :placeholder username})
    (st/form-track id :add-user/email {:type "email" :placeholder email})
    (st/form-track id :add-user/password {:type "password" :placeholder password})
    (st/form-track id :add-user/password-confirmation {:type "password"})]
   (st/add-button {:user/name username :user/email email :user/password password})])

(defn users [{:keys [:db/id :users/content :users/add-user]} _]
  [:.col-md-12
   [:.row [:h2 "Edit Users"]]
   [:.row (u/make-refs content)]
   [:button "Add User"]])
