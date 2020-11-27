(ns starkbank.user
  "Used to define API user."
  (:refer-clojure :exclude [set])
  (:import [com.starkbank Project])
  (:use [clojure.walk]))

(defn project
  "The Project map is the main authentication entity for the SDK.
  All requests to the Stark Bank API must be authenticated via a project,
  which must have been previously created at the Stark Bank website
  [https://sandbox.web.starkbank.com] or [https://web.starkbank.com]
  before you can use it in this SDK. Projects may be passed as a parameter on
  each request or may be defined as the default user at the start (See README).

  ## Parameters (required):
    - `environment` [string]: environment where the project is being used. ex: \"sandbox\" or \"production\"
    - `id` [string]: unique id required to identify project. ex: \"5656565656565656\"
    - `private-key` [string]: PEM string of the private key linked to the project. ex: \"-----BEGIN PUBLIC KEY-----\nMFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEyTIHK6jYuik6ktM9FIF3yCEYzpLjO5X/\ntqDioGM+R2RyW0QEo+1DG8BrUf4UXHSvCjtQ0yLppygz23z0yPZYfw==\n-----END PUBLIC KEY-----\""
  ([environment id private-key] 
    {:environment environment, :id id, :private-key private-key}))

(defn- get-java-project
  ([project] 
    (let [{
        id :id
        environment :environment
        private-key :private-key
      } project]
      (Project. environment id private-key))))

(defn- try-java-project
  ([params]
    (try
      (get-java-project params)
      (catch Exception e (stringify-keys params))))
   
  ([params, callback]
    (try
      (get-java-project params)
      (catch Exception e (callback params)))))
