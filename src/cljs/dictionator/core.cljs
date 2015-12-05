(ns dictionator.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [dictionator.singleplayer :as singleplayer]
            [dictionator.multiplayer :as mulatiplayer]
            [dictionator.common :as common]))


(def data {:actual-screen :initial-screen
           })

;; Rooting component with the main logic
(defui RootView
  static om/IQuery
  (query [this]
         [:actual-screen :name])
  Object
  (render [this]
          (let [{:keys [actual-screen name]} (om/props this)]
            (common/basic-wrapper {:name name
                                   :submit-name (fn [added-name]
                                                  (om/transact! this `[(screens/update-name! {:name ~added-name})]))
                                   :actual-screen actual-screen
                                   :submit-change-screen (fn [changed-screen]
                                                           (om/transact! this `[(screens/update-screen! {:actual-screen ~changed-screen})]))}))))

(defmulti mutate (fn [_ k _] k))

(defmethod mutate 'screens/update-screen!
  [{:keys [state]} _ {:keys [actual-screen]}]
  {:action (fn [] (swap! state #(assoc % :actual-screen actual-screen)))})

(defmethod mutate 'screens/update-name!
  [{:keys [state]} _ {:keys [name]}]
  {:action (fn [] (swap! state #(assoc % :name name)))})

(defn read [{:keys [state]} k _]
  {:value (get @state k)})

(def parser (om/parser {:read read :mutate mutate}))

(def reconciler
  (om/reconciler
   {:state data
    :parser parser}))

(om/add-root! reconciler RootView (gdom/getElement "content"))
