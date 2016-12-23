(ns workout-log.spec
  (:require
   [cljs.spec :as s]))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(s/def ::transaction (s/or :map (s/coll-of map?) :coll (s/coll-of coll?)))

(s/def :submit/user (s/and string? #(< 5 (count %))))
(s/def :submit/email (s/and string? #(re-matches email-regex %)))
(s/def :submit/password (s/and string? #(< 5 (count %))))

(defmulti widget :widget/type)
(s/def :widget/user string?)
(s/def :widget/email string?)
(s/def :widget/password string?)

(s/def :widget/shared (s/keys :req [:widget/type]))
(s/def :widget/ref (s/or :expanded :widget/widget :collapsed int?))
(s/def :widget/refs (s/+ :widget/ref))
(s/def :widget/content (s/+ :widget/ref))

(s/def :add-exercise/name string?)
(defmethod widget :widget/add-exercise [_]
  (s/keys :req [:widget/type]
          :opt [:add-exercise/name
                ]))

(s/def :add-lift/weight number?)
(s/def :add-lift/exercise :widget/ref)
(s/def :add-lift/time number?)
(s/def :add-lift/reps :widget/ref)
(defmethod widget :widget/add-lift [_]
  (s/keys :req [:widget/type]
          :opt [:add-lift/weight :add-lift/exercise
                :add-lift/time :add-lift/refps]))

(s/def :add-note/text string?)
(defmethod widget :widget/add-note [_]
  (s/keys :req [:widget/type] :opt [:add-note/text]))

(s/def :add-user/username :widget/user)
(s/def :add-user/email :widget/email)
(s/def :add-user/password :widget/password)
(s/def :add-user/password-confirmation :widget/password)
(defmethod widget :widget/add-user [_]
  (s/keys :req [:widget/type] :opt [:add-user/email :add-user/password
                                    :add-user/username
                                    :add-user/password-confirmation]))

(s/def :add-rep/exercise :widget/ref)
(s/def :add-rep/notes :widget/refs)
(s/def :add-rep/time number?)
(s/def :add-rep/weight number?)
(defmethod widget :widget/add-rep [_]
  (s/keys :req [:widget/type]
          :opt [:add-rep/exercise :add-rep/notes
                :add-rep/time :add-rep/weight]))

(s/def :add-workout/notes :widget/refs)
(s/def :add-workout/sets :widget/refs)
(s/def :add-workout/time-start number?)
(s/def :add-workout/time-stop number?)
(defmethod widget :widget/add-workout [_]
  (s/keys :req [:widget/type]
          :opt [:add-workout/notes :add-workout/sets
                :add-workout/time-start :add-workout/time-stop]))

(s/def :body/content :widget/ref)
(defmethod widget :widget/body [_]
  (s/keys :req [:widget/type] :opt [:body/content]))

(s/def :card/title string?)
(s/def :card/question :widget/ref)
(s/def :card/answer :widget/ref)
(defmethod widget :widget/card [_]
  (s/keys :req [:widget/type :card/title :card/question :card/answer]))

(s/def :container/content :widget/refs)
(defmethod widget :widget/container [_]
  (s/keys :req [:widget/type :container/content]))

(s/def :exercise/name string?)
(defmethod widget :widget/exercise [_]
  (s/keys :req [:widget/type :exercise/name]))

(s/def :exercises/content :widget/refs)
(defmethod widget :widget/exercises [_]
  (s/keys :req [:widget/type]
          :opt [:exercises/content]))

(s/def :footer/content :widget/refs)
(defmethod widget :widget/footer [_]
  (s/keys :req [:widget/type]
          :opt [:footer/content]))

(s/def :grid/data string?)
(s/def :grid/content :widget/refs)
(defmethod widget :widget/grid [_]
  (s/keys :req [:widget/type :grid/content :grid/data]))

(s/def :header/content :widget/refs)
(defmethod widget :widget/header [_]
  (s/keys :req [:widget/type]
          :opt [:header/content]))

(s/def :header-drawer/title string?)
(s/def :header-drawer/content :widget/refs)
(defmethod widget :widget/header-drawer [_]
  (s/keys :req [:widget/type :header-drawer/title :header-drawer/content]))

(s/def :link/text string?)
(s/def :link/icon string?)
(s/def :link/href string?)
(defmethod widget :widget/link [_]
  (s/keys :req [:widget/type :link/text :link/icon :link/href]))

(s/def :nav/title string?)
(s/def :nav/links :widget/refs)
(defmethod widget :widget/nav [_]
  (s/keys :req [:widget/type :nav/title :nav/links]))

(s/def :nav-link/text string?)
(s/def :nav-link/href string?)
(defmethod widget :widget/nav-link [_]
  (s/keys :req [:widget/type :nav-link/text :nav-link/href]))

(s/def :note/text string?)
(defmethod widget :widget/note [_]
  (s/keys :req [:widget/type :note/text]))

(s/def :notes/content :widget/refs)
(defmethod widget :widget/notes [_]
  (s/keys :req [:widget/type]
          :opt [:notes/content]))

(defmethod widget :widget/login-card [_]
  :widget/shared)

(s/def :menu-item/text string?)
(defmethod widget :widget/menu-item [_]
  (s/keys :req [:widget/type :menu-item/text]))

(s/def :page/content :widget/refs)
(defmethod widget :widget/page [_]
  (s/keys :req [:widget/type :page/content]))

(s/def :register-card/user :widget/user)
(s/def :register-card/email :widget/email)
(s/def :register-card/password :widget/password)
(defmethod widget :widget/register-card [_]
  (s/keys :req [:widget/type :register-card/user :register-card/email
                :register-card/password]))

(s/def :reps/content :widget/refs)
(defmethod widget :widget/reps [_]
  (s/keys :req [:widget/type]
          :opt [:reps/content]))

(s/def :rep/exercise :widget/ref)
(s/def :rep/weight number?)
(s/def :rep/time number?)
(s/def :rep/notes :widget/refs)
(defmethod widget :widget/rep [_]
  (s/keys :req [:widget/type :rep/exercise]
          :opt [:rep/weight :rep/time :rep/notes]))

(s/def :sentence/text string?)
(s/def :sentence/group
  (s/or :uuid uuid? :int int?
        :transit-uuid #(instance? com.cognitect.transit.types.UUID %)))
(s/def :sentence/language
  (s/or :no-transit #{:sentence.language/eng :sentence.language/tur}
        :transit #(#{:sentence.language/eng :sentence.language/tur} (:db/ident %))))
(defmethod widget :widget/sentence [_]
  (s/keys :req [:widget/type :sentence/text]))

(s/def :sidebar/links1 :widget/refs)
(s/def :sidebar/links2 :widget/refs)
(s/def :sidebar/links3 :widget/refs)
(defmethod widget :widget/sidebar [_]
  (s/keys :req [:widget/type :sidebar/links1 :sidebar/links2 :sidebar/links3]))

(s/def :sidebar-link/text string?)
(s/def :sidebar-link/href string?)
(defmethod widget :widget/sidebar-link [_]
  (s/keys :req [:widget/type :sidebar-link/text :sidebar-link/href]))

(s/def :user/name :widget/user)
(s/def :user/email :widget/email)
(s/def :user/password :widget/password)
(defmethod widget :widget/user [_]
  (s/keys :req [:widget/type :user/name]
          :opt [:user/password :user/email]))

(s/def :users/content :widget/refs)
(s/def :users/add-user :widget/ref)
(defmethod widget :widget/users [_]
  (s/keys :req [:widget/type]
          :opt [:users/content :users/add-user]))

(s/def :user-card/user :widget/ref)
(s/def :user-card/data :widget/ref)
(defmethod widget :widget/user-card [_]
  (s/keys :req [:widget/type :user-card/user :user-card/data]))
(s/def :widget/widget (s/multi-spec widget :widget/type))
