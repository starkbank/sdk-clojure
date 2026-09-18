(ns starkbank.merchant-installment-test
  (:use [clojure.test])
  (:require [starkbank.merchant-installment :as merchant-installment]
            [starkbank.merchant-installment.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-and-get-merchant-installments
  (testing "query and get merchant installments"
    (def installments (take 10 (merchant-installment/query {:limit 3})))
    (doseq [installment installments]
      (is (string? (:id installment)))
      (is (= (:id installment) (:id (merchant-installment/get (:id installment))))))))

(deftest page-merchant-installments
  (testing "page merchant installments"
    (def get-page (fn [params] (merchant-installment/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))

(deftest query-and-get-merchant-installment-logs
  (testing "query and get merchant installment logs"
    (def installment-logs (take 10 (log/query {:limit 3})))
    (doseq [installment-log installment-logs]
      (is (string? (:id installment-log)))
      (is (map? (:installment installment-log)))
      (def fetched-log (log/get (:id installment-log)))
      (is (= (:id installment-log) (:id fetched-log))))))

(deftest page-merchant-installment-logs
  (testing "page merchant installment logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
