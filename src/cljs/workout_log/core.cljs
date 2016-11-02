(ns workout-log.core
  (:require
   [mount.core :as mount]
   [workout-log.actions :as a]
   [workout-log.db :as db]
   [workout-log.components :as c]
   [workout-log.components.templates :as templates]
   [workout-log.db.mock-data :as m]
   [workout-log.requests :as req]
   [workout-log.state :as state :refer [conn events]]
   [workout-log.spec :as spec]
   [workout-log.util :as u]
   [cognitect.transit :as t]
   [datascript.core :as d]
   [goog.dom :as gdom]
   [goog.events :as events]
   [om.core :as om :include-macros true]
   [cljs.core.async :as async :refer [<! >! chan put! take!
                                      poll! offer! mult tap]]
   [secretary.core :as secretary :refer-macros [defroute]]
   [cljs.pprint :as pprint]
   [cljs.spec :as s])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:import goog.History))

(enable-console-print!)

(defroute api-schema "/api/schema" []
  (req/http-get "/api/schema" identity))
(defroute users "#/api/users" []
  (req/http-get "/api/users" templates/make-users))
(defroute register "/#api/register" []
  (req/http-get "/api/users" identity))
(defroute language-ids "/language-ids" []
  nil)
(defroute translation-group "#/translation-group" []
  (req/http-get "/translation-group" templates/card))
(defroute query "#/api/query" [data]
  (req/http-get "/api/query" templates/card))

(defn run []
  (secretary/set-config! :prefix "#")
  (s/check-asserts true)
  (mount/start)
  (go
    (while true
      (let
         [tx (<! @events)]
          #_[tx (s/assert ::spec/transaction (<! @events))]
        (.log js/console (str tx))
        (try (d/transact! @conn tx)
             (catch js/Object e
               (.log js/console e))))))
  (let [history (History.)]
    (events/listen history "navigate"
                   (fn [event]
                     (secretary/dispatch! (.-token event))))
    (.setEnabled history true))
  (om/root c/widget @conn
           {:react-key "root"
            :target (.getElementById js/document "workout-log")}))

(defn on-js-reload []
  (om/root c/widget @conn
           {:react-key "root"
            :target (.getElementById js/document "workout-log")}))
