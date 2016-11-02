(ns workout-log.db.mock-data
  (:require
   [workout-log.components.templates :as t]))
(def body [:widget/body :body/content])
(def page [:widget/page :page/content])
(def header [:widget/header :header/content])
(def footer [:widget/footer :footer/content])
(def add-exercise [:widget/add-exercise :add-exercise/name])
(def add-user [:widget/add-user :add-user/username
               :add-user/email :add-user/password
               :add-user/password-confirmation])
(def add-note [:widget/add-note :note/text])
(def workout [:widget/workout :workout/sets :workout/time-start
              :workout/time-stop :workout/notes])
(def workout-set [:widget/set :set/type :set/reps :set/time :set/notes])
(def rep [:widget/rep :rep/exercise :rep/weight :rep/time :rep/notes])
(def users [:widget/users :users/content])
(def exercises [:widget/exercises :exercises/content])
(def reps [:widget/reps :reps/content])
(def notes [:widget/notes :notes/content])
(def note [:widget/note :note/text])
(def exercise [:widget/exercise :exercise/name #_ :exercise/categories])

(def many  {:db/cardinality :db.cardinality/many})
(def ref   {:db/valueType :db.type/ref})
(def com   {:db/isComponent true})
(def index {:db/index true})

(def schema
  {:page/content  (merge many ref com)
   :body/content  (merge many ref com)
   :users/content (merge many ref com)
   :exercises/content (merge many ref com)
   :workout/sets  (merge      ref com)
   :workout/notes (merge many ref com)
   :reps/notes    (merge many ref com)
   :notes/content (merge many ref com)
   :set/type      (merge      ref com)
   :set/notes     (merge many ref com)
   :set/reps      (merge many ref com)
   :rep/exercise  (merge      ref com)
   :rep/notes     (merge many ref com)
   :exercise/categories (merge many)
   :widget/type  {:db/index true}})

(defn make [[type & keys] id & data]
  (merge {:db/id id
          :widget/type type}
         (zipmap keys data)))

(def fixtures
  (mapv #(apply make %)
        [[users -11]
         [exercises -12]
         [reps -13]
         [notes -14]
         [page     -1 [-2 -3 -11 -5 -12 #_ -13 -14 -15 -6 -7 -8 -10 -4]]
         [header   -2]
         [body     -3]
         [footer   -4]
         [add-user -5]
         [note -6 "This is a newly created note"]
         [note -9 "Another note"]
         [exercise -7 "Deadlift"]
         [rep -8 -7 200 0 [-6 -9]]
         [add-note -10]
         [add-exercise -15]]))
