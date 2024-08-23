(ns starkbank.darf-payment
  "When you initialize a DarfPayment, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the objects
  to the Stark Bank API and returns the list of created objects.

  ## Parameters (required):
    - `:description` [string]: Text to be displayed in your statement (min. 10 characters). ex: \"payment ABC\"
    - `:revenue-code` [string]: 4-digit tax code assigned by Federal Revenue. ex: \"5948\"
    - `:tax-id` [string]: tax id (formatted or unformatted) of the payer. ex: \"12.345.678/0001-95\"
    - `:competence` [string]: competence month of the service. ex: \"2021-4-30\"
    - `:nominal-amount` [long]: amount due in cents without fee or interest. ex: 23456 (= R$ 234.56)
    - `:fine-amount` [long]: fixed amount due in cents for fines. ex: 234 (= R$ 2.34)
    - `:interest-amount` [long]: amount due in cents for interest. ex: 456 (= R$ 4.56)
    - `:due` [string]: due date for payment. ex: \"2021-5-17\"

  ## Parameters (optional):
    - `:reference-number` [string]: number assigned to the region of the tax. ex: \"08.1.17.00-4\"
    - `:scheduled` [string, default today]: payment scheduled date. ex: \"2020-03-25\"
    - `:tags` [list of strings]: list of strings for tagging

  Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the payment is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current payment status. ex: \"processing\" or \"success\"
    - `:amount` [integer, default nil]: amount automatically calculated from line or bar-code. ex: 23456 (= R$ 234.56)
    - `:fee` [integer, default nil]: fee charged when a utility payment is created. ex: 200 (= R$ 2.00)
    - `:updated` [string, default nil]: latest update datetime for the payment. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string, default nil]: creation datetime for the payment. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:require [starkbank.utils.rest :refer [delete-id get-content get-id
                                          get-page get-stream post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "darf-payment")

(defn create
  "Send a list of DarfPayment objects for creation in the Stark Bank API

  ## Parameters (required):
    - `payments` [list of DarfPayment maps]: list of DarfPayment maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of DarfPayment maps with updated attributes"
  ([payments]
   (-> (post-multi @credentials (resource) payments {})) 
   )

  ([payments, user]
   (-> (post-multi user (resource) payments {}))))

(defn query
  "Receive a stream of DarfPayment objects previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of UtilityPayment maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user]
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 DarfPayment objects previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
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
    (-> (get-page user (resource) params))))

(defn get
  "Receive a single DarfPayment object previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - DarfPayment map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

(defn delete
  "Delete a DarfPayment entity previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: UtilityPayment unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - deleted DarfPayment map"
  ([id]
    (-> (delete-id @credentials (resource) id)))

  ([id, user]
    (-> (delete-id user (resource) id))))

(defn pdf
  "Receive a single DarfPayment pdf file generated in the Stark Bank API by passing its id.
  Only valid for darf payments with \"success\" status.

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - DarfPayment pdf file content"
  ([id]
    (-> (get-content @credentials (resource) id "pdf" {})))

  ([id, user]
    (-> (get-content user (resource) id "pdf" {}))))
