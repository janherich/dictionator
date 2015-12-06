(ns dictionator.common
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [dictionator.multiplayer :as multiplayer]))

;; Footer is the same for all screens

(def footer
  (dom/div #js {:id "footer"}
           (dom/p #js {:className "footer_p"}
                  "Made with ♥ CodeCouple")))

;; Wrapper for the game part - for single page application
(defui Game
  static om/IQuery
  (query [this]
         [:players :current-word])
  Object
  (render [this]
          (dom/span #js {:className "glyphicon glyphicon-star points"}
                    (dom/p #js {} 0))
          (dom/div #js {:className "row"}
                   (dom/div #js {:className "col-md-12 text-center previous-word"}
                            (dom/h3 #js {:className "prev"} "Previous word: ")
                            (dom/p #js {} "Herisk")
                            (dom/p #js {:className "last-letter"} "o"))
                   (dom/div #js {:className "col-md-12 text-center"}
                            (dom/form #js {}
                                      (dom/input #js {:className "input-word"
                                                      :type "text"
                                                      :placeholder ""}))))
          footer))

;; Factory for game wrapper
(def game (om/factory Game))

(defui GameMenu
  static om/IQuery
  (query [this]
         [:game-mode])
  Object
  (render [this]
          (let [{:keys [game-mode]} (om/props this)]
            (dom/div #js {:className "wrapper-game"}
                     (dom/div #js {:className "row back-button"}
                              (dom/a #js {:href "#"
                                          :className "back"}
                                     "⇦ Leave game"))
                     (if (= :single-player game-mode)
                       (dom/div #js {} "Single player !")
                       (multiplayer/multiplayer-choosing-game {}))
                     footer))))

(def game-menu (om/factory GameMenu))

;; First screen button
(defui LetsPlay
  Object
  (render [this]
          (let [{:keys [init-game!]} (om/get-computed this)]
            (dom/div #js {:className "lets-play"}
                     (dom/div #js {:className "col-md-5"})
                     (dom/div #js {:className "col-md-2 center"}
                              (dom/button #js {:type "submit"
                                               :onClick (fn [event]
                                                          (.preventDefault event)
                                                          (init-game!))}
                                          (dom/div #js {:className "push_button red"}
                                                   "Let's play")))))))

;; Factory for the let's play button (first screen)
(def lets-play (om/factory LetsPlay))

;; Input form for second screen
(defui InputName
  Object
  (initLocalState [this]
                  {:form-input ""})
  (render [this]
          (let [{:keys [set-player-name!]} (om/get-computed this)]
            (dom/div #js {:className "input-name"}
                     (dom/div #js {:className "col-md-4"} "")
                     (dom/div #js {:className "col-lg-4 text-center"}
                              (dom/form #js {:onSubmit (fn [event]
                                                         (.preventDefault event)
                                                         (set-player-name! (:form-input (om/get-state this)))
                                                         (om/update-state! this assoc :form-input ""))}
                                        (dom/div #js {:className "input-group"}
                                                 (dom/input #js {:type "text"
                                                                 :className "form-control"
                                                                 :placeholder "Your name"
                                                                 :value (:form-input (om/get-state this))
                                                                 :onChange (fn [event]
                                                                             (om/update-state! this assoc :form-input (.. event -target -value)))})
                                                 (dom/span #js {:className "input-group-btn"}
                                                           (dom/button #js {:className "btn btn-default"
                                                                            :type "submit"}
                                                                       "➔")))))
                     (dom/div #js {:className "col-lg-4"} "")))))


;; Factory for Input form
(def input-name (om/factory InputName))

;; Component for selecting game (multiplayer/singleplayer)
(defui SelectGame
  Object
  (render [this]
          (dom/div #js {:className "select-game"}
                   (dom/div #js {:className "col-md-12"}
                            (dom/div #js {:className "col-md-5"} "")
                            (dom/div #js {:className "col-md-2 center meed-margin"}
                                     (dom/a #js {:href "#"
                                                 :className "push_button red"}
                                            "Singleplayer"))
                            (dom/div #js {:className "col-md-5"} ""))
                   (dom/div #js {:className "col-md-12 need-margin"}
                            (dom/div #js {:className "col-md-5"})
                            (dom/div #js {:className "col-md-2 center"}
                                     (dom/a #js {:href "#"
                                                 :className "push_button blue"}
                                            "Multiplayer"))
                            (dom/div #js {:className "col-md-5"})))))

;; Factory for selecting game
(def select-game (om/factory SelectGame))

;; Basic menu for first two screens if singleplayer, first 3 screens if multiplayer
(defui BasicMenu
  static om/IQuery
  (query [this]
         [:initialized? :player-name])
  Object
  (render [this]
          (let [{:keys [initialized? player-name]} (om/props this)
                set-player-name! (fn [name]
                                   (om/transact! this `[(dict/set-player-name! {:name ~name})]))
                set-game-mode! (fn [mode]
                                 (om/transact! this `[(dict/set-game-mode! {:mode ~mode})]))]
            (dom/div #js {:className "wrapper"}
                     (dom/div #js {:className "col-md-12 text-center"
                                   :id "playground"}
                              (dom/div #js {:className "row"}
                                       (dom/div #js {:className "col-md-12 text-center"
                                                     :id "playground"}
                                                (dom/h1 #js {} (if (not initialized?)
                                                                 "Dictionator"
                                                                 (if (not player-name)
                                                                   "Input your name"
                                                                   "")))
                                                (dom/div #js {:className "row"}
                                                         (if (not initialized?)
                                                           (lets-play (om/computed {} {:init-game! #(om/transact! this `[(dict/init-game!)])}))
                                                           (if (not player-name)
                                                             (input-name (om/computed {} {:set-player-name! set-player-name!}))
                                                             (select-game))))
                                                (dom/div #js {:id "col-md-5"}))))
                     footer))))

;; Factory for basic menu
(def basic-menu (om/factory BasicMenu))
