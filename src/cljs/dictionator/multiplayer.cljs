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
                    team-name
                    (dom/button #js {:type "button"
                                     :className "btn btn-success right small"}
                                "Join")))))

(def existing-game (om/factory ExistingGame))

;; Component for screen for new-game or join existing game
(defui MultiplayerChoosingGame
  static om/IQuery
  (query [this]
         [:games])
  Object
  (initLocalState [this]
                  {:form-input ""})
  (render [this]
          (let [{:keys [games]} (om/props this)
                {:keys [submit-game-name]} (om/get-computed this)]
            (dom/div #js {:id "join game"}
                     (dom/div #js {:id "row"}
                              (dom/div #js {:className "col-md-12 text-center"}
                                       (dom/h2 #js {:className "need-top-bottom-margin"}
                                               "Join game"))
                              (dom/div #js {:className "col-md-3"} "")
                              (dom/div #js {:className "col-md-6 center"}
                                       (dom/ul #js {:className "list-group"}
                                               (map existing-game games)))
                              (dom/div #js {:className "col-md-12 text-center"}
                                       (dom/h2 #js {:className "game-name"}
                                               "or Create a new one"))
                              (dom/div #js {:className "col-md-4"} "")
                              (dom/div #js {:className "col-md-4 center add-game"}
                                       (dom/form #js {:onSubmit (fn [event]
                                                                  (.preventDefault event)
                                                                  (submit-game-name (:form-input (om/get-state this))))}
                                                 (dom/div #js {:className "input-group"}
                                                          (dom/input #js {:type "text"
                                                                          :className "form-control"
                                                                          :placeholder "Name of your game"
                                                                          :value (:form-input (om/get-state this))
                                                                          :onChange (fn [event]
                                                                                      (om/update-state! this assoc :form-input (.. event -target -value)))})
                                                          (dom/span #js {:className "input-group-btn"}
                                                                    (dom/button #js {:className "btn btn-default"
                                                                                     :type "submit"} "➔"))))))))))


;; Factory for component for screen for new-game or join existing game

(def multiplayer-choosing-game (om/factory MultiplayerChoosingGame))
