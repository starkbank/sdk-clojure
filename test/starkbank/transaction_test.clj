(ns starkbank.transaction-test
  (:use [clojure.test])
  (:require [starkbank.transaction :as transaction]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-pdf-delete-transactions
  (testing "create, get, pdf and delete transactions"
    (user/set-default-user-test)
    (def transactions (transaction/create
      [{
        :amount 1
        :description "testing clojure transaction"
        :external-id (.toString (java.util.UUID/randomUUID))
        :receiver-id "5768064935133184"
        :tags ["testing" "clojure"]
      }]))
    (println (transaction/get (:id (first transactions))))))

(deftest query-transactions
  (testing "query transactions"
    (user/set-default-user-test)
    (def transactions (take 200 (transaction/query {:limit 3})))))
