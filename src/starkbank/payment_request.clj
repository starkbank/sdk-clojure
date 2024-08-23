(ns starkbank.payment-request
  "A PaymentRequest is an indirect request to access a specific cash-out service
  (such as Transfer, BrcodePayments, etc.) which goes through the cost center
  approval flow on our website. To emit a PaymentRequest, you must direct it to
  a specific cost center by its ID, which can be retrieved on our website at the
  cost center page.

  ## Parameters (required):
  - `:center-id` [string]: unique id returned when PaymentRequest is created. ex: \"5656565656565656\"
  - `:payment` [Transfer, BrcodePayment, BoletoPayment, DarfPayment, TaxPayment, UtilityPayment, Transaction or map]: payment entity that should be approved and executed.
  - `:type` [string]: payment type, inferred from the payment parameter if it is not a map. ex: \"transfer\", \"brcode-payment\"

  ## Parameters (optional):
  - `:due` [string]: Payment target date in ISO format.
  - `:tags` [list of strings]: list of strings for tagging

  Attributes (return-only):
  - `:id` [string]: id of the object
  - `:amount` [integer]: PaymentRequest amount. ex: 100000 = R$1.000,00
  - `:status` [string]: current PaymentRequest status.ex: \"pending\" or \"approved\"
  - `:actions` [list of PaymentRequest.Action, default nil]: list of actions that are affecting this PaymentRequest.ex: [{\"type\": \"member\", \"id\": \"56565656565656, \"action\": \"requested\"}]
  - `:updated` [string]: latest update datetime for the PaymentRequest. ex: \"2020-03-26T19:32:35.418698+00:00\"
  - `:created` [string]: creation datetime for the PaymentRequest. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get type])
  (:require [starkbank.utils.rest :refer [get-page get-stream post-multi]]
            [starkbank.settings :refer [credentials]]) 
  )

(defn- resource []
  "payment-request")

(defn create
  "Send a list of PaymentRequest maps for creation in the Stark Bank API

  ## Parameters (required):
    - `requests` [list of PaymentRequest maps]: list of PaymentRequest maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of PaymentRequest maps with updated attributes"
  ([requests]
   (-> (post-multi @credentials (resource) requests  {})))

  ([requests, user]
   (-> (post-multi user (resource) requests  {}))))

(defn query
  "Receive a stream of PaymentRequest maps previously created by this user in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of objects to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil] date filter for objects created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil] date filter for objects created only before specified date. ex: \"2020-03-10\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"-created\" or \"-due\".
    - `:status` [string, default nil]: filter for status of retrieved objects. ex: \"success\" or \"failed\"
    - `:type` [string, default nil]: payment type, inferred from the payment parameter if it is not a map. ex: \"transfer\", \"brcode-payment\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of PaymentRequest maps with updated attributes"
  ([]
     (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 PaymentRequest maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of objects to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil] date filter for objects created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil] date filter for objects created only before specified date. ex: \"2020-03-10\"
    - `:sort` [string, default \"-created\"]: sort order considered in response. Valid options are \"-created\" or \"-due\".
    - `:status` [string, default nil]: filter for status of retrieved objects. ex: \"success\" or \"failed\"
    - `:type` [string, default nil]: payment type, inferred from the payment parameter if it is not a map. ex: \"transfer\", \"brcode-payment\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :requests and :cursor:
      - `:requests`: list of request maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of requests"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))
