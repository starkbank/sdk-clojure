(ns starkbank.merchant-category-test
  (:use [clojure.test])
  (:require [starkbank.merchant-category :as merchant-category]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest query-merchant-categories
  (testing "query merchant categories"
    (def categories (take 10 (merchant-category/query {:search "food"})))
    (doseq [category categories]
      (is (map? category)))))
