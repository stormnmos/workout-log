(ns workout-log.components.bootstrap)

(defn button [{:keys [:on-click] :as att-map} text]
  [:button att-map
   text])
