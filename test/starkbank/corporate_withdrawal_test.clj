(ns starkbank.corporate-withdrawal-test
  (:use [clojure.test])
  (:require [starkbank.corporate-withdrawal :as withdrawal]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-and-get-withdrawals
  (testing "create and get withdrawals"
    (def created-withdrawal (withdrawal/create
                              {:amount 1
                               :external-id (.toString (java.util.UUID/randomUUID))
                               :tags ["testing" "clojure"]}))
    (is (not (nil? (:id created-withdrawal))))
    (def queried-withdrawal (withdrawal/get (:id created-withdrawal)))
    (is (= (:id created-withdrawal) (:id queried-withdrawal)))))

(deftest query-withdrawals
  (testing "query withdrawals"
    (def withdrawals (take 200 (withdrawal/query {:limit 1})))
    (is (<= (count withdrawals) 1))))

(deftest page-withdrawals
  (testing "page withdrawals"
    (def get-page (fn [params] (withdrawal/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
