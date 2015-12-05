(ns dictionator.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [dictionator.singleplayer :as singleplayer]
            [dictionator.multiplayer :as mulatiplayer]
            [dictionator.common :as common]))


(def data {:actual-screen :initial-screen})

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
                                                       (.log js/console (om/get-state this))
                                                       (om/update-state! this assoc :form-input ""))}
                                      (dom/div #js {:className "input-group"}
                                               (dom/input #js {:type "text"
                                                               :className "form-control"
                                                               :placeholder "Your name"
                                                               :value (:form-input (om/get-state this))
                                                               :onChange (fn [event]
                                                                           (om/update-state! this assoc :form-input (.. event -target -value)))
                                                               })
                                               (dom/span #js {:className "input-group-btn"}
                                                         (dom/button #js {:className "btn btn-default"
                                                                          :type "submit"}
                                                                     "➔")))))
                   (dom/div #js {:className "col-lg-4"} ""))))


;; Factory for Input form
(def input-name (om/factory InputName))


;; Fuctction for selecting the right screen
(defn screens [actual-screen screen-1 screen-2]
  (cond
    (= actual-screen :initial-screen) screen-1
    (= actual-screen :input-name-screen) screen-2))


;; Wrapper for first two screens if singleplayer, first 3 screens if multiplayer
(defui BasicWrapper
  static om/IQuery
  (query [this]
         [:actual-screen :submit-change-screen])
  Object
  (render [this]
          (let [{:keys [actual-screen submit-change-screen]} (om/props this)]
            (dom/div #js {:className "wrapper"}
                     (dom/div #js {:className "col-md-12 text-center"
                                   :id "playground"}
                              (dom/div #js {:className "row"}
                                       (dom/div #js {:className "col-md-12 text-center"
                                                     :id "playground"}
                                                (dom/h1 #js {} (screens actual-screen "Dictionator" "Input your name"))
                                                (dom/div #js {:className "row"}
                                                         (screens actual-screen (lets-play {:submit-change-screen submit-change-screen}) (input-name)))
                                                (dom/div #js {:id "col-md-5"}))))
                     common/footer))))

;; Factory for Wrapper
(def basic-wrapper (om/factory BasicWrapper))


;; Rooting component with the main logic
(defui RootView
  static om/IQuery
  (query [this]
         [:actual-screen :name])
  Object
  (render [this]
          (let [{:keys [actual-screen name]} (om/props this)]
            (if name
              (singleplayer/game-wrapper)
              (basic-wrapper {:actual-screen actual-screen
                              :submit-change-screen (fn [changed-screen]
                                                      (om/transact! this `[(screens/update-input! {:actual-screen ~changed-screen})]))})))))

(defmulti mutate (fn [_ k _] k))

(defmethod mutate 'screens/update-input!
  [{:keys [state]} _ {:keys [actual-screen]}]
  {:action (fn [] (swap! state #(assoc % :actual-screen actual-screen)))})

(defn read [{:keys [state]} k _]
  {:value (get @state k)})

(def parser (om/parser {:read read :mutate mutate}))

(def reconciler
  (om/reconciler
   {:state data
    :parser parser}))

(om/add-root! reconciler RootView (gdom/getElement "content"))
