(ns starkbank.merchant-country-test
  (:use [clojure.test])
  (:require [starkbank.merchant-country :as merchant-country]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-merchant-countries
  (testing "query merchant countries"
    (def countries (take 10 (merchant-country/query {:search "brazil"})))
    (doseq [country countries]
      (is (not (nil? (:code country)))))))
