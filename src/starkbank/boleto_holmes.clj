(ns starkbank.boleto-holmes
  "When you initialize a BoletoHolmes, the entity will not be automatically
  created in the Stark Bank API. The 'create' function sends the maps
  to the Stark Bank API and returns the list of created maps.

  ## Parameters (required):
    - `:boleto-id` [string]: investigated boleto entity ID. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:tags` [list of strings]: list of strings for tagging

  ## Attributes (return-only):
    - `:id` [string, default nil]: unique id returned when the holmes is created. ex: \"5656565656565656\"
    - `:status` [string, default nil]: current holmes status. ex: \"solving\" or \"solved\"
    - `:result` [string, default nil]: result of boleto status investigation. ex: \"paid\" or \"cancelled\"
    - `:updated` [string, default nil]: latest update datetime for the holmes. ex: \"2020-03-26T19:32:35.418698+00:00\"
    - `:created` [string, default nil]: creation datetime for the holmes. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank BoletoHolmes])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      boleto-id "boleto-id"
      tags "tags"}
      (stringify-keys clojure-map)]

    (defn- apply-java-hashmap [x] (java.util.HashMap. x))

    (BoletoHolmes. (java.util.HashMap.
      {
        "boletoId" boleto-id
        "tags" (if (nil? tags) nil (into-array String tags))
      }
    )))))

(defn- java-to-clojure
  ([java-object]
   {:id (.id java-object)
    :boleto-id (.boletoId java-object)
    :status (.status java-object)
    :result (.result java-object)
    :tags (into [] (.tags java-object))
    :created (.created java-object)
    :updated (.updated java-object)}))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        after "after"
        before "before"
        tags "tags"
        ids "ids"
        status "status"
        boleto-id "boleto-id"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
          "status" status
          "boletoId" boleto-id}))))

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        after "after"
        before "before"
        tags "tags"
        ids "ids"
        status "status"
        boleto-id "boleto-id"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "after" after
          "before" before
          "tags" (if (nil? tags) nil (into-array String tags))
          "ids" (if (nil? ids) nil (into-array String ids))
          "status" status
          "boletoId" boleto-id}))))

(defn create
  "Send a list of BoletoHolmes maps for creation in the Stark Bank API

  ## Parameters (required):
    - `holmes` [list of BoletoHolmes maps]: list of BoletoHolmes maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of BoletoHolmes maps with updated attributes"
  ([holmes]
   (def java-holmes (map clojure-to-java holmes))
   (def created-java-holmes (BoletoHolmes/create java-holmes))
   (map java-to-clojure created-java-holmes))

  ([holmes, user]
   (def java-holmes (map clojure-to-java holmes))
   (def created-java-holmes (BoletoHolmes/create java-holmes (#'starkbank.user/get-java-user user)))
   (map java-to-clojure created-java-holmes)))

(defn query
  "Receive a stream of BoletoHolmes maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:boleto-id` [string, default nil]: filter for holmes that investigate a specific boleto by its ID. ex: \"5656565656565656\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of BoletoHolmes maps with updated attributes"
  ([]
   (map java-to-clojure (BoletoHolmes/query)))

  ([params]
   (def java-params (clojure-query-to-java params))
   (map java-to-clojure (BoletoHolmes/query java-params)))

  ([params, user]
   (def java-params (clojure-query-to-java params))
   (map java-to-clojure (BoletoHolmes/query java-params (#'starkbank.user/get-java-user user)))))

(defn page
  "Receive a list of up to 100 BoletoHolmes maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"success\"
    - `:boleto-id` [string, default nil]: filter for holmes that investigate a specific boleto by its ID. ex: \"5656565656565656\"
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

    ## Return:
    - map with :holmes and :cursor:
    - `:holmes`: list of holmes maps with updated attributes
    - `:cursor`: cursor string to retrieve the next page of holmes"
  ([]
    (def holmes-page (BoletoHolmes/page))
    (def cursor (.cursor holmes-page))
    (def holmes (map java-to-clojure (.holmes holmes-page)))
    {:holmes holmes, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def holmes-page (BoletoHolmes/page java-params))
    {:holmes (map java-to-clojure (.holmes holmes-page)), :cursor (.cursor holmes-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def holmes-page (BoletoHolmes/page java-params (#'starkbank.user/get-java-user user)))
    {:holmes (map java-to-clojure (.holmes holmes-page)), :cursor (.cursor holmes-page)}))

(defn get
  "Receive a single BoletoHolmes map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BoletoHolmes map with updated attributes"
  ([id]
   (java-to-clojure
    (BoletoHolmes/get id)))

  ([id, user]
   (java-to-clojure
    (BoletoHolmes/get
     id
     (#'starkbank.user/get-java-user user)))))

(ns starkbank.boleto-holmes.log
  "Every time a BoletoHolmes entity is modified, a corresponding BoletoHolmes.Log
  is generated for the entity. This log is never generated by the
  user, but it can be retrieved to check additional information
  on the BoletoHolmes.

  ## Attributes:
    - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
    - `:holmes` [BoletoHolmes]: BoletoHolmes entity to which the log refers to.
    - `:errors` [list of strings]: list of errors linked to this BoletoHolmes event.
    - `:type` [string]: type of the BoletoHolmes event which triggered the log creation. ex: \"processing\" or \"success\"
    - `:created` [string]: creation datetime for the log. ex: \"2020-03-26T19:32:35.418698+00:00\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank BoletoHolmes$Log])
  (:require [starkbank.boleto-holmes :as holmes])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- java-to-clojure
  ([java-object]
   {:id (.id java-object)
    :created (.created java-object)
    :type (.type java-object)
    :holmes (#'holmes/java-to-clojure (.holmes java-object))}))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
      limit "limit"
      after "after"
      before "before"
      types "types"
      holmes-ids "holmes-ids"
    } (stringify-keys clojure-map)]
    (java.util.HashMap.
      {
        "limit" (if (nil? limit) nil (Integer. limit))
        "after" after
        "before" before
        "types" (if (nil? types) nil (into-array String types))
        "holmesIds" (if (nil? holmes-ids) nil (into-array String holmes-ids))}))))

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
      cursor "cursor"
      limit "limit"
      after "after"
      before "before"
      types "types"
      holmes-ids "holmes-ids"
    } (stringify-keys clojure-map)]
    (java.util.HashMap.
      {
        "cursor" cursor
        "limit" (if (nil? limit) nil (Integer. limit))
        "after" after
        "before" before
        "types" (if (nil? types) nil (into-array String types))
        "holmesIds" (if (nil? holmes-ids) nil (into-array String holmes-ids))}))))
      
(defn get
  "Receive a single Log map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Log map with updated attributes"
  ([id]
   (java-to-clojure
    (BoletoHolmes$Log/get id)))

  ([id, user]
   (java-to-clojure
    (BoletoHolmes$Log/get
     id
     (#'starkbank.user/get-java-user user)))))

(defn query
  "Receive a stream of Log maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter retrieved entities by event types. ex: \"processing\" or \"success\"
    - `:holmes-ids` [list of strings, default nil]: list of BoletoHolmes ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Log maps with updated attributes"
  ([]
   (map java-to-clojure (BoletoHolmes$Log/query)))

  ([params]
   (def java-params (clojure-query-to-java params))
   (map java-to-clojure (BoletoHolmes$Log/query java-params)))

  ([params, user]
   (def java-params (clojure-query-to-java params))
   (map java-to-clojure (BoletoHolmes$Log/query java-params (#'starkbank.user/get-java-user user)))))

(defn page
  "Receive a list of up to 100 BoletoHolmes.Log maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of entities to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for entities created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for entities created only before specified date. ex: \"2020-3-10\"
    - `:types` [list of strings, default nil]: filter retrieved entities by event types. ex: \"processing\" or \"success\"
    - `:holmes-ids` [list of strings, default nil]: list of BoletoHolmes ids to filter retrieved entities. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :logs and :cursor:
      - `:logs`: list of log maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of logs"
  ([]
    (def log-page (BoletoHolmes$Log/page))
    (def cursor (.cursor log-page))
    (def logs (map java-to-clojure (.logs log-page)))
    {:logs logs, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def log-page (BoletoHolmes$Log/page java-params))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def log-page (BoletoHolmes$Log/page java-params (#'starkbank.user/get-java-user user)))
    {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))
