(ns starkbank.brcode-payment
    "When you initialize a BrcodePayment, the entity will not be automatically
    created in the Stark Bank API. The 'create' function sends the objects
    to the Stark Bank API and returns the list of created objects.
  
    ## Parameters (required):
      - `:brcode` [string]: String loaded directly from the QRCode or copied from the invoice. ex: \"00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A\"
      - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
      - `:description` [string]: Text to be displayed in your statement (min. 10 characters). ex: \"payment ABC\"
  
    ## Parameters (optional):
      - `:amount` [integer, default nil]: amount automatically calculated from line or barCode. ex: 23456 (= R$ 234.56)
      - `:scheduled` [string, default now]: payment scheduled date or datetime. ex: \"2020-11-25T17:59:26.249976+00:00\"
      - `:tags` [list of strings, default nil]: list of strings for tagging
  
    ## Attributes (return-only):
      - `:id` [string, default nil]: unique id returned when payment is created. ex: \"5656565656565656\"
      - `:status` [string, default nil]: current payment status. ex: \"success\" or \"failed\"
      - `:type` [string, default nil]: brcode type. ex: \"static\" or \"dynamic\"
      - `:fee` [integer, default nil]: fee charged when the brcode payment is created. ex: 200 (= R$ 2.00)
      - `:transaction-ids` [list of strings, default nil]: ledger transaction ids linked to this boleto. ex: [\"19827356981273\"] 
      - `:updated` [string, default nil]: latest update datetime for the payment. ex: \"2020-11-25T17:59:26.249976+00:00\"
      - `:created` [string, default nil]: creation datetime for the payment. ex: \"2020-11-25T17:59:26.249976+00:00\""
    (:refer-clojure :exclude [get set update])
    (:require [starkbank.utils.rest :refer [get-content get-id get-page
                                            get-stream patch-id post-multi]]
              [starkbank.settings :refer [credentials]]))

(defn- resource []
  "brcode-payment")
  
(defn create
  "Send a list of BrcodePayment maps for creation in the Stark Bank API

  ## Parameters (required):
    - `payments` [list of BrcodePayment maps]: list of BrcodePayment maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of BrcodePayment maps with updated attributes"
  ([payments]
   (-> (post-multi @credentials (resource) payments {})))

  ([payments, user]
   (-> (post-multi user (resource) payments {}))))

(defn query
  "Receive a stream of BrcodePayment maps previously created in the Stark Bank API.
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
    - stream of BrcodePayment maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params)))
  )

(defn page
  "Receive a list of up to 100 BrcodePayment maps previously created in the Stark Bank API and the cursor to the next page.
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
  "Receive a single BrcodePayment map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BrcodePayment map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user(resource) id {})))
  )

(defn pdf
  "Receive a single BrcodePayment pdf file generated in the Stark Bank API by passing its id.
  Only valid for brcode payments with \"success\" status.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BrcodePayment pdf file content"
  ([id]
   (-> (get-content @credentials (resource) id "pdf" {})))

  ([id, user]
   (-> (get-content user (resource) id "pdf" {})))
  )
  
(defn update
  "Update a BrcodePayment by passing id.

  ## Parameters (required):
    - `:id` [list of strings]: BrcodePayment unique ids. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:status` [string]: If the BrcodePayment hasn't been paid yet, you may cancel it by passing \"canceled\" in the status

  ## Return:
    - target BrcodePayment with updated attributes"
  ([id, params]
    (-> (patch-id @credentials (resource) params id)))

  ([id, params, user]
    (-> (patch-id user (resource) params id)))
  )
