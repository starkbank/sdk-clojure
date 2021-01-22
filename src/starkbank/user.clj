(ns starkbank.user
  "Used to define API user."
  (:refer-clojure :exclude [set])
  (:import [com.starkbank Project])
  (:import [com.starkbank Organization])
  (:use [clojure.walk]))

(defn project
  "The Project map is an authentication entity for the SDK that is permanently
  linked to a specific Workspace.
  All requests to the Stark Bank API must be authenticated via an SDK user,
  which must have been previously created at the Stark Bank website
  [https://web.sandbox.starkbank.com] or [https://web.starkbank.com]
  before you can use it in this SDK. Projects may be passed as the user parameter on
  each request or may be defined as the default user at the start (See README).

  ## Parameters (required):
    - `environment` [string]: environment where the project is being used. ex: \"sandbox\" or \"production\"
    - `id` [string]: unique id required to identify project. ex: \"5656565656565656\"
    - `private-key` [string]: PEM string of the private key linked to the project. ex: \"-----BEGIN PUBLIC KEY-----\nMFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEyTIHK6jYuik6ktM9FIF3yCEYzpLjO5X/\ntqDioGM+R2RyW0QEo+1DG8BrUf4UXHSvCjtQ0yLppygz23z0yPZYfw==\n-----END PUBLIC KEY-----\""
  ([environment id private-key] 
    {:environment environment, :id id, :private-key private-key :type "project"}))

(defn- get-java-project
  ([project] 
    (let [{
        id :id
        environment :environment
        private-key :private-key
      } project]
      (Project. environment id private-key))))

(defn organization
  "The Organization map is an authentication entity for the SDK that
  represents your entire Organization, being able to access any Workspace
  underneath it and even create new Workspaces. Only a legal representative
  of your organization can register or change the Organization credentials.
  All requests to the Stark Bank API must be authenticated via an SDK user,
  which must have been previously created at the Stark Bank website
  [https://web.sandbox.starkbank.com] or [https://web.starkbank.com]
  before you can use it in this SDK. Organizations may be passed as the user parameter on
  each request or may be defined as the default user at the start (See README).
  If you are accessing a specific Workspace using Organization credentials, you should
  specify the workspace ID when building the Organization map or by request, using
  the Organization.replace(organization, workspace_id) method, which creates a copy of the organization
  map with the altered workspace ID. If you are listing or creating new Workspaces, the
  workspace_id should be nil.

  ## Parameters (required):
    - `environment` [string]: environment where the project is being used. ex: \"sandbox\" or \"production\"
    - `id` [string]: unique id required to identify project. ex: \"5656565656565656\"
    - `private-key` [string]: PEM string of the private key linked to the project. ex: \"-----BEGIN PUBLIC KEY-----\nMFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEyTIHK6jYuik6ktM9FIF3yCEYzpLjO5X/\ntqDioGM+R2RyW0QEo+1DG8BrUf4UXHSvCjtQ0yLppygz23z0yPZYfw==\n-----END PUBLIC KEY-----\"

  ## Parameters (optional):
    - `:workspace_id` [string]: unique id of the accessed Workspace, if any. ex: nil or \"4848484848484848\""
  ([environment id private-key]
   {:environment environment, :id id, :private-key private-key :workspace-id nil :type "organization"})
  ([environment id private-key workspace-id]
    {:environment environment, :id id, :private-key private-key :workspace-id workspace-id :type "organization"}))

(defn- get-java-organization
  ([organization]
   (let [{id :id
          environment :environment
          private-key :private-key
          workspace-id :workspace-id} organization]
     (Organization. environment id private-key workspace-id))))

(defn- get-java-user
  ([params]
    (case (:type params)
      "project" (get-java-project params)
      "organization" (get-java-organization params))))

(defn- try-java-user
  ([params]
   (try
     (get-java-user params)
     (catch Exception e (stringify-keys params))))

  ([params, callback]
   (try
     (get-java-user params)
     (catch Exception e (callback params)))))
