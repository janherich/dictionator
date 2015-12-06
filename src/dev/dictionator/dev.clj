(ns dictionator.dev
  (:require [clojure.java.io :as io]
            [clojure.string :refer []]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer :all]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [com.stuartsierra.component :as component]
            [dictionator.system :as app]
            [dictionator.protocols :as p]
            [dictionator.impl.web-truth-source :refer [movie-truth-source]]))

(def system nil)

(defn init []
  (alter-var-root #'system (constantly (app/dev-system {:web-port 300}))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system #(some-> % component/stop)))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'dictionator.dev/go))

;; server state
(def app-state
  (atom {:players {"ch-id1" {:player-id "id1"
                             :name "twiggy"
                             :game "game1"}
                   "ch-id2" {:player-id "id2"
                             :name "siggy"}
                   "ch-id3" {:player-id "id3"
                             :name "ibi"}}
         :games {"game1" {:players {"id1" {:points 0
                                           :last-try nil}
                                    "id2" {:points 0
                                           :last-try nil}
                                    "id3" {:points 0
                                           :last-try nil}}
                          :used-words #{}
                          :truth-source movie-truth-source
                          :current-word nil}}}))

(defn quess-word [app-state game player word]
  (let [{:keys [truth-source used-words]} (get-in @app-state [:games game])]
    (if (not (contains? used-words word))
      (if (p/exists? truth-source word)
        (swap! app-state update-in [:games game]
               (fn [{:keys [current-word] :as game}]
                 (if (or (nil? current-word)
                         (= (last current-word)
                            (first word)))
                   (-> game
                       (assoc :current-word word)
                       (update-in [:used-words] conj word)
                       (assoc-in [:players player :last-try]
                                 {:message "Good quess"
                                  :success true})
                       (update-in [:players player :points] inc))
                   (-> game
                       (assoc-in [:players player :last-try]
                                 {:message (format "There is no %s named % known"
                                                   (p/truth-term truth-source)
                                                   word)
                                  :success false})))))
        (swap! app-state assoc-in [:games game :players player :last-try]
               {:message (format "There is no %s %s known"
                                 (p/truth-term truth-source)
                                 word)
                :success false}))
      (swap! app-state assoc-in [:games game :players player :last-try]
             {:message (format "%s %s was already used in the game"
                               (p/truth-term truth-source)
                               word)
              :success false}))))
