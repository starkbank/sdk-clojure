(ns starkbank.settings
  "Used to set options in SDK."
  (:refer-clojure :exclude [set]))

(def credentials (atom nil))
(def error-lang (atom "en-us"))


(defn user
  "sets a default user (project or organization) to be automatically used in all requests"
  [user]
  (reset! credentials user))

(defn language
  "sets a default language to be automatically used in all requests.
   Options are en-US and pt-BR"
  [language]
  (reset! error-lang language))

(defrecord sdk-setup [host sdk-version api-version language timeout])
(def settings (->sdk-setup "bank" "2.6.0" "v2" @error-lang 15))
