(ns starkbank.boleto-payment
  "When you initialize a BoletoPayment, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (conditionally required):
    - `:line` [string, default nil]: Number sequence that describes the payment. Either 'line' or 'bar-code' parameters are required. If both are sent, they must match. ex: \"34191.09008 63571.277308 71444.640008 5 81960000000062\"
    - `:bar-code` [string, default nil]: Bar code number that describes the payment. Either 'line' or 'barCode' parameters are required. If both are sent, they must match. ex: \"34195819600000000621090063571277307144464000\"

  ## Parameters (required):
    - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:description` [string]: Text to be displayed in your statement (min. 10 characters). ex: \"payment ABC\"

  ## Parameters (optional):
    - `:scheduled` [string, default today]: payment scheduled date. ex: ~D[2020-03-25]
    - `:tags` [list of strings]: list of strings for tagging

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the payment is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current payment status. ex: \"processing\" or \"success\"
    - `:amount` [integer, default nil]: amount automatically calculated from line or bar-code. ex: 23456 (= R$ 234.56)
    - `:fee` [integer, default nil]: fee charged when a boleto payment is created. ex: 200 (= R$ 2.00)
    - `:created` [string, default nil]: creation datetime for the payment. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:require [starkbank.utils.rest :refer [delete-id get-content get-id
                                          get-page get-stream post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "boleto-payment")

(defn create
  "Send a list of BoletoPayment maps for creation in the Stark Bank API

  ## Parameters (required):
    - `payments` [list of BoletoPayment maps]: list of BoletoPayment maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of BoletoPayment maps with updated attributes"
  ([payments]
    (-> (post-multi @credentials (resource) payments {})))

  ([payments, user]
   (-> (post-multi user (resource) payments {}))))

(defn query
  "Receive a stream of BoletoPayment maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of BoletoPayment maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 BoletoPayment maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :payments and :cursor:
      - `:payments`: list of payment maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of payments"
  ([]
    (-> (get-page @credentials (resource) {})))

  ([params]
    (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params)))
  )

(defn get
  "Receive a single BoletoPayment map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BoletoPayment map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {})))
  )

(defn delete
  "Delete a BoletoPayment entity previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: BoletoPayment unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - deleted BoletoPayment map"
  ([id]
    (-> (delete-id @credentials (resource) id)))

  ([id, user]
   (-> (delete-id user (resource) id)))
  )

(defn pdf
  "Receive a single BoletoPayment pdf file generated in the Stark Bank API by passing its id.
  Only valid for boleto payments with \"success\" status.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BoletoPayment pdf file content"
  ([id]
    (-> (get-content @credentials (resource) id "pdf" {})))

  ([id, user]
    (-> (get-content user (resource) id "pdf" {})))
)
