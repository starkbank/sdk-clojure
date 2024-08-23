(ns starkbank.key
  "Used to generate API-compatible key pairs."
  (:require [core-clojure.key :refer [create-key]])
  )

(defn create
  "Generates a secp256k1 ECDSA private/public key pair to be used in the API authentications

  ## Parameters (optional):
    - `path` [string, default nil]: path to save the keys .pem files. No files will be saved if this parameter isn't provided."
  ([] 
    {:public-pem (.-publicPem (create-key)), :private-pem (.-privatePem (create-key))})
  ([path] 
    {:public-pem (.-publicPem (create-key path)), :private-pem (.-privatePem (create-key path))}))
