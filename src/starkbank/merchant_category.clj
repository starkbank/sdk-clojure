(ns starkbank.merchant-category
  "MerchantCategory's codes and types are used to define categories filters in CorporateRules.
  A MerchantCategory filter must define exactly one parameter between code and type.
  A type, such as \"food\", \"services\", etc., defines an entire group of merchant codes,
  whereas a code only specifies a specific MCC.

  ## Parameters (conditionally required):
    - `:code` [string, default nil]: category's code. ex: \"veterinaryServices\" or \"fastFoodRestaurants\"
    - `:type` [string, default nil]: category's type. ex: \"pets\" or \"food\"

  ## Attributes (return-only):
    - `:name` [string, default nil]: category's name. ex: \"Veterinary services\" or \"Fast food restaurants\"
    - `:number` [string, default nil]: category's number. ex: \"742\" or \"5814\""
  (:require [starkbank.utils.rest :refer [get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "merchant-category")

(defn query
  "Receive a stream of MerchantCategory maps available in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:search` [string, default nil]: keyword to search for code, type, name or number. ex: \"food\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of MerchantCategory maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))
