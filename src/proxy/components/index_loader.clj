(ns proxy.components.index-loader
  (:require
   [manifold.stream :as stream]
   [hellhound.component :as hcomp]
   [hellhound.system :refer [defcomponent]]))

(defn response
  [body]
  {:body body :status 200 :headers []})


(defn load-index
  [output]
  (fn [event]
    (let [path (System/getProperty "user.dir")
          file (str path "/index.html")]
      (println (str "Loading: " file))

      (stream/put!
       output
       (assoc event :index-content (slurp file))))))

(defn start!
  [component context]
  (let [input  (hcomp/input component)
        output (hcomp/output component)]
    (stream/consume (load-index output) input)
    component))

(defn stop [this] this)

(defn factory
  []
  (defcomponent ::job start! stop))
