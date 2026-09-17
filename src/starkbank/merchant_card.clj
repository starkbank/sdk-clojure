(ns starkbank.merchant-card
  "Stores information about cards used in approved purchases; these cards can be reused for new purchases
  without creating a new MerchantSession.

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the MerchantCard is created. ex: \"5656565656565656\"
    - `:ending` [string, default nil]: last 4 digits of the card number. ex: \"1234\"
    - `:expiration` [string, default nil]: card expiration date. ex: \"2025-06\"
    - `:holder-name` [string, default nil]: name of the card holder. ex: \"Tony Stark\"
    - `:funding-type` [string, default nil]: funding type. ex: \"credit\" or \"debit\"
    - `:network` [string, default nil]: card network. ex: \"visa\"
    - `:status` [string, default nil]: current card status. ex: \"active\", \"expired\", \"canceled\" or \"blocked\"
    - `:tags` [list of strings, default nil]: tags associated with the card. ex: [\"travel\", \"food\"]
    - `:created` [string, default nil]: creation datetime for the MerchantCard. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string, default nil]: latest update datetime for the MerchantCard. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "merchant-card")

(defn get
  "Receive a single MerchantCard map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - MerchantCard map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of MerchantCard maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"active\", \"expired\", \"canceled\" or \"blocked\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of MerchantCard maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 MerchantCard maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"active\", \"expired\", \"canceled\" or \"blocked\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :cards and :cursor:
      - `:cards`: list of MerchantCard maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of cards"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))
