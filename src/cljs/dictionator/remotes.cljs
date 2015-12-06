(ns dictionator.remotes
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan close! put! take! >! <!] :as async]
            [cognitect.transit :as t]
            [om.next :as om]))

(def r (t/reader :json))
(def w (t/writer :json))

(defn- setup-on-message [ws-conn read-channel write-channel]
  (set! (.-onmessage ws-conn)
        (fn [data]
          ;; read ws conn messages, transform to transit and put on channel
          (let [d (t/read r (.-data data))]
            (put! read-channel d))))
  (set! (.-onclose ws-conn)
        (fn [_]
          (close! read-channel)
          (close! write-channel)))
  ;; read messages and transform them to transit format before sending them via ws conn
  (go-loop []
    (when-some [v (<! write-channel)]
      (try
        (.send ws-conn (t/write w v))
        (catch js/Object ex
          (.log js/console "error during ws sending")))
      (recur)))
  ;; send a ping every 10 sec
  (go-loop []
    (<! (async/timeout 10000))
    (>! write-channel :ping)
    (recur)))

(defn open-ws-channel [ws-uri]
  (let [protocol (if (= "https:" (-> js/window .-location .-protocol))
                   "wss" "ws")
        ws-conn (js/WebSocket. (str protocol "://" (-> js/window .-location .-host) ws-uri))
        read-channel (chan)
        write-channel (chan)]
    (set! (.-onopen ws-conn)
          (fn [_]
            (.log js/console "Connected via websocket")
            (setup-on-message ws-conn read-channel write-channel)))
    {:read read-channel
     :write write-channel}))

(defn send
  [{:keys [read write]} {:keys [remote]} cb]
  (put! write remote)
  (take! read (fn [data]
                (.log js/console (str "Received: " data))
                (cb data))))
