(ns dictionator.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [dictionator.parser :as p]
            [dictionator.remotes :as r]
            [dictionator.common :as common]))

(def data {:initialized? false
           :player-name nil
           :game-mode nil
           :current-game nil
           :games-list nil})


;; Rooting component with the main logic
(defui RootView
  static om/IQuery
  (query [this]
         '[:initialized? :player-name :game-mode :current-game])
  Object
  (render [this]
          (let [{:keys [initialized? player-name game-mode current-game]} (om/props this)]
            (if (and initialized? player-name game-mode)
              (common/game-menu {:game-mode game-mode
                                 :current-game current-game})
              (common/basic-menu {:initialized? initialized?
                                  :player-name player-name
                                  :game-mode game-mode})))))

(def reconciler
  (om/reconciler
   {:state data
    :parser p/parser
    :send (partial r/send (r/open-ws-channel "/ws"))}))

(om/add-root! reconciler RootView (gdom/getElement "content"))
