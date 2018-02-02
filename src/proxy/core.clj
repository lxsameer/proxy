(ns proxy.core
  (:require [aleph.http :as http])
  (:gen-class))

(defn response
  [res]
  {:status (:status res)
   :headers (:headers res)
   :body (:body res)})

(defn proxy->
  [host-url uri]
  (println "PROXYING '" uri "'...")
  (-> @(http/get (str host-url uri))
      response))



(defn handler
  [host-url path]
  (fn [req]
    (let [uri (:uri req)
          cwd (or path (System/getProperty "user.dir"))]
      (if (= uri "/")
        {:status 200
         :headers []
         :body (slurp (str cwd "/index.html"))}
        (proxy-> host-url uri)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [[url _port path] args
        port             (Integer. (or _port "3000"))]
    (if (nil? url)
      (println "URL is missing. Please pass the 3rd party URL as the first arg")
      (do
        (println (str "Starting server on " port "..."))
        (http/start-server (handler url path) {:port port})))))
