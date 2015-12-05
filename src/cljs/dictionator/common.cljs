(ns dictionator.common
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))


;; Footer is the same for all screens

(def footer
  (dom/div #js {:id "footer"}
           (dom/p #js {:className "footer_p"}
                  "Made with ♥ CodeCouple")))
