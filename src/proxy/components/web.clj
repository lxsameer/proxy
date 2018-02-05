(ns proxy.components.web
  (:require [aleph.http :as http]
            [hellhound.component :as hcomp]
            [manifold.stream :as stream]
            [manifold.deferred :as d]
            [hellhound.system :refer [defcomponent]]))

(defn handler
  [input output]
  (stream/consume #(d/success!
                    (:response-deferred %)
                    (:response %))
                  input)
  (fn [req]
    (let [response (d/deferred)]
      (stream/put! output {:request req
                           :response-deferred response})
      response)))

(defn start!
  [port]
  (fn [this context]
    (let [input  (hcomp/input this)
          output (hcomp/output this)]
      (assoc this
             :server
             (http/start-server (handler input output) {:port port})))))

(defn stop!
  "Stops the running webserver server."
  [this]
  (if (:server this)
    (do
      (.close (:server this))
      (dissoc this :server))
    this))


(defn factory
  [{:keys [port] :as config}]
  {:hellhound.component/name ::server
   :hellhound.component/start-fn (start! port)
   :hellhound.component/stop-fn stop!})

(defn make-response
  [output]
  (fn [{:keys [index-content] :as event}]
    (stream/put! output
                 (assoc event :response {:body index-content
                                         :headers []
                                         :status 200}))))

(defn ->response
  [this context]
  (let [input  (hcomp/input this)
        output (hcomp/output this)]
    (stream/consume (make-response output) input)
    this))

(defn ->response-factory
  []
  (defcomponent ::->response ->response #(identity %)))
