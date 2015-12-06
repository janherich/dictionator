(ns dictionator.parser
  (:require [dictionator.protocols :as p]))

(defmulti readf (fn [_ k _] k))

(defmethod readf :default
  [{:keys [app-state channel]} k _]
  {:value (get @app-state k)})

(defmethod readf :current-game
  [{:keys [app-state channel]} _ _]
  {:value (:current-game @app-state)})

(defmulti mutatef (fn [_ k _] k))

(defmethod mutatef 'dict/set-player-name!
  [{:keys [app-state channel]} _ {:keys [name]}]
  {:action (fn []
             (let [state (swap! app-state assoc-in [:players channel :name] name)]
               {:player-name (get-in state [:players channel :name])}))
   :value {:keys [:player-name]}})

(defmethod mutatef 'dict/set-game-mode!
  [{:keys [app-state channel]} _ _]
  {:action (fn []
             (let [state (swap! app-state
                                (fn [{:keys [truth-sources] :as state}]
                                  (let [[truth-source] truth-sources
                                        player-id (get-in state [:players channel :player-id])
                                        game-name (str "singleplayer-" player-id)]
                                    (-> state
                                        (assoc-in [:players channel :game] game-name)
                                        (update-in [:games] assoc game-name
                                                   {:players
                                                    {player-id {:points 0
                                                                :last-try nil}}
                                                    :used-words #{}
                                                    :current-word nil
                                                    :single-player? true
                                                    :truth-source truth-source})))))]
               (let [player-id (get-in state [:players channel :player-id])
                     game-name (str "singleplayer-" player-id)]
                 {:game-mode :single-player
                  :current-game (-> state
                                    (get-in [:games game-name])
                                    (dissoc :truth-source)
                                    (dissoc :used-words))})))
   :value {:keys [:current-game :game-mode]}})
;; TODO finish
(defn quess-word [app-state game player word]
  (let [{:keys [truth-source used-words]} (get-in @app-state [:games game])]
    (if (not (contains? used-words word))
      (if (p/exists? truth-source word)
        (swap! app-state update-in [:games game]
               (fn [{:keys [current-word] :as game}]
                 (if (or (nil? current-word)
                         (= (last current-word)
                            (first word)))
                   (-> game
                       (assoc :current-word word)
                       (update-in [:used-words] conj word)
                       (assoc-in [:players player :last-try]
                                 {:message "Good quess"
                                  :success true})
                       (update-in [:players player :points] inc))
                   (-> game
                       (assoc-in [:players player :last-try]
                                 {:message (format "There is no %s named % known"
                                                   (p/truth-term truth-source)
                                                   word)
                                  :success false})))))
        (swap! app-state assoc-in [:games game :players player :last-try]
               {:message (format "There is no %s %s known"
                                 (p/truth-term truth-source)
                                 word)
                :success false}))
      (swap! app-state assoc-in [:games game :players player :last-try]
             {:message (format "%s %s was already used in the game"
                               (p/truth-term truth-source)
                               word)
              :success false}))))

(defmethod mutatef 'dict/guess-term!
  [{:keys [app-state channel]} _ {:keys [name]}]
  {:action (fn []
             ;;(let [state (swap! app-state)])
             )
   :value {:keys [:current-word]}})
