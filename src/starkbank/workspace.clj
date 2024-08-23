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
  (:require [starkbank.utils.rest :refer [get-id get-page get-stream patch-id
                                          post-multi]]
            [starkbank.settings :refer [credentials]]))

(defn- resource []
  )

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
    (-> (post-multi @credentials (resource) workspace-params {})))

  ([workspace-params, user]
    (-> (post-multi user (resource) workspace-params {}))))

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
    (-> (get-stream @credentials (resource) {})))

  ([params]
    (-> (get-stream @credentials (resource) params)))

  ([params, user] 
    (-> (get-stream user (resource) params))))

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
   (-> (get-page @credentials (resource) {})))
  
  ([params]
   (-> (get-page @credentials (resource) params)))
  
  ([params, user]
   (-> (get-page user (resource) params))))

(defn get
  "Receive a single Workspace map previously created in the Stark Bank API by passing its id

  ## Parameters (required):
    - `:id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:user` [Project or Organization, default nil]: Project or Organization map returned from starkbank.user/project or starkbank.user/organization. Only necessary if starkbank.settings/user has not been set.

  ## Return:
    - Workspace map with updated attributes"
  ([id]
    (-> (get-id @credentials (resource) id {})))

  ([id, user]
    (-> (get-id user (resource) id {}))))

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
    (-> (patch-id @credentials (resource) id params)))

  ([id, params, user]
    (-> (patch-id user (resource) id params))))
