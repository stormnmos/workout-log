(ns workout-log.components.statefull
  (:require
   [workout-log.components.stateless :as st]
   [workout-log.components.utils :as u]
   [workout-log.db :as db]))

(defn default [{:keys [:db/id]} _]
  [:h1 "Not finding component: " id])

(defn exercise [{:keys [:db/id :exercise/name]} _]
  [:.card.card-block
   [:h4.card-title "Edit Exercise"]
   [:form
    (st/form-id id)
    (st/form-track id :exercise/name {:type "text" :placeholder name})]
   (st/transact-button {:db/id id :exercise/name name})
   (st/remove-button id)])

(defn add-exercise [{:keys [:db/id add-exercise/name]} _]
  [:.card.card-block
   [:h4.card-title "Add Exercise"]
   [:form
    (st/form-track id :add-exercise/name {:type "text" :placeholder name})]
   (st/add-button {:exercise/name name})])

(defn exercises [{:keys [:db/id :exercises/content]} _]
  [:.col-md-12
   [:.row [:h2 "Exercises"]]
   [:.row (u/make-refs content)]])

(defn user [{:keys [:db/id :user/name :user/password :user/email]} _]
  [:.card.card-block
   [:h3.card-title "Edit User"]
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
  [:.card.card-block
   [:h3.card-title "Add User"]
   [:div
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

(defn lift [{:keys [:db/id :lift/type :lift/reps :lift/time :lift/notes]} _]
  [:.card.card-block
   [:form
    (st/form-id id)
    (st/form-track id :lift/type {:type "text" :placeholder type})
    (st/form-track id :lift/reps {:type "number" :placeholder reps})
    (st/form-track id :lift/time {:type "time" :placeholder time})
    (u/make-refs notes)]])

(defn note [{:keys [:db/id :note/text]} _]
  [:.card.card-block
   [:h3.card-title "Edit Note"]
   [:form
    (st/form-id id)
    (st/form-track id :note/text {:type "textarea" :rows 3 :placeholder text})]
   (st/transact-button {:db/id id :note/text text})
   (st/remove-button id)])

(defn add-note [{:keys [:db/id :add-note/text]} _]
  [:.card.card-block
   [:h3.card-title "Add Note"]
   [:form
    (st/form-track id :add-note/text {:type "textarea" :rows 3 :placeholder text})]
   (st/add-button {:note/text text})])

(defn add-workout [{:keys [:db/id :add-workout/sets :add-workout/time-start
                           :add-workout/time-stop :add-workout/notes]} _]
  (let [exercise-names nil]
    [:.card.card-block
     [:h3.card-title "Add Workout"]
     [:form
      (st/form-track id :add-workout/time-start {:type "time" :placeholder time-start})
      (st/form-track id :add-workout/time-stop {:type "time" :placeholder time-stop})]
     (st/add-button {:workout/time-start time-start :workout/time-stop time-stop})]))

(defn notes [{:keys [:db/id :notes/content]} _]
  [:.col-md-12
   [:.row [:h3 "Notes"]]
   [:.row (u/make-refs content)]])

(defn rep [{:keys [:db/id :rep/exercise :rep/weight :rep/time :rep/notes]} _]
  [:.card
   [:h3.card-title "Edit Rep"]
   [:form.row
    (st/form-id id)
    (st/form-track id :rep/weight {:type "number" :placeholder weight})
    (st/form-track id :rep/time {:type "time" :placeholder time})
    (u/make-refs notes)]])

(defn add-rep [{:keys [:db/id :add-rep/exercise
                       :add-rep/weight :add-rep/time :add-rep/notes]} _]
  [:.card.card-title
   [:h3.card-title "Add Rep"]
   [:form
    (st/form-id id)
    (let [exercises (map :exercise/name (db/pull-widgets :widget/exercise))]
      [:h3 (str exercises)])
    (st/form-track id :add-rep/exercise {:type "text" :placeholder exercise})
    (st/form-track id :add-rep/weight {:type "number" :placeholder weight})
    (st/form-track id :add-rep/time {:type "number" :placeholder time})]
   (st/add-button {:rep/exercise exercise :rep/notes notes
                   :rep/weight weight :rep/time time})])

(defn reps [{:keys [:db/id :reps/content]} _]
  [:.col-md-12
   [:.row [:h3 "Reps"]]
   [:.row (u/make-refs content)]])

(defn workout [{:keys [:db/id :workout/sets :workout/time-start
                           :workout/time-stop :workout/notes]} _]
  [:.card.card-block
   [:h3.card-title "Workout"]
   [:form
    (st/form-id id)
    (u/make-refs sets)
    (st/form-track id :workout/time-start {:type "number" :placeholder time-start})
    (st/form-track id :workout/time-stop {:type "time" :placeholder time-stop})
    (u/make-refs notes)]])

(defn footer [{:keys [:header/footer]} _]
  [:footer
   [:.footer-strip
    [:.container
     [:p.text-left.col-md-5
      "Copyright © 2016 Jeremy Storm. All rights reserved"]
     [:p.text-center.col-md-2
      [:a {:href "#top"}
       [:i.fa.fa-angle-up
        "Back to top"]]]]]])

(defn header [{:keys [:header/content]} _]
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
     [:a.nav-item.nav-link {:href "/about.html"} "About"]]]])

(defn page [{:keys [:page/content]} _]
  [:.app
   (header 1 2)
   [:p "text"]
   [:h4 "SPACER"]
   [:.container.fluid
    [:.row
     [:h4 "Workout Log - Beta"]]
    [:div.page
     [:ul.nav.nav-pills {:role "tablist"}
      [:li.nav-item
       [:a.nav-link.active {:data-toggle "tab" :href "#new" :role "tab"} "New"]]
      [:li.nav-item
       [:a.nav-link {:data-toggle "tab" :href "#users" :role "tab"} "Users"]]
      [:li.nav-item
       [:a.nav-link {:data-toggle "tab" :href "#exercises" :role "tab"} "Exercises"]]
      [:li.nav-item
       [:a.nav-link {:data-toggle "tab" :href "#notes" :role "tab"} "Notes"]]
      [:li.nav-item
       [:a.nav-link {:data-toggle "tab" :href "#workouts" :role "tab"} "Workouts"]]
      [:li.nav-item
       [:a.nav-link {:data-toggle "tab" :href "#lifts" :role "tab"} "Lifts"]]
      [:li.nav-item
       [:a.nav-link {:data-toggle "tab" :href "#reps" :role "tab"} "Reps"]]
      [:li.nav-item
       [:a.nav-link {:data-toggle "tab" :href "#all" :role "tab"} "All"]]]
     [:.tab-content
      [:.tab-pane.active.fade.in {:id "new" :role "tabpanel"}
       (u/make u/widgets (first (db/get-widgets :widget/add-user)))
       (map st/render-map (db/pull-widgets :widget/add-workout))
       (u/make u/widgets (first (db/get-widgets :widget/add-workout)))]
      [:.tab-pane.fade {:id "users" :role "tabpanel"}
       (map st/render-map (db/pull-widgets :widget/user))
       (u/make u/widgets (first (db/get-widgets :widget/add-user)))]
      [:.tab-pane.fade {:id "exercises" :role "tabpanel"}
       (map st/render-map (sort-by :db/id (db/pull-widgets :widget/exercise)))
       (u/make-all u/widgets (db/get-widgets :widget/add-exercise))]
      [:.tab-pane.fade {:id "notes" :role "tabpanel"}
       (map st/render-map (db/pull-widgets :widget/note))
       #_(u/make u/widgets (first (db/get-widgets :widget/add-note)))]
      [:.tab-pane.fade {:id "workouts" :role "tabpanel"}
       (map st/render-map (db/pull-widgets :widget/workout))
       (u/make u/widgets (first (db/get-widgets :widget/add-workout)))]
      [:.tab-pane.fade {:id "lifts" :role "tabpanel"}
       (map st/render-map (db/pull-widgets :widget/lift))
       #_(u/make u/widgets (first (db/get-widgets :widget/add-lift)))]
      [:.tab-pane.fade {:id "reps" :role "tabpanel"}
       (map st/render-map (db/pull-widgets :widget/rep))
       (u/make u/widgets (first (db/get-widgets :widget/add-rep)))]
      [:.tab-pane.fade {:id "all" :role "tabpanel"}
       #_(map st/render-map  )]]]]
   (footer 1 2)])

(defn login-page [{:keys [:login/content]} _]
  [:.app
   (header 1 2)
   [:p "text"]
   [:h4 "SPACER"]
   [:.container.fluid
    [:.row
     [:h4 "Workout Log - Login"]]
    [:div.page
     :p "Insert login fields here"]]])
