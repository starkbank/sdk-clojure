(ns starkbank.brcode-preview-test
	(:use [clojure.test])
	(:require [starkbank.brcode-preview :as preview]
						[starkbank.invoice :as invoice]
						[starkbank.user-test :as user]))
  
(deftest query-brcode-preview
	(testing "query brcode-preview"
		(user/set-test-user)
		(def invoices (invoice/query {:limit 1 :status "created"}))
		(def brcodes [(:brcode (first invoices))])
		(def preview (first (preview/query {:brcodes brcodes})))
		(is (not (nil? (:id preview))))))
