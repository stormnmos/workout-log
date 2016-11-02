(ns workout-log.actions
  (:require
   [cljs.core.async :as async]
   [datascript.core :as d]
   [datascript.db :as ddb]
   [om.core :as om]
   [workout-log.components.templates :as templates]
   [workout-log.db :as db]
   [workout-log.state :as state :refer [conn events]]
   [workout-log.requests :as req])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn transact! [data]
  (go
    (>! @events data)))

(defmulti transactions!
  (fn [transaction]
    (:type transaction)))

(defn card-request-handler [[status body]]
  (transact! [(templates/card body)]))

(defn schema-request-handler [response]
  (transact! [response]))

(defn users-request-handler [[status body]]
  (transact! body))

(defn add-text [eid events owner order tag]
  (go (>! events [{:db/id -1 :widget/type :text
                   :widget/content "New"
                   :widget/owner owner
                   :widget/order order
                   :widget/tag tag}])))

(defn retract [_ eid events]
  (go (>! events [[:db.fn/retractEntity eid]])))

(defn not-active [owner]
  {:on-click  #(om/set-state! owner :show-dropdown true)
   :on-mouse-leave #(om/set-state! owner :show-dropdown false)})

(defn active [owner]
  {:on-mouse-enter #(om/set-state! owner :show-dropdown true)
   :on-mouse-leave #(om/set-state! owner :show-dropdown false)})

(defn validate-card [eid db]
  "confirm that eid should be deleted")

(defn remove-eid [eid]
  (transact! [[:db.fn/retractEntity eid]]))

(defn next-card [eid]
  (let [next (->> (d/datoms (d/db @conn) :avet :widget/type :widget/card)
                  (map :e)
                  (filter #(< eid %))
                  (first))]
    (transact! [[:db.fn/cas (db/get-widget :widget/grid) :grid/content
                         eid next]
                        [:db.fn/retractEntity eid]])))

(defn get-card-eids []
  (d/datoms :avet :card/title))

(defn track-input [id key e]
  (transact! [{:db/id id
              key (-> e .-target .-value)}]))
