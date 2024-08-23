(ns starkbank.event
  "An Event is the notification received from the subscription to the Webhook.
  Events cannot be created, but may be retrieved from the Stark Bank API to
  list all generated updates on entities.

  ## Attributes:
    - `:id` [string]: unique id returned when the event is created. ex: \"5656565656565656\"
    - `:log` [Log]: a Log map from one of the subscribed services (Transfer.Log, Boleto.Log, BoletoPayment.log, DarfPayment.log, TaxPayment.Log or UtilityPayment.Log)
    - `:created` [string]: creation datetime for the notification event. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:is-delivered` [bool]: true if the event has been successfully delivered to the user url. ex: false
    - `:workspace-id` [string]: ID of the Workspace that generated this event. Mostly used when multiple Workspaces have Webhooks registered to the same endpoint. ex: \"4545454545454545\"
    - `:subscription` [string]: service that triggered this event. ex: \"transfer\", \"utility-payment\""
  (:refer-clojure :exclude [get update])
  (:require [starkbank.utils.rest :refer [delete-id get-id get-page get-stream
                                          patch-id]]
            [starkbank.utils.signature :refer [verify-signature]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "event")

(defn query
  "Receive a stream of notification Event maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: ~D[2020-03-25]
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: ~D[2020-03-25]
    - `:is-delivered` [bool, default nil]: filter successfully delivered events. ex: true or false
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Event maps with updated attributes"
  ([]
    (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 Event maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: ~D[2020-03-25]
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: ~D[2020-03-25]
    - `:is-delivered` [bool, default nil]: filter successfully delivered events. ex: true or false
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :events and :cursor:
      - `:events`: list of event maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of events"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single notification Event map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Event map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn delete
  "Delete a list of notification Event entities previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: Event unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - deleted Event map"
  ([id]
   (-> (delete-id @credentials (resource) id)))

  ([id, user]
    (-> (delete-id user (resource) id))))

(defn update
  "Update notification Event by passing id.
    If is-delivered is true, the event will no longer be returned on queries with is-delivered=false.

  ## Parameters (required):
    - `:id` [list of strings]: Event unique ids. ex: \"5656565656565656\"
    - `:is-delivered` [bool]: If true and event hasn't been delivered already, event will be set as delivered. ex: true

  ## Parameters (optional):
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - target Event with updated attributes"
  ([id, params]
    (-> (patch-id @credentials (resource) params id)))

  ([id, params, user]
   (-> (patch-id user (resource) params id))))

(defn parse
  "Create a single Event map received from event listening at subscribed user endpoint.
  If the provided digital signature does not check out with the StarkBank public key, an \"invalidSignature\"
  error will be returned.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `cache-pid` [PID, default nil]: PID of the process that holds the public key cache, returned on previous parses. If not provided, a new cache process will be generated.
    - `user` [Project]: Project map returned from StarkBank.project(). Only necessary if default project has not been set in configs.

  ## Return:
    - Event map with updated attributes
    - Cache PID that holds the Stark Bank public key in order to avoid unnecessary requests to the API on future parses"
  ([content, signature]
   (-> (verify-signature content signature @credentials)))

  ([content, signature, user]
    (-> (verify-signature content signature user))))
