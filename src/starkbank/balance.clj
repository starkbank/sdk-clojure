(ns starkbank.balance
  "The Balance map displays the current balance of the workspace,
  which is the result of the sum of all transactions within this
  workspace. The balance is never generated by the user, but it
  can be retrieved to see the information available.

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when Balance is created. ex: \"5656565656565656\"
    - `:amount` [integer, default nil]: current balance amount of the workspace in cents. ex: 200 (= R$ 2.00)
    - `:currency` [string, default nil]: currency of the current workspace. Expect others to be added eventually. ex: \"BRL\"
    - `:updated` [string, default nil]: update datetime for the balance. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
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
  "Receive the Balance entity linked to your workspace in the Stark Bank API

  ## Options:
    - `:user` [Project]: Project map returned from starkbank.user/project. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Balance map with updated attributes"
  ([] (->
    (Balance/get)
    (java-to-clojure)))

  ([user] (-> 
    user
    (#'starkbank.user/get-java-project)
    (Balance/get)
    (java-to-clojure))))
