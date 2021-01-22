(ns starkbank.balance-test
  (:use [clojure.test])
  (:require [starkbank.balance]
            [starkbank.user-test :as user]))

(deftest get-balance
  (testing "get balance"
    (user/set-test-project)
    (def balance (starkbank.balance/get))
    (is (map? balance))
    (is (not (nil? (:id balance))))
    (is (not (nil? (:amount balance))))
    (is (not (nil? (:currency balance))))
    (is (not (nil? (:updated balance))))))
