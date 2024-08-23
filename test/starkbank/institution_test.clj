(ns starkbank.institution-test
  (:use [clojure.test])
  (:require [starkbank.institution :as institution] 
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-and-get-institutions
(testing "query institutions"
  (is (= 2 (count (institution/query {:search "stark"}))))
  (is (= 1 (count (institution/query {:spi-codes ["20018183"]}))))
  (is (= 1 (count (institution/query {:str-codes ["341"]}))))
  ))
