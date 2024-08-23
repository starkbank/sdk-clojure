(ns starkbank.webhook
  "A Webhook is used to subscribe to notification events on a user-selected endpoint.
  Currently available services for subscription are transfer, boleto, boleto-payment,
  and utility-payment

  ## Parameters (required):
    - `:url` [string]: Url that will be notified when an event occurs.
    - `:subscriptions` [list of strings]: list of any non-empty combination of the available services. ex: [\"transfer\", \"deposit\", \"brcode-payment\"]

  ## Attributes:
    - `:id` [string, default nil]: unique id returned when the webhook is created. ex: \"5656565656565656\""
  (:refer-clojure :exclude [get set])
  (:require [starkbank.settings :refer [credentials]]
            [starkbank.utils.rest :refer [delete-id get-id get-page get-stream post-single]]))

(defn- resource []
  "webhook")

(defn create
  "Send a single Webhook subscription for creation in the Stark Bank API

  ## Parameters (required):
    - `:url` [string]: url to which notification events will be sent to. ex: \"https://webhook.site/60e9c18e-4b5c-4369-bda1-ab5fcd8e1b29\"
    - `:subscriptions` [list of strings]: list of any non-empty combination of the available services. ex: [\"transfer\", \"boleto-payment\"]

  ## Parameters (optional):
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Webhook map with updated attributes"
  ([webhook-params]
    (-> (post-single @credentials (resource) webhook-params {})))

  ([webhook-params, user]
    (-> (post-single user (resource) webhook-params {}))))

(defn query
  "Receive a stream of Webhook subcription maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Webhook maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 Webhook maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :webhooks and :cursor:
      - `:webhooks`: list of webhook maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of webhooks"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single Webhook subscription map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Webhook map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

(defn delete
  "Delete a Webhook subscription entity previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: Webhook unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - deleted Webhook map"
  ([id]
    (-> (delete-id @credentials (resource) id)))

  ([id, user]
    (-> (delete-id user (resource) id))))
