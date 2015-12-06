(ns dictionator.system
  (:require [com.stuartsierra.component :as component]
            [dictionator.server :as webserver]))

(defn dev-system [{:keys [web-port] :as config-options}]
  (component/system-map
   :webserver (webserver/dev-server web-port)))

(defn prod-system [{:keys [web-port] :as config-options}]
  (component/system-map
   :webserver (webserver/prod-server web-port)))
