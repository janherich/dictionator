(ns dictionator.system
  (:require [com.stuartsierra.component :as component]
            [dictionator.server :as webserver]
            [dictionator.impl.web-truth-source :refer [movie-truth-source]]))

(defn dev-system [{:keys [web-port] :as config-options}]
  (component/system-map
   :app-state (atom {:truth-sources [movie-truth-source]
                     :players {}
                     :games {}})
   :webserver (component/using
               (webserver/dev-server web-port)
               [:app-state])))

(defn prod-system [{:keys [web-port] :as config-options}]
  (component/system-map
   :app-state (atom {:truth-sources [movie-truth-source]
                     :players {}
                     :games {}})
   :webserver (component/using
               (webserver/prod-server web-port)
               [:app-state])))
