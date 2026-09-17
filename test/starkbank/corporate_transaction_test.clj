(ns starkbank.corporate-transaction-test
  (:use [clojure.test])
  (:require [starkbank.corporate-transaction :as transaction]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-and-get-transactions
  (testing "query and get transactions"
    (def transactions (take 200 (transaction/query {:limit 1})))
    (when (not (empty? transactions))
      (def queried-transaction (transaction/get (:id (first transactions))))
      (is (= (:id (first transactions)) (:id queried-transaction))))))

(deftest page-transactions
  (testing "page transactions"
    (def get-page (fn [params] (transaction/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
