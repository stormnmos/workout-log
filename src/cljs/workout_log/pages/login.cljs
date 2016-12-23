(ns workout-log.pages.login
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
   [datascript.core :as d]
   [goog.events :as events]
   [secretary.core :as secretary :refer-macros [defroute]]
   [cljs.core.async :as async :refer [<! >! chan put! take!
                                      poll! offer! mult tap]]
   [cljs.spec :as s]
   [om.core :as om :include-macros true])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:import goog.History))

(enable-console-print!)

(defn run []
  (secretary/set-config! :prefix "#")
  (s/check-asserts true)
  (mount/start)
  (go
    (while true
      (let [tx (<! @events)]
        (.log js/console (str tx))
        (try (d/transact! @conn tx)
             (catch js/Object e
               (.log js/console e))))))
  (let [history (History.)]
    (events/listen history "navigate"
                   (fn [event]
                     (secretary/dispatch! (.-token event)))))
  (om/root c/widget @conn
           {:react-key "root"
            :target (.getElementById js/document "login")}))

(defn on-js-reload []
  (om/root c/widget @conn
           {:react-key "root"
            :target (.getElementById js/document "login")}))
