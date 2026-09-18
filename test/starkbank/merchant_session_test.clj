(ns starkbank.merchant-session-test
  (:use [clojure.test])
  (:require [starkbank.merchant-session :as merchant-session]
            [starkbank.merchant-session.log :as log]
            [starkbank.utils.page :as page]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(defn- create-example-session []
  (merchant-session/create
    {:allowed-funding-types ["credit" "debit"]
     :allowed-installments [{:total-amount 5000 :count 1}]
     :expiration 3600
     :challenge-mode "disabled"
     :tags ["testing" "clojure"]}))

(deftest create-get-and-purchase-merchant-session
  (testing "create, get and purchase a merchant session"
    (def session (create-example-session))
    (is (string? (:uuid session)))
    (is (= (:id session) (:id (merchant-session/get (:id session)))))
    (def purchase (merchant-session/purchase
                    (:uuid session)
                    {:amount 5000
                     :installment-count 1
                     :card-expiration "2035-01"
                     :card-number "5448280000000007"
                     :card-security-code "123"
                     :holder-name "Holder Name"
                     :funding-type "credit"}))
    ;; post-sub-resource pode devolver o envelope; aceita as duas formas
    (is (string? (or (:id purchase) (get-in purchase [:purchase :id]))))))

(deftest query-and-page-merchant-sessions
  (testing "query and page merchant sessions"
    (def sessions (take 10 (merchant-session/query {:limit 3})))
    (doseq [session sessions]
      (is (string? (:id session))))
    (def get-page (fn [params] (merchant-session/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))

(deftest query-and-get-merchant-session-logs
  (testing "query and get merchant session logs"
    (def session-logs (take 10 (log/query {:limit 3})))
    (doseq [session-log session-logs]
      (is (string? (:id session-log)))
      (is (map? (:session session-log)))
      (def fetched-log (log/get (:id session-log)))
      (is (= (:id session-log) (:id fetched-log))))))

(deftest page-merchant-session-logs
  (testing "page merchant session logs"
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (<= (count ids) 4))))
