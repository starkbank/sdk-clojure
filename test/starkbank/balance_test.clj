(ns starkbank.balance-test
  (:use [clojure.test])
  (:require [starkbank.balance :as balance]
            [starkbank.user-test :as user]))

(deftest get-balance
  (testing "get balance"
    (user/set-default-user-test)
    (balance/gets)))
