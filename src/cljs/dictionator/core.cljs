(ns dictionator.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [dictionator.parser :as p]
            [dictionator.singleplayer :as singleplayer]
            [dictionator.multiplayer :as mulatiplayer]
            [dictionator.common :as common]))

(def data {:initialized? false
           :player-name nil
           :game-mode nil})


;; Rooting component with the main logic
(defui RootView
  static om/IQuery
  (query [this]
         `[:initialized? :player-name :game-mode;;{:game ~(om/get-query common/Game)}
           ])
  Object
  (render [this]
          (let [{:keys [initialized? player-name game-mode]} (om/props this)]
            (if (and initialized? player-name game-mode)
              (common/game-menu {:game-mode game-mode})
              (common/basic-menu {:initialized? initialized?
                                  :player-name player-name
                                  :game-mode game-mode})))))

(def reconciler
  (om/reconciler
   {:state data
    :parser p/parser}))

(om/add-root! reconciler RootView (gdom/getElement "content"))
