(ns starkbank.workspace
  "Workspaces are bank accounts. They have independent balances, statements, operations and permissions.
  The only property that is shared between your workspaces is that they are linked to your organization,
  which carries your basic information, such as tax ID, name, etc..

  ## Parameters (required):
    - `:username` [string]: Simplified name to define the workspace URL. This name must be unique across all Stark Bank Workspaces. Ex: \"starkbankworkspace\"
    - `:name` [string]: Full name that identifies the Workspace. This name will appear when people access the Workspace on our platform, for example. Ex: \"Stark Bank Workspace\"

  ## Parameters (optional):
    - `:allowed-tax-ids` [list of strings, default nil]: list of tax IDs that will be allowed to send Deposits to this Workspace. If empty, all are allowed. ex: [\"012.345.678-90\", \"20.018.183/0001-80\"]
  
  ## Attributes:
    - `:id` [string, default nil]: unique id returned when the workspace is created. ex: \"5656565656565656\""
  (:refer-clojure :exclude [get set update])
  (:import [com.starkbank Workspace])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      username "username"
      name "name"
      allowed-tax-ids "allowed-tax-ids"
    }
    (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "username" username
          "name" name
          "allowed-tax-ids" allowed-tax-ids
        }
      ))))

(defn- java-to-clojure
  ([java-object]
    {
      :id (.id java-object)
      :username (.username java-object)
      :name (.name java-object)
      :allowed-tax-ids (into [] (.allowedTaxIds java-object))
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

(defn- clojure-page-to-java
  ([clojure-map]
    (let [{
        cursor "cursor"
        limit "limit"
        username "username"
        ids "ids"
      } (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "cursor" cursor
          "limit" (if (nil? limit) nil (Integer. limit))
          "username" username
          "ids" (if (nil? ids) nil (into-array String ids))
        }
      ))))

(defn- clojure-update-to-java
  ([clojure-map]
   (let [{
     username "username"
     name "name"
     allowed-tax-ids "allowed-tax-ids"
    } (stringify-keys clojure-map)]
     (java.util.HashMap.
      {
        "username" username
        "name" name
        "allowedTaxIds" (if (nil? allowed-tax-ids) nil (into-array String allowed-tax-ids))
      }))))

(defn create
  "Send a single Workspace for creation in the Stark Bank API

  ## Parameters (required):
    - `:username` [string]: Simplified name to define the workspace URL. This name must be unique across all Stark Bank Workspaces. Ex: \"starkbankworkspace\"
    - `:name` [string]: Full name that identifies the Workspace. This name will appear when people access the Workspace on our platform, for example. Ex: \"Stark Bank Workspace\"

  ## Parameters (optional):
    - `:allowed-tax-ids` [list of strings, default []]: list of tax IDs that will be allowed to send Deposits to this Workspace. If empty, all are allowed. ex: [\"012.345.678-90\", \"20.018.183/0001-80\"]
    - `:user` [Organization, default nil]: Organization map returned from starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

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
  "Receive a stream of Workspace maps previously created in the Stark Bank API.
  If no filters are passed and the user is an Organization, all of the Organization Workspaces
  will be retrieved.
  Use this function instead of page if you want to stream the objects without worrying about cursors and pagination.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:username` [string, default nil]: query by the simplified name that defines the workspace URL. This name is always unique across all Stark Bank Workspaces. Ex: \"starkbankworkspace\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

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

(defn page
  "Receive a list of up to 100 Workspace maps previously created in the Stark Bank API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:username` [string, default nil]: query by the simplified name that defines the workspace URL. This name is always unique across all Stark Bank Workspaces. Ex: \"starkbankworkspace\"
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - map with :workspaces and :cursor:
      - `:workspaces`: list of workspace maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of workspaces"
  ([]
    (def workspace-page (Workspace/page))
    (def cursor (.cursor workspace-page))
    (def workspaces (map java-to-clojure (.workspaces workspace-page)))
    {:workspaces workspaces, :cursor cursor})

  ([params]
    (def java-params (clojure-page-to-java params))
    (def workspace-page (Workspace/page java-params))
    {:workspaces (map java-to-clojure (.workspaces workspace-page)), :cursor (.cursor workspace-page)})

  ([params, user] 
    (def java-params (clojure-page-to-java params))
    (def workspace-page (Workspace/page java-params (#'starkbank.user/get-java-user user)))
    {:workspaces (map java-to-clojure (.workspaces workspace-page)), :cursor (.cursor workspace-page)}))

(defn get
  "Receive a single Workspace map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

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

(defn update
  "Update a Workspace by passing id.

  ## Parameters (required):
    - `:id` [list of strings]: Workspace unique ids. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:username` [string]: Simplified name to define the workspace URL. This name must be unique across all Stark Bank Workspaces. Ex: \"starkbank-workspace\"
    - `:name` [string]: Full name that identifies the Workspace. This name will appear when people access the Workspace on our platform, for example. Ex: \"Stark Bank Workspace\"
    - `:allowed-tax-ids` [list of strings, default nil]: list of tax IDs that will be allowed to send Deposits to this Workspace. If empty, all are allowed. ex: [\"012.345.678-90\", \"20.018.183/0001-80\"]
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - target Workspace with updated attributes"
  ([id, params]
    (java-to-clojure
    (Workspace/update id (clojure-update-to-java params))))

  ([id, params, user]
    (java-to-clojure
    (Workspace/update
      id
      (clojure-update-to-java params)
      (#'starkbank.user/get-java-user user)))))
