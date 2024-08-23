(ns starkbank.utils.rest
  (:require
   [core-clojure.utils.rest :as rest]
   [starkbank.settings :refer [settings]]))


(defn get-page [user path query]
  (rest/get-page (.host settings) (.sdk-version settings) user path query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn get-stream [user path query]
  (rest/get-stream (.host settings) (.sdk-version settings) user path query (.api-version settings) (.language settings) (.timeout settings)))

(defn get-id [user path id query]
  (rest/get-id (.host settings) (.sdk-version settings) user path id query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn get-content [user path id sub-resource query]
  (rest/get-content (.host settings) (.sdk-version settings) user path id sub-resource query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn get-sub-resource [user path id sub-resource query]
  (rest/get-sub-resource (.host settings) (.sdk-version settings) user path id sub-resource query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn get-public-key [user] 
  (rest/get-public-key (.host settings) (.sdk-version settings) user (.api-version settings) (.language settings) (.timeout settings))
)

(defn post [user resource-name payload query]
  (rest/post (.host settings) (.sdk-version settings) user resource-name payload query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn post-multi [user resource-name payload query]
  (rest/post-multi (.host settings) (.sdk-version settings) user resource-name payload query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn post-single [user resource-name payload query]
  (rest/post-single (.host settings) (.sdk-version settings) user resource-name payload query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn post-sub-resource [user resource-name id sub-resource payload query]
  (rest/post-sub-resource (.host settings) (.sdk-version settings) user resource-name id sub-resource payload query (.api-version settings) (.language settings) (.timeout settings))
  )

(defn delete-id [user path id]
    (rest/delete-id (.host settings) (.sdk-version settings) user path id (.api-version settings) (.language settings) (.timeout settings))
  )

(defn patch-id [user path payload id]
  (rest/patch-id (.host settings) (.sdk-version settings) user path payload id (.api-version settings) (.language settings) (.timeout settings))
  )

(defn get-raw [user path query prefix throw-error]
  (rest/get-raw (.host settings) (.sdk-version settings) user path query (.api-version settings) (.language settings) (.timeout settings) prefix throw-error)
  )

(defn post-raw [user resource-name payload query prefix throw-error]
    (rest/post-raw (.host settings) (.sdk-version settings) user resource-name payload query (.api-version settings) (.language settings) (.timeout settings) prefix throw-error)
  )

(defn patch-raw [user path payload query prefix throw-error]
  (rest/patch-raw (.host settings) (.sdk-version settings) user path payload query (.api-version settings) (.language settings) (.timeout settings) prefix throw-error)
  )

(defn put-raw [user path payload query prefix throw-error]
  (rest/patch-raw (.host settings) (.sdk-version settings) user path payload query (.api-version settings) (.language settings) (.timeout settings) prefix throw-error)
  )

(defn delete-raw [user path prefix throw-error]
  (rest/delete-raw (.host settings) (.sdk-version settings) user path  (.api-version settings) (.language settings) (.timeout settings) prefix throw-error)
  ) 
