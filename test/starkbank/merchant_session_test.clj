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

(deftest create-and-get-merchant-session
  (testing "create and get a merchant session"
    (def session (create-example-session))
    (is (string? (:uuid session)))
    (is (= (:id session) (:id (merchant-session/get (:id session)))))))

(deftest purchase-merchant-session-deprecated
  (testing "purchase throws without making a request"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"^Function deprecated since v2\.6\.0$"
                          (merchant-session/purchase
                            "0bb894a2697d41d99fe02cad2c00c9bc"
                            {:amount 5000
                             :funding-type "credit"})))))

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
