(ns dictionator.singleplayer
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [dictionator.common :as common]))



;; Wrapper for the game part - for single page application
(defui GameWrapper
  static om/IQuery
  (query [this]
         [])
  Object
  (render [this]
          (dom/div #js {:className "wrapper-game"}
                   (dom/div #js {:className "row back-button"}
                            (dom/a #js {:href "#"
                                        :className "back"}
                                   "⇦ Leave game"))
                   ;; (dom/p #js {:className "name"}
                   ;;        "Dajanka")
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
                   common/footer)))

;; Factory for game wrapper
(def game-wrapper (om/factory GameWrapper))
