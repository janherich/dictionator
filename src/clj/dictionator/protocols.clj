(ns dictionator.protocols)

(defprotocol TruthSource
  "Protocol defining unambigous truth source"
  (exists? [this term] "Is the term known by the truth source ?"))
