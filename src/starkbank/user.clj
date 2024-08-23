(ns starkbank.user
  "Used to define API user."
  (:refer-clojure :exclude [set])
  (:require [core-clojure.user.organization :as core-organization]
            [core-clojure.user.project :as core-project]))


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
    - `:id` [string]: unique id required to identify project. ex: \"5656565656565656\"
    - `private-key` [string]: PEM string of the private key linked to the project. ex: \"-----BEGIN PUBLIC KEY-----\nMFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEyTIHK6jYuik6ktM9FIF3yCEYzpLjO5X/\ntqDioGM+R2RyW0QEo+1DG8BrUf4UXHSvCjtQ0yLppygz23z0yPZYfw==\n-----END PUBLIC KEY-----\""
  [environment id private-key]
  (core-project/project environment id private-key))

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
  the user/organization-replace(organization, workspace-id) method, which creates a copy of the organization
  map with the altered workspace ID. If you are listing or creating new Workspaces, the
  workspace-id should be nil.

  ## Parameters (required):
    - `environment` [string]: environment where the project is being used. ex: \"sandbox\" or \"production\"
    - `:id` [string]: unique id required to identify project. ex: \"5656565656565656\"
    - `private-key` [string]: PEM string of the private key linked to the project. ex: \"-----BEGIN PUBLIC KEY-----\nMFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEyTIHK6jYuik6ktM9FIF3yCEYzpLjO5X/\ntqDioGM+R2RyW0QEo+1DG8BrUf4UXHSvCjtQ0yLppygz23z0yPZYfw==\n-----END PUBLIC KEY-----\"

  ## Parameters (optional):
    - `:workspace-id` [string]: unique id of the accessed Workspace, if any. ex: nil or \"4848484848484848\""
  ([environment id private-key]
   (core-organization/organization environment id private-key))
  ([environment id private-key workspace-id]
    (core-organization/organization environment id private-key workspace-id)))

(defn organization-replace
  ([organization workspace-id]
   (core-organization/orgaization-replace organization workspace-id)))
