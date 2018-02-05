(ns proxy.core
  (:require [proxy.system :as system]
            [hellhound.system :as hh-system]
            [aleph.http :as http])
  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [[url _port path] args
        port             (Integer. (or _port "3000"))]
    (if (nil? url)
      (println "URL is missing. Please pass the 3rd party URL as the first arg")
      (let [proxy-system (system/factory port url)]
        (println (str "Starting server on " port "..."))
        (hh-system/set-system! proxy-system)
        (hh-system/start!)))))

        ;;(http/start-server (handler url path) {:port port})))))
