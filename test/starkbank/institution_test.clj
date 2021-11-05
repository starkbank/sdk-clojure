(ns starkbank.institution-test
  (:use [clojure.test])
  (:require [starkbank.institution :as institution]
            [starkbank.user-test :as user]))

(deftest query-and-get-institutions
(testing "query institutions"
  (user/set-test-project)
  (is (= 2 (count (institution/query {:search "stark"}))))
  (is (= 1 (count (institution/query {:spi-codes (into-array String ["20018183"])}))))
  (is (= 1 (count (institution/query {:str-codes (into-array String ["341"])}))))))
