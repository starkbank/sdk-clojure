(ns starkbank.brcode-preview-test
  (:use [clojure.test])
  (:require [starkbank.brcode-preview :as preview]
            [starkbank.invoice :as invoice]
            [starkbank.utils.user :refer [set-project]]))
  
(set-project)

(deftest query-brcode-preview
  (testing "query brcode-preview"
    (def invoices (invoice/query {:limit 1 :status "created"}))
    (def brcodes [(:brcode (first invoices))])
    (def preview (first (preview/query {:brcodes brcodes})))
    (is (= "active" (:status preview)))
    ))
