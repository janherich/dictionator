(ns dictionator.impl.web-truth-source
  (:require [clojure.string :refer [lower-case upper-case trim split join]]
            [org.httpkit.client :as http]
            [cheshire.core :refer [parse-string]]
            [dictionator.protocols :as p]))

(def ^:private response-payload (comp #(parse-string % (comp keyword lower-case)) :body))

(deftype WebTruthSource [url truth?]
  p/TruthSource
  (exists? [this term]
    (truth? term @(http/get (format url (join "+" (split term #"\s")))))))

(let [truth? (fn [term response]
               (= (-> term trim upper-case)
                  (some-> response response-payload :title trim upper-case)))]
  (def movie-truth-source
    (WebTruthSource. "http://www.omdbapi.com/?t=%s" truth?)))
