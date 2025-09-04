(ns starkbank.transaction-test
  (:use [clojure.test])
  (:require [starkbank.transaction :as transaction] 
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-transactions
  (testing "create transactions"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Function deprecated since v2.6.0"
          (transaction/create [{}])))))

(deftest query-get-transactions
  (testing "query and get transactions"
    (def transactions (take 200 (transaction/query {:limit 3})))
    (is (= 3 (count transactions)))
    (def transaction (transaction/get (:id (first transactions))))
    (is (not (nil? (:id transaction))))))

(deftest page-transactions
  (testing "page transactions"
    (def get-page (fn [params] (transaction/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
