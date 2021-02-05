(ns starkbank.transaction-test
  (:use [clojure.test])
  (:require [starkbank.transaction :as transaction]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]))

(deftest create-get-transactions
  (testing "create, get, pdf and delete transactions"
    (user/set-test-project)
    (def transactions (transaction/create
      [{
        :amount 1
        :description "testing clojure transaction"
        :external-id (.toString (java.util.UUID/randomUUID))
        :receiver-id "5768064935133184"
        :tags ["testing" "clojure"]
      }]))
    (def transaction (transaction/get (:id (first transactions))))
    (is (not (nil? (:id transaction))))))

(deftest query-transactions
  (testing "query transactions"
    (user/set-test-project)
    (def transactions (take 200 (transaction/query {:limit 3})))
    (is (= 3 (count transactions)))))

(deftest page-transactions
  (testing "page transactions"
    (user/set-test-project)
    (def first-page (transaction/page {:limit 2}))
    (println first-page)
    (def second-page (transaction/page {:limit 2 :cursor (:cursor first-page)}))
    (println (:transactions second-page))
    (println (:cursor second-page))
    (is (not (= (:id (first (:transactions first-page))) (:id (first (:transactions second-page))))))))
