(ns dictionator.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(def data {:actual-screen :initial-screen})


(def footer
  (dom/div #js {:id "footer"}
           (dom/p #js {:className "footer_p"}
                  "Made with ♥ CodeCouple")))


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
                                                           :input-name-screen)
                                                          (.log js/console (om/props this)))}
                                          (dom/div #js {:className "push_button red"}
                                                   "Let's play")))))))

(def lets-play (om/factory LetsPlay))

(defui InputName
  Object
  (render [this]
          (dom/div #js {:className "input-name"}
                   (dom/div #js {:className "col-md-4"} "")
                   (dom/div #js {:className "col-lg-4 text-center"}
                            (dom/div #js {:className "input-group"}
                                     (dom/input #js {:type "text"
                                                     :className "form-control"
                                                     :placeholder "Your name"})
                                     (dom/span #js {:className "input-group-btn"}
                                               (dom/button #js {:className "btn btn-default"
                                                                :type "button"}
                                                           "➔"))))
                   (dom/div #js {:className "col-lg-4"} ""))))

(def input-name (om/factory InputName))

(defn screens [actual-screen screen-1 screen-2]
  (cond
    (= actual-screen :initial-screen) screen-1
    (= actual-screen :input-name-screen) screen-2))

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
                     footer))))

(def basic-wrapper (om/factory BasicWrapper))

(defui RootView
  static om/IQuery
  (query [this]
         [:actual-screen])
  Object
  (render [this]
          (let [{:keys [actual-screen]} (om/props this)]
            (basic-wrapper {:actual-screen actual-screen
                            :submit-change-screen (fn [changed-screen]
                                                    (om/transact! this `[(screens/update-input! {:actual-screen ~changed-screen})]))}))))

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
