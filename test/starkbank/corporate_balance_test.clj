(ns starkbank.corporate-balance-test
  (:use [clojure.test])
  (:require [starkbank.corporate-balance :as balance]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest get-balance
  (testing "get corporate balance"
    (def corporate-balance (balance/get))
    (is (integer? (:amount corporate-balance)))))
