(ns workout-log.components.stateless
  (:require
   [clojure.string :refer [capitalize]]
   [workout-log.requests :as req]
   [workout-log.actions :as a]))

(defn remove-button [id]
  [:a.btn.btn-primary {:href "#" :onClick (req/remove-id id)} "Remove"])

(defn add-button [tx]
  [:a.btn.btn-primary {:onClick (req/add tx)} "Add"])

(defn transact-button [tx]
  [:a.btn.btn-primary {:href "#" :onClick (req/transact tx)} "Transact"])

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
   [:label.col-form-label.col-xs-2 (capitalize (name k))]
   [:.col-xs-10
    [:input.form-control (merge {:onKeyUp (track-input id k)} opts)]]])

(defn textarea-track [id k opts]
  [:.form-group.row
   [:label.col-form-label.col-xs-2 (capitalize (name k))]
   [:.col-xs-10
    [:textarea.form-control (merge {:onKeyUp (track-input id k)} opts)]]])

(defn table-row [row]
  (let [id (first row)
        cells (rest row)]
    [:tr
     [:th {:scope "row"} id]
     (map (fn [cell] [:td {:id id} cell]) cells)]))

(defn table [headers rows]
  [:.table-responsive
   [:table.table.table-bordered.table-sm
    [:thead
     [:tr
      (map (fn [header] [:th header]) headers)]]
    [:tbody
     (map table-row rows)]]])


(defn user-table [users]
  [:.table-responsive
   [:table.table.table-bordered.table-sm
    [:thead
     [:tr
      [:th "#"]
      [:th "Username"]
      [:th "Email"]
      [:th "Password"]]]]])

(defn render-vec [v]
  [:ul
   (for [i v]
     [:li (str i)])])

(defn render-map [m]
  (let [ks (sort (keys m))]
    [:table.table.table-bordered.table-sm
     (for [k ks]
       [:tr
        [:th (str k)]
        [:td (let [c (get m k)]
               (cond
                 (vector? c) (render-vec c)
                 (map? c) (render-map c)
                 :else (str c)))]])]))
