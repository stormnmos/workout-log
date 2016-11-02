(ns workout-log.components.templates
  (:require [cljs.spec :as s]))

(defn widget [id type]
  {:db/id id
   :widget/type type})

(defn make [type & keys]
  (fn [id & data]
    (merge (widget id type)
           (zipmap keys data))))

(defn users [data]
  [{:db/id 2
    :users/content (flatten data)}])

(defn sentence [data]
  [(merge {:widget/type :widget/sentence} data)])

(defn card
  ([data]
   (card -1 data))
  ([id data]
   [(merge (widget id :widget/card)
           {:card/title "New Card from Datomic"
            :card/question (:db/id (first data))
            :card/answer   (:db/id (second data))})
    (merge {:widget/type :widget/sentence} (first data))
    (merge {:widget/type :widget/sentence} (second data))]))

(defn user-card-template
  ([id user]
   {:db/id id
    :widget/type :widget/user-card
    :user-card/user (first user)})
  ([id user data]
   {:db/id id
    :widget/type :widget/user-card
    :user-card/user user
    :user-card/data data}))

(defn make-users [datas]
  [(user-card-template -1 (first datas))
   (user-card-template -2 (second datas))
   {:db/id 19
    :grid/content [-1 -2]}])

#_(defn sync-users [response]
  (.log js/console (:user/email (ffirst response)))
  [{:db/id 6
    :users/content
    (mapv #(merge {:db/id -1
                  :widget/type :widget/user}
                  (first %))
          response)}])

(defn sync [data]
  (flatten data))

#_(defn sync-users [data]
  (users (mapv #(merge {:widget/type :widget/user} %)
               (flatten (:body data)))))

#_(defn sync-users [datas]
  (map #(merge {:widget/type :widget/user} %)
       (flatten (:body datas))))

(defn sync-att [id attribute type qr]
  [{:db/id id
    attribute
    (map (fn [r]
           (merge {:widget/type type} r))
         (flatten qr))}])
