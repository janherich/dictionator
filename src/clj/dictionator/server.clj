(ns dictionator.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [bidi.bidi :as bidi]))

(def routes
  ["" {"/api" {:get {[""] :read}
               :post {[""] :mutate}}}])

(def handlers
  {:read (fn [req] {:status 200
                    :body "reading..."})
   :mutate (fn [req] {:status 204
                      :body "mutating..."})
   :not-found (fn [req] {:status 404
                         :body "ooops..."})})

(defn router [{:keys [uri request-method] :as req}]
  (let [{:keys [handler]} (bidi/match-route routes uri :request-method request-method)]
    ((get handlers handler :not-found)
     req)))

(defn wrap-state [handler app-state]
  (fn [req]
    (handler (assoc req :app-state app-state))))

(defn prod-handler [app-state]
  (wrap-resource
   (wrap-state router app-state)
   "public"))

(defn dev-handler [app-state]
  (fn [req]
    ((prod-handler app-state) req)))

(defrecord WebServer [port handler app-state]
  component/Lifecycle
  (component/start [component]
    (let [request-handler (handler app-state)
          server (httpkit/run-server handler {:port port})] ;; server constructor returns shutdown-fn
      (assoc component :shut-down server)))
  (component/stop [component]
    (when-let [shutdown-fn (:shut-down component)]
      (shutdown-fn))
    (dissoc component :shut-down)))

(defn dev-server [web-port]
  (WebServer. web-port dev-handler nil))

(defn prod-server [web-port]
  (WebServer. web-port prod-handler nil))
