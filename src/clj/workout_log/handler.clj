(ns workout-log.handler
  (:require [bidi.ring :refer (make-handler)]
            [clojure.core.async :as async :refer [go >!]]
            [compojure.core :refer [defroutes GET PUT POST ANY]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [datomic.api :as d]
            [workout-log.db.queries :as q]
            [workout-log.db.transaction-templates :as tt]
            [workout-log.state :refer [conn tx-chan]]
            [ring.util.response :as res]))

(defn index-handler
  [request]
  (res/response "Homepage"))

(defn echo-handler [request]
  (res/response request))

(defn users-handler [request]
  (res/response (q/pull-users)))

(defn login-handler [request]
  (res/response "Login page."))

(def handler
  (make-handler ["/" {"index.html" index-handler}]))

(defroutes routes
  (POST "/api/att" request
       {:status 200
        :body (q/pull-att (get-in request [:transit-params :att]))})
  (GET "/api/users" []
       {:status 200

        :body (q/pull-users)})
  (GET "/login" request "Login page.")
  (POST "/api/transact" request
        {:status 200
         :body (do
                 (d/transact conn (get-in request [:transit-params :tx]))
                 "done")})
  (POST "/api/q" request
       {:status 200
        :headers {"Content-Type" "application/transit"}
        :body (d/q (get-in request [:transit-params :q]))})
  (POST "/api/query" request
        {:status 200
         :headers {"Content-Type" "application/transit"}
         :body (d/query (merge (:transit-params request)
                               {:args (d/db conn)
                                :timeout 1000}))})
  (POST "/api/add" request
        {:status 200
         :body
         (let [result
               (async/>!! tx-chan (tt/add (:transit-params request)))]
           (if result "true" "false"))})
  (POST "/api/add-user" request
        {:status 200
         :headers {"Content-Type" "application/transit"}
         :body
         (let [result
               (async/>!! tx-chan (tt/add-user (:transit-params request)))]
           (if result "true" "false"))})
  (POST "/api/add-note" request
        {:status 200
         :headers {"Content-Type" "application/transit"}
         :body
         (let [result
               (async/>!! tx-chan (tt/add-note (:transit-params request)))]
           (if result "true" "false"))})
  (route/files "/" {:root "target"})
  (route/not-found "<h1>Page not found</h1>"))
