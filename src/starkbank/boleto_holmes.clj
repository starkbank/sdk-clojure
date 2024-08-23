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
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream
                                          post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "boleto-holmes")

(defn create
  "Send a list of BoletoHolmes maps for creation in the Stark Bank API

  ## Parameters (required):
    - `holmes` [list of BoletoHolmes maps]: list of BoletoHolmes maps to be created in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of BoletoHolmes maps with updated attributes"
  ([holmes]
   (-> (post-multi @credentials (resource) holmes {}))
   )

  ([holmes, user]
   (-> (post-multi user (resource) holmes {}))
   )
  )

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
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

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
   (-> (get-page @credentials (resource) {})) 
   )

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user] 
   (-> (get-page user (resource) params)) 
))

(defn get
  "Receive a single BoletoHolmes map previously created by the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - BoletoHolmes map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))
