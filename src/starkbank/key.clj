(ns starkbank.key
  "Used to generate new API-compatible key pairs."
  (:import [com.starkbank Key]))

(defn create
  "creates keys"
  ([] 
    (def key-pair (Key/create))
    {:public-pem (.-publicPem key-pair), :private-pem (.-privatePem key-pair)})
  ([path] 
    (def key-pair (Key/create path))
    {:public-pem (.-publicPem key-pair), :private-pem (.-privatePem key-pair)}))
