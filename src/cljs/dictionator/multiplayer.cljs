(ns dictionator.multiplayer
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))


(defui ExistingGame
  static om/IQuery
  (query [this]
         [:team-name])
  Object
  (render [this]
          (let [{:keys [team-name]} (om/props this)]
            (dom/li #js {:className "list-group-item"}
                    (.log js/console  team-name)
                    team-name
                    (dom/button #js {:type "button"
                                     :className "btn btn-success right small"}
                                "Join")))))

(def existing-game (om/factory ExistingGame))

;; Component for screen for new-game or join existing game
(defui MultiplayerChoosingGame
  static om/IQuery
  (query [this]
         [:teams])
  Object
  (render [this]
          (let [{:keys [teams]} (om/props this)]
            (.log js/console teams)
            (dom/div #js {:id "join game"}
                     (dom/div #js {:id "row"}
                              (dom/div #js {:className "col-md-12 text-center"}
                                       (dom/h2 #js {:className "need-top-bottom-margin"}
                                               "Join game"))
                              (dom/div #js {:className "col-md-3"} "")
                              (dom/div #js {:className "col-md-6 center"}
                                       (dom/ul #js {:className "list-group"}
                                               (map existing-game teams)))
                              (dom/div #js {:className "col-md-12 text-center"}
                                       (dom/h2 #js {:className "game-name"}
                                               "or Create a new one"))
                              (dom/div #js {:className "col-md-4"} "")
                              (dom/div #js {:className "col-md-4 center add-game"}
                                       (dom/div #js {:className "input-group"}
                                                (dom/input #js {:type "text"
                                                                :className "form-control"
                                                                :placeholder "Name of your game"})
                                                (dom/span #js {:className "input-group-btn"}
                                                          (dom/button #js {:className "btn btn-default"
                                                                           :type "button"} "➔")))))))))


;; Factory for component for screen for new-game or join existing game

(def multiplayer-choosing-game (om/factory MultiplayerChoosingGame))
