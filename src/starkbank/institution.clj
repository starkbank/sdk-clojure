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
  (:require [starkbank.utils.rest :refer [get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "institution")

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
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))
