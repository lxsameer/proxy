(ns proxy.system
  (:require [hellhound.system :as hh-system :refer [defcomponent]]
            [hellhound.component :as hcomp]
            [proxy.components.web :as web]
            [proxy.components.crawler :as crawler]
            [proxy.components.index-loader :as loader]))

(defn uri
  [event]
  (:uri (:request event)))

(defn factory
  [port host]
  {:components [(web/factory {:port port})
                (crawler/factory host)
                (loader/factory)
                (web/->response-factory)]

   :workflow [[::web/server #(not (= "/" (uri %))) ::crawler/job]
              [::web/server #(= "/" (uri %))       ::loader/job]
              [::crawler/job ::web/server]
              [::loader/job  ::web/->response]
              [::web/->response ::web/server]]})
