(ns starkbank.settings
  "Used to set options in SDK."
  (:refer-clojure :exclude [set])
  (:import [com.starkbank Project])
  (:use [starkbank.user]))

(defn set-default-user
  "sets a default user (project) to be automatically used in all requests"
  ([user]
   (def java-project (#'starkbank.user/get-java-project user))
   (set! (. com.starkbank.Settings -user) java-project)))

(defn set-default-language
  "sets a default language to be automatically used in all requests. Options are en-US and pt-BR"
  ([language]
   (set! (. com.starkbank.Settings -language) language)))

(defn- set-user-agent-override
  ([]
   (set! (. com.starkbank.Settings -userAgentOverride) (str "Clojure-" (clojure-version) "-SDK-2.0.0"))))

(set-user-agent-override)
