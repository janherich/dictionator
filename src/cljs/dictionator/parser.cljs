(ns dictionator.parser
  (:require [om.next :as om]))

(defmulti mutate (fn [_ k _] k))

(defmethod mutate 'dict/init-game!
  [{:keys [state]} _ _]
  {:action (fn [] (swap! state assoc :initialized? true))
   :value {:keys [:initialized?]}})

(defmethod mutate 'dict/set-player-name!
  [{:keys [state]} _ {:keys [name]}]
  {:action (fn [] (swap! state assoc :player-name name))
   :value {:keys [:player-name]}})

(defmethod mutate 'dict/choose-game-mode!
  [{:keys [state]} _ {:keys [mode]}]
  {:action (fn [] (swap! state assoc :game-mode mode))
   :value {:keys [:game-mode]}})

(defn read [{:keys [state]} k _]
  {:value (get @state k)})

(def parser (om/parser {:read read :mutate mutate}))
