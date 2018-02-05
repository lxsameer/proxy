(ns proxy.system
  (:require [hellhound.system :as hh-system :refer [defcomponent]]
            [hellhound.component :as hcomp]
            [proxy.components.web :as web]
            [proxy.components.crawler :as crawler]))

(defn factory
  [port host]
  {:components [(web/factory {:port port})
                (crawler/factory host)]

   :workflow [[::web/server ::crawler/job]
              [::crawler/job ::web/server]]})
