(ns starkbank.transaction-test
  (:use [clojure.test])
  (:require [starkbank.transaction :as transaction] 
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-get-transactions
  (testing "create, get, pdf and delete transactions"
    (def transactions (transaction/create
      [{
        :amount 1
        :description "testing clojure transaction"
        :external-id (.toString (java.util.UUID/randomUUID))
        :receiver-id "5768064935133184"
        :tags ["testing" "clojure"]
      }]))
    (def transaction (transaction/get (:id (first transactions))))
    (is (not (nil? (:id transaction))))))

(deftest query-transactions
  (testing "query transactions"
    (def transactions (take 200 (transaction/query {:limit 3})))
    (is (= 3 (count transactions)))))

(deftest page-transactions
  (testing "page transactions"
    (def get-page (fn [params] (transaction/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
