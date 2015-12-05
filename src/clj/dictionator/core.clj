(ns dictionator.core
  (:require [com.stuartsierra.component :as component]
            [dictionator.system :as app])
  (:gen-class))

(defn -main
  "starts the Dictionator webservice"
  [& args]
  (let [[port] args]
    (println (format "Starting Dictionator webservice on port: %s" port))
    (component/start
     (app/prod-system {:web-port 3000}))))
