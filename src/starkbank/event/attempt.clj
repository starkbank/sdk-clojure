(ns starkbank.event.attempt
  "When an Event delivery fails, an event attempt will be registered.
  It carries information meant to help you debug event reception issues.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:code` [string]: delivery error code. ex: badHttpStatus, badConnection, timeout
    - `:message` [string]: delivery error full description. ex: \"HTTP POST request returned status 404\"
    - `:event-id` [string]: ID of the Event whose delivery failed. ex: \"4848484848484848\"
    - `:webhook-id` [string]: ID of the Webhook that triggered this event. ex: \"5656565656565656\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "event-attempt")

(defn get
  "Receive a single Event.Attempt object previously created by the Stark Bank API by its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Attempt map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of Attempt maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:event-ids` [list of strings, default nil]: list of Event ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:webhook-ids` [list of strings, default nil]: list of Webhook ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Attempt maps with updated attributes"
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 Event.Attempt maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:event-ids` [list of strings, default nil]: list of Event ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:webhook-ids` [list of strings, default nil]: list of Webhook ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :attempts and :cursor:
      - `:attempts`: list of event maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of attempts"
  ([]
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))
