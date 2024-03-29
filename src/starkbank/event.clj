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
  (:import [com.starkbank Event])
  (:require [starkbank.user]
            [starkbank.transfer]
            [starkbank.boleto]
            [starkbank.boleto-payment]
            [starkbank.invoice]
            [starkbank.deposit]
            [starkbank.boleto-holmes]
            [starkbank.brcode-payment]
            [starkbank.darf-payment]
            [starkbank.tax-payment]
            [starkbank.utility-payment])
  (:use [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :created (.created java-object)
      :is-delivered (.isDelivered java-object)
      :subscription (.subscription java-object)
      :workspace-id (.workspaceId java-object)
      :log (case (.subscription java-object)
            "transfer" (#'starkbank.transfer.log/java-to-clojure (.log java-object))
            "invoice" (#'starkbank.invoice.log/java-to-clojure (.log java-object))
            "deposit" (#'starkbank.deposit.log/java-to-clojure (.log java-object))
            "boleto" (#'starkbank.boleto.log/java-to-clojure (.log java-object))
            "boleto-holmes" (#'starkbank.boleto-holmes.log/java-to-clojure (.log java-object))
            "brcode-payment" (#'starkbank.brcode-payment.log/java-to-clojure (.log java-object))
            "boleto-payment" (#'starkbank.boleto-payment.log/java-to-clojure (.log java-object))
            "darf-payment" (#'starkbank.darf-payment.log/java-to-clojure (.log java-object))
            "utility-payment" (#'starkbank.utility-payment.log/java-to-clojure (.log java-object))
            "tax-payment" (#'starkbank.tax-payment.log/java-to-clojure (.log java-object))
            (.log java-object))
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        is-delivered "is-delivered"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "isDelivered" is-delivered
        }
      ))))

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        is-delivered "is-delivered"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "isDelivered" is-delivered
        }
      ))))

(defn- clojure-update-to-java
  ([clojure-map]
    (let [{
        is-delivered "is-delivered"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "isDelivered" is-delivered
        }
      ))))

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
    (map java-to-clojure (Event/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event/query java-params (#'starkbank.user/get-java-user user)))))

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
    (def event-page (Event/page))
    (def cursor (.cursor event-page))
    (def events (map java-to-clojure (.events event-page)))
    {:events events, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def event-page (Event/page java-params))
    {:events (map java-to-clojure (.events event-page)), :cursor (.cursor event-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def event-page (Event/page java-params (#'starkbank.user/get-java-user user)))
    {:events (map java-to-clojure (.events event-page)), :cursor (.cursor event-page)}))

(defn get
  "Receive a single notification Event map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Event map with updated attributes"
  ([id]
    (java-to-clojure
      (Event/get id)))

  ([id, user]
    (java-to-clojure
      (Event/get
        id
        (#'starkbank.user/get-java-user user)))))

(defn delete
  "Delete a list of notification Event entities previously created in the Stark Bank API

  ## Parameters (required):
    - `:id` [string]: Event unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - deleted Event map"
  ([id]
    (java-to-clojure
      (Event/delete id)))

  ([id, user]
    (java-to-clojure
      (Event/delete
        id
        (#'starkbank.user/get-java-user user)))))

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
    (java-to-clojure
      (Event/update id (clojure-update-to-java params))))

  ([id, params, user]
    (java-to-clojure
      (Event/update
        id
        (clojure-update-to-java params)
        (#'starkbank.user/get-java-user user)))))

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
    (java-to-clojure
      (Event/parse content signature)))

  ([content, signature, user]
    (java-to-clojure
      (Event/parse
        content
        signature
        (#'starkbank.user/get-java-user user)))))

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
  (:import [com.starkbank Event$Attempt])
  (:require [starkbank.event :as event])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :code (.code java-object)
      :message (.message java-object)
      :webhook-id (.webhookId java-object)
      :event-id (.eventId java-object)
      :created (.created java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        event-ids "event-ids"
        webhook-ids "webhook-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "eventIds" (if (nil? event-ids) nil (into-array String event-ids))
          "webhookIds" (if (nil? webhook-ids) nil (into-array String webhook-ids))
        }
      ))))

(defn- clojure-page-to-java
  ([clojure-map]
    (let [
      {
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        event-ids "event-ids"
        webhook-ids "webhook-ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "eventIds" (if (nil? event-ids) nil (into-array String event-ids))
          "webhookIds" (if (nil? webhook-ids) nil (into-array String webhook-ids))
        }
      ))))

(defn get
  "Receive a single Event.Attempt object previously created by the Stark Bank API by its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Attempt map with updated attributes"
  ([id]
    (java-to-clojure
      (Event$Attempt/get id)))

  ([id, user]
    (java-to-clojure
      (Event$Attempt/get
        id
        (#'starkbank.user/get-java-user user)))))

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
    (map java-to-clojure (Event$Attempt/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event$Attempt/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Event$Attempt/query java-params (#'starkbank.user/get-java-user user)))))

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
    (def attempt-page (Event$Attempt/page))
    (def cursor (.cursor attempt-page))
    (def attempts (map java-to-clojure (.attempts attempt-page)))
    {:attempts attempts, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def attempt-page (Event$Attempt/page java-params))
    {:attempts (map java-to-clojure (.attempts attempt-page)), :cursor (.cursor attempt-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def attempt-page (Event$Attempt/page java-params (#'starkbank.user/get-java-user user)))
    {:attempts (map java-to-clojure (.attempts attempt-page)), :cursor (.cursor attempt-page)}))
