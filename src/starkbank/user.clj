(ns starkbank.user
  "Used to generate new API-compatible key pairs."
  (:import [com.starkbank Project]))

(defn project
  "defines project info"
  ([environment id private-key] 
    {'environment environment, 'id id, 'private-key private-key}))

(defn- get-java-project
  "sets a default user (project) to be automatically used in all requests"
  ([project] 
    (let [{
        id 'id
        environment 'environment
        private-key 'private-key
      } project]
      (Project. environment id private-key))))

(defn set-default-user
  "sets a default user (project) to be automatically used in all requests"
  ([project] 
    (def java-project (get-java-project project))
    (set! (. com.starkbank.User  -defaultUser) java-project)
    ))
