(ns starkbank.request
  (:refer-clojure :exclude [get set update])
  (:require [starkbank.settings :refer [credentials]]
            [starkbank.utils.rest :refer [delete-raw get-raw patch-raw
                                          post-raw put-raw]]))

(defn resource []
  "Joker")

(defn get
  "Receive a json of resources previously created in StarkBank's API
     ## Parameters (required):
       - :path [string]: StarkBank resource's route. ex: \"/invoice/\"
    
     ## Parameters (optional):
       - :user [Organization/Project object, default null]: Organization or Project object. Not necessary if starkbank.user was set before function call
       - :query [map, default None]: Query parameters. ex: {:limit 1, :status \"paid\"} 
    
     ## Return:
      a map of StarkBank objects with updated attributes"
  ([path query] 
   (-> (get-raw @credentials path query (resource) false)))
  
  ([user path query]
   (-> (get-raw user path query (resource) false)))
  )

(defn post
  "Create any Starkbank entity sending a json of resources to StarkBank's API
  
   ## Parameters (required):
     - :path [string]: StarkBank resource's route. ex: \"/invoice/\"
     - :body [map]: request parameters. ex: {:invoices [{:amount 100, :name \"Iron Bank S.A.\", :taxId \"20.018.183/0001-80\"}]}
     - :query [map, default None]: Query parameters. ex: {:limit 1, :status \"paid\"} 

   ## Parameters (optional):
     - :user [Organization/Project object, default null]: Organization or Project object. Not necessary if starkbank.user was set before function call
  
   ## Return:
    a map of StarkBank objects with updated attributes"
  ([path payload query]
   (-> (post-raw @credentials path payload query (resource) false)))

  ([user path payload query]
   (-> (post-raw user path payload query (resource) false))))


(defn patch
  "Update a resources previously created in StarkBank's API
  
   ## Parameters (required):
     - :path [string]: StarkBank resource's route. ex: \"/invoice/\"
     - :body [map request parameters. ex: {:invoices [{:amount 100, :name \"Iron Bank S.A.\", :taxId \"20.018.183/0001-80\"}]}
  
   ## Parameters (optional):
     - :user [Organization/Project object, default null]: Organization or Project object. Not necessary if starkbank.user was set before function call
  
   Return:
    a list of StarkBank objects with updated attributes"
  ([path payload query]
   (-> (patch-raw @credentials path payload query (resource) false)))

  ([user path payload query]
   (-> (patch-raw user path payload query (resource) false))))

(defn put
  "Update a resources previously created in StarkBank's API
  
   ## Parameters (required):
     - :path [string]: StarkBank resource's route. ex: \"/invoice/\"
     - :body [map request parameters. ex: {:invoices [{:amount 100, :name \"Iron Bank S.A.\", :taxId \"20.018.183/0001-80\"}]}
     - :query [map, default None]: Query parameters. ex: {:limit 1, :status \"paid\"} 

   ## Parameters (optional):
     - :user [Organization/Project object, default null]: Organization or Project object. Not necessary if starkbank.user was set before function call
  
   Return:
    a list of StarkBank objects with updated attributes"
  ([path payload query]
   (-> (put-raw @credentials path payload query (resource) false)))

  ([user path payload query]
   (-> (put-raw user path payload query (resource) false))))

(defn delete
  "Delete a resource previously created in StarkBank's API
  
   ## Parameters (required):
     - path [string]: StarkBank resource's route. ex: \"/invoice/9999999999999999\"
   
   ## Parameters (optional):
     - user [Organization/Project object, default null]: Organization or Project object. Not necessary if starkbank.user was set before function call
  
   Return:
    a list of StarkBank objects with updated attributes"
  ([path]
   (-> (delete-raw @credentials path (resource) false)))

  ([user path]
   (-> (delete-raw user path (resource) false))))
