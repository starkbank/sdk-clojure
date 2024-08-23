(ns starkbank.deposit-test
    (:use [clojure.test])
    (:require [starkbank.deposit :as deposit]
              [starkbank.deposit.log :as log] 
              [starkbank.utils.page :as page]
              [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-and-get-deposits
  (testing "query and get deposits"
    (def deposits (take 200 (deposit/query {:limit 3})))
    (is (= 3 (count deposits))))
    (def deposit (first deposits))
    (def deposit-id (:id deposit))
    (def deposit (deposit/get deposit-id))
    (is (= deposit-id (:id deposit))))

(deftest page-deposit
  (testing "page deposit"
    (def get-page (fn [params] (deposit/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-deposit-logs
  (testing "query and get deposit logs"
    (def deposit-logs (log/query {:limit 1}))
    (is (= 1 (count deposit-logs)))
    (def deposit-log (log/get (:id (first deposit-logs))))
    (is (not (nil? (:id deposit-log))))
    (is (not (nil? (:errors deposit-log))))
    (is (string? (:created deposit-log)))
    (is (map? (:deposit deposit-log)))))

(deftest page-deposit-logs
  (testing "page deposit-logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
