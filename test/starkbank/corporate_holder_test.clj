(ns starkbank.corporate-holder-test
  (:use [clojure.test])
  (:require [starkbank.corporate-holder :as holder]
            [starkbank.corporate-holder.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(defn- example-holder []
  {:name (str "Iron Bank S.A." (java.util.UUID/randomUUID))
   :tags ["testing" "clojure"]
   :permissions [{:owner-id (System/getenv "SANDBOX_ID") :owner-type "project"}]
   :rules [{:name "Example Rule"
            :interval "day"
            :amount 100000
            :currency-code "USD"}]})

(deftest create-update-cancel-holders
  (testing "create, update and cancel holders"
    (def holders (holder/create [(example-holder)] {:expand ["rules"]}))
    (def created-holder (first holders))
    (is (not (nil? (:id created-holder))))
    (def updated-holder (holder/update (:id created-holder) {:name "Updated Name"}))
    (is (= "Updated Name" (:name updated-holder)))
    (def canceled-holder (holder/cancel (:id created-holder)))
    (is (= "canceled" (:status canceled-holder)))))

(deftest query-and-get-holders
  (testing "query and get holders"
    (def holders (take 200 (holder/query {:limit 1})))
    (def queried-holder (holder/get (:id (first holders))))
    (is (= (:id (first holders)) (:id queried-holder)))))

(deftest page-holders
  (testing "page holders"
    (def get-page (fn [params] (holder/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-holder-logs
  (testing "query and get holder logs"
    (def holder-logs (take 1 (log/query {:limit 1})))
    (when (not (empty? holder-logs))
      (def holder-log (log/get (:id (first holder-logs))))
      (is (not (nil? (:id holder-log)))))))

(deftest page-holder-logs
  (testing "page holder logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
