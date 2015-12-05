(ns dictionator.server
  (:require [com.stuartsierra.component :as component]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [bidi.bidi :as bidi]))

(def routes
  ["" {"/" :index
       "/api" {:get {[""] :api}
               :post {[""] :api}}}])
