(ns starkbank.balance-test
  (:use [clojure.test])
  (:require [starkbank.balance]
            [starkbank.utils.user :refer [set-project]]))

(deftest get-balance
  (testing "get balance"
    (set-project)
    (def balance (starkbank.balance/get))
    (is (number? balance))))
