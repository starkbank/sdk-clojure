(ns starkbank.merchant-card-test
  (:use [clojure.test])
  (:require [starkbank.merchant-card :as merchant-card]
            [starkbank.merchant-card.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-and-get-merchant-cards
  (testing "query and get merchant cards"
    (def cards (take 10 (merchant-card/query {:limit 3})))
    (doseq [card cards]
      (is (string? (:id card)))
      (is (= (:id card) (:id (merchant-card/get (:id card))))))))

(deftest page-merchant-cards
  (testing "page merchant cards"
    (def get-page (fn [params] (merchant-card/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))

(deftest query-and-get-merchant-card-logs
  (testing "query and get merchant card logs"
    (def card-logs (take 10 (log/query {:limit 3})))
    (doseq [card-log card-logs]
      (is (string? (:id card-log)))
      (is (map? (:card card-log)))
      (def fetched-log (log/get (:id card-log)))
      (is (= (:id card-log) (:id fetched-log))))))

(deftest page-merchant-card-logs
  (testing "page merchant card logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
