(ns starkbank.split-profile
  "When you create a Split, the SplitProfile entity is automatically created for
  the workspace. If you haven't created a Split yet, you can use the 'put'
  function to create your SplitProfile. There is only one SplitProfile per
  workspace: calling 'put' again updates the existing rules instead of
  creating a new one.

  ## Parameters (optional):
    - `:interval` [string, default \"week\"]: frequency of transfer. Options: \"day\", \"week\", \"month\"
    - `:delay` [integer, default nil]: how long the amount will stay at the workspace before being transferred out, in milliseconds. ex: 604800. A value of 0 is legitimate and means no delay.
    - `:tags` [list of strings, default []]: list of strings for tagging

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when SplitProfile is created. ex: \"5656565656565656\"
    - `:status` [string]: current SplitProfile status. ex: \"created\"
    - `:created` [string]: creation datetime for the SplitProfile. ex: \"2020-03-10 10:30:00.000000+00:00\"
    - `:updated` [string]: update datetime for the SplitProfile. ex: \"2020-03-10 10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream put-raw]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  "split-profile")

(defn- put-multi [user profiles]
  (-> (put-raw user (resource) {:profiles profiles} {} "" true)
      :content
      :profiles))

(defn put
  "Send a list containing a single SplitProfile map to the Stark Bank API. If a
  SplitProfile already exists for the workspace, this updates its rules instead
  of creating a new one.

  ## Parameters (required):
    - `profiles` [list of SplitProfile maps]: list containing the SplitProfile map to be created or updated in the API

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - list of SplitProfile maps with updated attributes"
  ([profiles]
   (-> (put-multi @credentials profiles)))

  ([profiles, user]
   (-> (put-multi user profiles))))

(defn get
  "Receive a single SplitProfile map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - SplitProfile map with updated attributes"
  ([id]
   (-> (get-id @credentials (resource) id {})))

  ([id, user]
   (-> (get-id user (resource) id {}))))

(defn query
  "Receive a stream of SplitProfile maps previously created in the Stark Bank API.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of SplitProfile maps with updated attributes. Since there is only one SplitProfile per workspace, expect at most a single map."
  ([]
   (-> (get-stream @credentials (resource) {})))

  ([params]
   (-> (get-stream @credentials (resource) params)))

  ([params, user]
   (-> (get-stream user (resource) params))))

(defn page
  "Receive a list of up to 100 SplitProfile maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-3-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-3-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :profiles and :cursor:
      - `:profiles`: list of SplitProfile maps with updated attributes. Since there is only one SplitProfile per workspace, expect at most a single map.
      - `:cursor`: cursor string to retrieve the next page of profiles"
  ([]
   (-> (get-page @credentials (resource) {})))

  ([params]
   (-> (get-page @credentials (resource) params)))

  ([params, user]
   (-> (get-page user (resource) params))))
