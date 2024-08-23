(ns starkbank.utils.parse
  (:require
   [clojure.string :as str]
   [cheshire.core :as chesire]
   [clojure.walk :as walk]))


(defn camel-to-kebab [s]
  (-> s
      (str/replace #"([a-z])([A-Z])" "$1-$2")
      (str/lower-case)))

(defn transform-keys-to-kebab [m]
  (letfn [(transform-key [k]
            (if (keyword? k)
              (keyword (camel-to-kebab (name k)))
              k))]
    (walk/postwalk (fn [x] (if (map? x)
                             (into {} (map (fn [[k v]] [(transform-key k) v]) x))
                             x)) m)))



(defn json-to-map
  [json-string]
  (chesire/parse-string json-string true))