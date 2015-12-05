(ns dictionator.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]
            [ring.middleware.transit :refer [wrap-transit-response wrap-transit-body]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [response]]
            [bidi.bidi :as bidi]))

(def routes
  ["" {"/api" {:get {[""] :read}
               :post {[""] :mutate}}}])

(def handlers
  {:read (fn [req]
           (response {:vals [:a :b :c]}))
   :mutate (fn [req]
             (response {:keys [:a :b :c]}))
   :not-found (fn [req]
                (response {:code :not-found}))})

(defn router [{:keys [uri request-method] :as req}]
  (let [{:keys [handler]} (bidi/match-route routes uri :request-method request-method)]
    ((get handlers handler :not-found)
     req)))

(defn wrap-state [handler app-state]
  (fn [req]
    (handler (assoc req :app-state app-state))))

(defn prod-handler [app-state]
  (-> router
      (wrap-state app-state)
      wrap-transit-response
      wrap-transit-body
      (wrap-resource "public")))

(defn dev-handler [app-state]
  (fn [req]
    ((-> (prod-handler app-state)
         (wrap-resource "public"))
     req)))

(defrecord WebServer [port handler app-state shut-down]
  component/Lifecycle
  (start [component]
    (if shut-down ;; don't start server if shut-down function present - server already started
      component
      (let [request-handler (handler app-state)
            server (httpkit/run-server request-handler {:port port})] ;; server constructor returns shutdown-fn
        (assoc component :shut-down server))))
  (stop [component]
    (when-let [shutdown-fn (:shut-down component)]
      (shutdown-fn))
    (dissoc component :shut-down)))

(defn dev-server [web-port]
  (map->WebServer {:port 3000
                   :handler dev-handler}))

(defn prod-server [web-port]
  (map->WebServer {:port 3000
                   :handler prod-handler}))
