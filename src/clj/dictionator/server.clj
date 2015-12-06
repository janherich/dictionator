(ns dictionator.server
  (:require [com.stuartsierra.component :as component]
            [om.next.server :as om]
            [org.httpkit.server :as httpkit :refer [with-channel on-close on-receive send!]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [response]]
            [dictionator.parser :as p]
            [dictionator.util :as util]))

(defn add-player [app-state channel]
  (assoc-in app-state [:players channel]
            {:name nil
             :player-id (java.util.UUID/randomUUID)}))

(defn remove-player [app-state channel]
  (let [player-id (get-in app-state [:players channel :player-id])]
    (-> app-state
        (update-in [:players] dissoc channel)
        (update-in [:games]
                   (fn [games]
                     (reduce (fn [acc [game-name {:keys [players] :as game-state}]]
                               (assoc acc game-name (assoc game-state :players
                                                           (->> players
                                                                (filter #(= player-id (first %)))
                                                                (into {})))))
                             {}
                             games))))))

(defn ws-handler [{:keys [app-state] :as req}]
  (with-channel req channel
    (swap! app-state add-player channel)
    (on-close channel (fn [status]
                        (swap! app-state remove-player channel)
                        (println "channel closed:" status)))
    (on-receive channel (fn [unmarshalled]
                          (let [data (util/read-transit unmarshalled)]
                            (when-not (= :ping data)
                              (let [parse-result ((om/parser {:read p/readf
                                                              :mutate p/mutatef})
                                                  {:app-state app-state
                                                   :channel channel}
                                                  data)]
                                (if-let [{:keys [result keys]} (-> parse-result first second)]
                                  (send! channel (util/write-transit (select-keys result keys)))
                                  (send! channel (util/write-transit parse-result))))))))))

(defn wrap-state [handler app-state]
  (fn [req]
    (handler (assoc req :app-state app-state))))

(defn prod-handler [app-state]
  (-> ws-handler
      (wrap-state app-state)))

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
