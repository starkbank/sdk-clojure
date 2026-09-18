(ns starkbank.card-method
  "CardMethod's codes are used to define methods filters in CorporateRules.

  ## Parameters (required):
    - `:code` [string]: method's code. Options: \"chip\", \"token\", \"server\", \"manual\", \"magstripe\", \"contactless\"

  ## Attributes (return-only):
    - `:name` [string, default nil]: method's name. ex: \"token\"
    - `:number` [string, default nil]: method's number. ex: \"81\""
  (:require [starkbank.utils.rest :refer [get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "card-method")

(defn query
  "Receive a stream of CardMethod maps previously created in the Stark Bank API.

  ## Options:
    - `:search` [string, default nil]: keyword to search for code, name, number or short-code
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of CardMethod maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user]
    (-> (get-stream user (resource) params))))
