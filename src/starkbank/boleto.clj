(ns starkbank.boleto
  "Used to generate new API-compatible key pairs."
  (:import [com.starkbank Boleto])
  (:use [starkbank.user]
        [clojure.walk]))

(defn- clojure-to-java
  ([clojure-map]
    (let [{
      id "id"
      amount "amount"
      name "name"
      tax-id "tax-id"
      street-line-1 "street-line-1"
      street-line-2 "street-line-2"
      district "district"
      city "city"
      state-code "state-code"
      zip-code "zip-code"
      due "due"
      fine "fine"
      interest "interest"
      overdue-limit "overdue-limit"
      tags "tags"
      descriptions "descriptions"
      discounts "discounts"
      fee "fee"
      line "line"
      bar-code "bar-code"
      status "status"
      created "created"}
      (stringify-keys clojure-map)]

      (defn- apply-java-hashmap [x] (java.util.HashMap. x))
      
      (Boleto. (java.util.HashMap.
        {
          "id" id
          "amount" (if (nil? amount) nil (Integer. amount))
          "name" name
          "taxId" tax-id
          "streetLine1" street-line-1
          "streetLine2" street-line-2
          "district" district
          "city" city
          "stateCode" state-code
          "zipCode" zip-code
          "due" due
          "fine" fine
          "interest" interest
          "overdueLimit" (if (nil? overdue-limit) nil (Integer. overdue-limit))
          "tags" (if (nil? tags) nil (into-array String tags))
          "descriptions" (if (nil? descriptions) nil (java.util.ArrayList. (map apply-java-hashmap descriptions)))
          "discounts" (if (nil? discounts) nil (java.util.ArrayList. (map apply-java-hashmap discounts)))
          "fee" fee
          "line" line
          "barCode" bar-code
          "status" status
          "created" created
        }
      )))))

(defn- java-to-clojure
  ([java-object]
    (defn- java-hashmap-to-map [x] (into {} x))
    {
      :id (.id java-object)
      :amount (.amount java-object)
      :name (.name java-object)
      :tax-id (.taxId java-object)
      :street-line-1 (.streetLine1 java-object)
      :street-line-2 (.streetLine2 java-object)
      :district (.district java-object)
      :city (.city java-object)
      :state-code (.stateCode java-object)
      :zip-code (.zipCode java-object)
      :due (.due java-object)
      :fine (.fine java-object)
      :interest (.interest java-object)
      :overdue-limit (.overdueLimit java-object)
      :tags (into [] (.tags java-object))
      :descriptions (into [] (keywordize-keys (map java-hashmap-to-map (.descriptions java-object))))
      :discounts (into [] (keywordize-keys (map java-hashmap-to-map (.discounts java-object))))
      :fee (.fee java-object)
      :line (.line java-object)
      :bar-code (.barCode java-object)
      :status (.status java-object)
      :created (.created java-object)
    }))

(defn- clojure-query-to-java
  ([clojure-map]
    (let [{
      limit "limit"}
      (stringify-keys clojure-map)]
      (java.util.HashMap.
        {
          "limit" (if (nil? limit) nil (Integer. limit))
        }
      ))))

(defn create
  "creates boletos"
  ([boletos]
    (def java-boletos (map clojure-to-java boletos))
    (def created-java-boletos (Boleto/create java-boletos))
    (map java-to-clojure created-java-boletos))

  ([boletos, project] 
    (def java-boletos (map clojure-to-java boletos))
    (def created-java-boletos (Boleto/create java-boletos (#'starkbank.user/get-java-project project)))
    (map java-to-clojure created-java-boletos)))

(defn query
  "queries boletos"
  ([params]
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Boleto/query java-params)))

  ([params, project] 
    (def java-params (clojure-query-to-java params))
    (map java-to-clojure (Boleto/query java-params (#'starkbank.user/get-java-project project)))))

(defn gets
  "gets boleto"
  ([id]
    (java-to-clojure
      (Boleto/get id)))

  ([id, project]
    (java-to-clojure
      (Boleto/get
        id
        (#'starkbank.user/get-java-project project)))))

(defn delete
  "deletes boleto"
  ([id]
    (java-to-clojure
      (Boleto/delete id)))

  ([id, project]
    (java-to-clojure
      (Boleto/delete
        id
        (#'starkbank.user/get-java-project project)))))

(defn pdf
  "gets boleto PDF"
  ([id]
    (clojure.java.io/input-stream
      (Boleto/pdf id)))

  ([id, project]
    (clojure.java.io/input-stream
      (Boleto/pdf
        id
        (#'starkbank.user/get-java-project project)))))
