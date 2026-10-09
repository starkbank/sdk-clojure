(ns starkbank.merchant-purchase-test
  (:use [clojure.test])
  (:require [starkbank.merchant-purchase :as merchant-purchase]
            [starkbank.merchant-purchase.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(defn- create-approved-card-id []
  (:card-id (first (merchant-purchase/query {:limit 1 :status "confirmed"}))))

(deftest create-get-and-update-merchant-purchases
  (testing "create, get and update merchant purchases"
    (def card-id (create-approved-card-id))
    (when card-id
      (def purchase (merchant-purchase/create
                      {:card-id card-id
                       :amount 5000
                       :funding-type "credit"
                       :installment-count 1
                       :challenge-mode "disabled"
                       :tags ["testing" "clojure"]}))
      (is (string? (:id purchase)))
      (is (= (:id purchase) (:id (merchant-purchase/get (:id purchase)))))
      (when (= "confirmed" (:status purchase))
        (merchant-purchase/update (:id purchase) {:status "reversed" :amount 0})))))

(deftest query-and-page-merchant-purchases
  (testing "query and page merchant purchases"
    (def purchases (take 10 (merchant-purchase/query {:limit 3})))
    (doseq [purchase purchases]
      (is (string? (:id purchase))))
    (def get-page (fn [params] (merchant-purchase/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))

(deftest query-and-get-merchant-purchase-logs
  (testing "query and get merchant purchase logs"
    (def purchase-logs (take 10 (log/query {:limit 3})))
    (doseq [purchase-log purchase-logs]
      (is (string? (:id purchase-log)))
      (is (map? (:purchase purchase-log)))
      (def fetched-log (log/get (:id purchase-log)))
      (is (= (:id purchase-log) (:id fetched-log))))))

(deftest page-merchant-purchase-logs
  (testing "page merchant purchase logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
