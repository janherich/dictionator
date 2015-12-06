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
   :value {:keys [:player-name]}
   :remote true})

(defmethod mutate 'dict/set-game-mode!
  [{:keys [state]} _ {:keys [mode]}]
  (cond-> {:action (fn [] (swap! state assoc
                                 :game-mode mode
                                 :current-game :loading))
           :value {:keys [:game-mode :current-game]}}
    (= :single-player mode) (assoc :remote true)))

(defmethod mutate 'dict/quess-term!
  [{:keys [state]} _ {:keys term}]
  {:remote true})

(defmulti read (fn [_ k _] k))

(defmethod read :current-game
  [{:keys [state]} _ _]
  (let [[k v :as e] (find @state :current-game)]
    (when-not (nil? e)
      (if (= v :loading)
        {:remote true}
        {:value v}))))

(defmethod read :default
  [{:keys [state]} k _]
  {:value (get @state k)})

(def parser (om/parser {:read read :mutate mutate}))
