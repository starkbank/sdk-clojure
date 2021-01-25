(ns starkbank.workspace
  "Workspaces are bank accounts. They have independent balances, statements, operations and permissions.
  The only property that is shared between your workspaces is that they are linked to your organization,
  which carries your basic information, such as tax ID, name, etc..

  ## Parameters (required):
    - `:username` [string]: Simplified name to define the workspace URL. This name must be unique across all Stark Bank Workspaces. Ex: \"starkbankworkspace\"
    - `:name` [string]: Full name that identifies the Workspace. This name will appear when people access the Workspace on our platform, for example. Ex: \"Stark Bank Workspace\"
  
  ## Attributes:
    - `:id` [string, default nil]: unique id returned when the workspace is created. ex: \"5656565656565656\""
  (:refer-clojure :exclude [get set])
  (:import [com.starkbank Workspace])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      username "username"
      name "name"
    }
    (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "username" username
          "name" name
        }
      ))))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :username (.username java-object)
      :name (.name java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
        limit "limit"
        username "username"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
          "username" username
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))

(defn create
  "Send a single Workspace for creation in the Stark Bank API

  ## Parameters (required):
    - `:username` [string]: Simplified name to define the workspace URL. This name must be unique across all Stark Bank Workspaces. Ex: \"starkbankworkspace\"
    - `:name` [string]: Full name that identifies the Workspace. This name will appear when people access the Workspace on our platform, for example. Ex: \"Stark Bank Workspace\"

  ## Parameters (optional):
    - `:user` [Organization]: Organization map returned from starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Workspace map with updated attributes"
  ([workspace-params]
    (def java-workspace-params (clojure-to-java workspace-params))
    (def created-java-workspace (Workspace/create java-workspace-params))
    (java-to-clojure created-java-workspace))

  ([workspace-params, user]
    (def java-workspace-params (clojure-to-java workspace-params))
    (def created-java-workspace (Workspace/create java-workspace-params (#'starkbank.user/get-java-user user)))
    (java-to-clojure created-java-workspace)))

(defn query
  "Receive a stream of Workspace maps previously created in the Stark Bank API
  If no filters are passed and the user is an Organization, all of the Organization Workspaces
  will be retrieved.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:username` [string, default nil]: query by the simplified name that defines the workspace URL. This name is always unique across all Stark Bank Workspaces. Ex: \"starkbankworkspace\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - stream of Workspace maps with updated attributes"
  ([]
    (map java-to-clojure (Workspace/query)))

  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Workspace/query java-params)))

  ([params, user] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Workspace/query java-params (#'starkbank.user/get-java-user user)))))

(defn get
  "Receive a single Workspace map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Workspace map with updated attributes"
  ([id]
    (java-to-clojure
      (Workspace/get id)))

  ([id, user]
    (java-to-clojure
      (Workspace/get
        id
        (#'starkbank.user/get-java-user user)))))
