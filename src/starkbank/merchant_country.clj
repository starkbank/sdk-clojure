(ns starkbank.merchant-country
  "MerchantCountry's codes are used to define countries filters in CorporateRules.

  ## Parameters (required):
    - `:code` [string]: country's code. ex: \"BRA\"

  ## Attributes (return-only):
    - `:name` [string, default nil]: country's name. ex: \"Brazil\"
    - `:number` [string, default nil]: country's number. ex: \"076\"
    - `:short-code` [string, default nil]: country's short code. ex: \"BR\""
  (:require [starkbank.utils.rest :refer [get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "merchant-country")

(defn query
  "Receive a stream of MerchantCountry maps available in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:search` [string, default nil]: keyword to search for code, name, number or short-code. ex: \"brazil\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of MerchantCountry maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))
