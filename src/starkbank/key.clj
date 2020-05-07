(ns starkbank.key
  "Used to generate API-compatible key pairs."
  (:import [com.starkbank Key]))

(defn create
  "Generates a secp256k1 ECDSA private/public key pair to be used in the API authentications

  ## Parameters (optional):
    - `path` [string, default nil]: path to save the keys .pem files. No files will be saved if this parameter isn't provided."
  ([] 
    (def key-pair (Key/create))
    {:public-pem (.-publicPem key-pair), :private-pem (.-privatePem key-pair)})
  ([path] 
    (def key-pair (Key/create path))
    {:public-pem (.-publicPem key-pair), :private-pem (.-privatePem key-pair)}))
