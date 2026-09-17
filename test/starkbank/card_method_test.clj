(ns starkbank.card-method-test
  (:use [clojure.test])
  (:require [starkbank.card-method :as card-method]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-card-methods
  (testing "query card methods"
    (def card-methods (card-method/query {:search "token"}))
    (doseq [method card-methods]
      (is (not (nil? (:code method)))))))
