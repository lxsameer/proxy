(ns proxy.components.crawler
  (:require
   [manifold.stream :as stream]
   [hellhound.component :as hcomp]
   [aleph.http :as http]
   [hellhound.system :refer [defcomponent]]
   [manifold.deferred :as d]))

(defn response
  [res]
  {:status (:status res)
   :headers (:headers res)
   :body (:body res)})

(defn fetch-url
  [url]
  (println "PROXYING '" url "'...")
  (d/chain (http/get url)
           response))

(defn proxy
  [output host]
  (fn [event]
    (let [request (:request event)
          url (str host (:uri request))]
      (stream/put! output (assoc event :response (fetch-url url))))))

(defn start
  [host]
  (fn [component context]
    (let [[input output] (hcomp/io component)]
     (stream/consume (proxy output host) input)
     component)))

(defn stop [this] this)

(defn factory
  [host]
  (defcomponent ::job (start host) stop))
