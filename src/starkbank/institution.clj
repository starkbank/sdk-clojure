(ns starkbank.institution
  "This resource is used to get information on the institutions that are recognized by the Brazilian Central Bank.
  Besides the display name and full name, they also include the STR code (used for TEDs) and the SPI Code
  (used for Pix) for the institutions. Either of these codes may be empty if the institution is not registered on
  that Central Bank service.

  ## Parameters:
    - `:display-name` [string]: short version of the institution name that should be displayed to end users. ex: \"Stark Bank\"
    - `:name` [string]: full version of the institution name. ex: \"Stark Bank S.A.\"
    - `:spi-code` [string]: SPI code used to identify the institution on Pix transactions. ex: \"20018183\"
    - `:str-code` [string]: STR code used to identify the institution on TED transactions. ex: \"123\""
(:refer-clojure :exclude [get set])
  (:import [com.starkbank Institution])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :display-name (.displayName java-object)
      :name (.name java-object)
      :spi-code (.spiCode java-object)
      :str-code (.strCode java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        search "search"
        spi-codes "spi-codes"
        str-codes "str-codes"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "search" search
          "spiCodes" (if (nil? spi-codes) nil (into-array String spi-codes))
          "strCodes" (if (nil? str-codes) nil (into-array String str-codes))
        }
      ))))

(defn query
  "Receive a list of Institution objects that are recognized by the Brazilian Central bank for Pix and TED transactions

  ## Options:
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:search` [string, default nil]: part of the institution name to be searched. ex: \"stark\"
    - `:spi-codes` [list of strings, default nil]: list of SPI (Pix) codes to be searched. ex: [\"20018183\"]
    - `:str-codes` [list of strings, default nil]: list of STR (TED) codes to be searched. ex: [\"260\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of Institution objects with updated attributes"
  ([]
    (map java-to-clojure (Institution/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Institution/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Institution/query java-params (#'starkbank.user/get-java-user user)))))
