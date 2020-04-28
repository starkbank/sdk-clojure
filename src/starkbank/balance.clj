(ns starkbank.balance
  "Used to generate new API-compatible key pairs."
  (:import [com.starkbank Balance])
  (:use [starkbank.user]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :amount (.amount java-object)
      :currency (.currency java-object)
      :updated (.updated java-object)
    }))

(defn get
  "gets balance"
  ([] (->
    (Balance/get)
    (java-to-clojure)))

  ([user] (-> 
    user
    (#'starkbank.user/get-java-project)
    (Balance/get)
    (java-to-clojure))))
