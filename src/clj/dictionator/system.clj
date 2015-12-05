(ns dictionator.system
  (:require [com.stuartsierra.component :as component]
            [dictionator.server :as webserver]))

(defn dev-system [config-options]
  (let [{:keys [web-port]} config-options]
    (component/system-map
     :webserver nil)))
