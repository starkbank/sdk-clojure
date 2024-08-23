(ns starkbank.utils.signature
  (:require [starkbank.utils.parse :refer [json-to-map transform-keys-to-kebab]]
            [starkbank.utils.rest :refer [get-public-key]])
  (:import [com.starkbank.ellipticcurve Ecdsa PublicKey Signature]
           [com.starkbank.ellipticcurve.utils ByteString]))


(defn verify-signature
  [content signature user ]
  (let [
        public-key (PublicKey/fromPem (get-public-key user))
        stark-signature (Signature/fromBase64 (ByteString. (.getBytes signature)))]
    (if (Ecdsa/verify content stark-signature public-key)
     (:event (transform-keys-to-kebab (json-to-map content)))
     (throw (IllegalArgumentException. "Provided signature and content do not match Stark Bank public key")))
    ) 
  )