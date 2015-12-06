(ns dictionator.util
  (:require [cognitect.transit :as t])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(defn write-transit [data]
  (let [baos (ByteArrayOutputStream.)
        _ (-> (t/writer baos :json)
              (t/write data))
        ret (.toString baos)]
    (.reset baos)
    ret))

(defn read-transit [data]
  (let [bais (ByteArrayInputStream. (.getBytes data))
        reader (t/reader bais :json)]
    (t/read reader)))
