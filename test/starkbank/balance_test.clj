(ns starkbank.balance-test
  (:use [clojure.test])
  (:require [starkbank.balance :as balance]
            [starkbank.user-test :as user]))

(deftest get-balance
  (testing "get balance"
    (user/set-default-user-test)
    (def b (balance/get))
    (is (map? b))
    (is (not (nil? (:id b))))
    (is (not (nil? (:amount b))))
    (is (not (nil? (:currency b))))
    (is (not (nil? (:updated b))))))
