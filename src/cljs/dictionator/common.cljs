(ns dictionator.common
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))


;; Footer is the same for all screens

(def footer
  (dom/div #js {:id "footer"}
           (dom/p #js {:className "footer_p"}
                  "Made with ♥ CodeCouple")))

;; First screen button
(defui LetsPlay
  static om/IQuery
  (query [this]
         [:submit-change-screen])
  Object
  (render [this]
          (let [{:keys [submit-change-screen]} (om/props this)]
            (dom/div #js {:className "lets-play"}
                     (dom/div #js {:className "col-md-5"})
                     (dom/div #js {:className "col-md-2 center"}
                              (dom/button #js {:type "submit"
                                               :onClick (fn [event]
                                                          (.preventDefault event)
                                                          ((:submit-change-screen (om/props this))
                                                           :input-name-screen))}
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
          (dom/div #js {:className "input-name"}
                   (dom/div #js {:className "col-md-4"} "")
                   (dom/div #js {:className "col-lg-4 text-center"}
                            (dom/form #js {:onSubmit (fn [event]
                                                       (.preventDefault event)
                                                       ((:submit-name (om/props this))
                                                        {:name (:form-input (om/get-state this))})
                                                       ((:submit-change-screen (om/props this))
                                                        :choose-game)
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
                   (dom/div #js {:className "col-lg-4"} ""))))


;; Factory for Input form
(def input-name (om/factory InputName))



;; Component for selecting game (multiplayer/singleplayer)
(defui SelectGame
  Object
  (render [this]
          (dom/div #js {:class "select-game"}
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

;; Fuctction for selecting the right screen
(defn screens [actual-screen screen-1 screen-2 screen-3]
  (cond
    (= actual-screen :initial-screen) screen-1
    (= actual-screen :input-name-screen) screen-2
    (= actual-screen :choose-game) screen-3))


;; Wrapper for first two screens if singleplayer, first 3 screens if multiplayer
(defui BasicWrapper
  static om/IQuery
  (query [this]
         [:actual-screen :submit-change-screen :submit-name])
  Object
  (render [this]
          (let [{:keys [actual-screen submit-change-screen submit-name]} (om/props this)]
            (dom/div #js {:className "wrapper"}
                     (dom/div #js {:className "col-md-12 text-center"
                                   :id "playground"}
                              (dom/div #js {:className "row"}
                                       (dom/div #js {:className "col-md-12 text-center"
                                                     :id "playground"}
                                                (dom/h1 #js {} (screens actual-screen "Dictionator" "Input your name" ""))
                                                (dom/div #js {:className "row"}
                                                         (screens actual-screen (lets-play {:submit-change-screen submit-change-screen}) (input-name {:submit-name submit-name :submit-change-screen submit-change-screen}) (select-game)))
                                                (dom/div #js {:id "col-md-5"}))))
                     footer))))

;; Factory for Wrapper
(def basic-wrapper (om/factory BasicWrapper))
