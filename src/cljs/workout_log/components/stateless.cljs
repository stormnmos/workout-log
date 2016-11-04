(ns workout-log.components.stateless
  (:require [workout-log.requests :as req]
            [workout-log.actions :as a]))

(defn remove-button [id]
  [:button {:onClick (req/remove-id id)} "Remove"])

(defn add-button [tx]
  [:button {:onClick (req/add tx)} "Add"])

(defn transact-button [tx]
  [:button {:onClick (req/transact tx)} "Transact"])

(defn track-input [id k]
  (partial a/track-input id k))

(defn type-placeholder [placeholder]
  {:type "text" :placeholder placeholder})

(defn form-id [id]
  [:.form-group.row
   [:label.col-form-label.col-xs-1 "ID"]
   [:.col-xs-11
    [:input.form-control {:type "text" :readonly true :value id :disabled true}]]])

(defn form-track [id k opts]
  [:.form-group.row
   [:label.col-form-label.col-xs-3 (str k)]
   [:.col-xs-9
    [:input.form-control (merge {:onKeyUp (track-input id k)} opts)]]])
