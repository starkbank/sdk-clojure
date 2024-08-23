(ns starkbank.payment-preview-test
  (:use [clojure.test])
  (:require [starkbank.boleto :as boleto]
            [starkbank.invoice :as invoice]
            [starkbank.payment-preview :as payment-preview]
            [starkbank.utils.user :refer [set-project]]))

(set-project)

(deftest create-payment-previews
  (testing "create payment-previews" 
    (def previews
      [{:id (:brcode (first (invoice/query {:limit 1})))}
       {:id (:line (first (boleto/query {:limit 1})))}
       {:id (str "8566000" (format "%08d" (rand-int 20000000)) "00640074119002551100010601813")}
       {:id (str "8364000" (format "%08d" (rand-int 20000000)) "01380076105302611108067159411")}
       ]) 

    (def payment-previews (take 4 (payment-preview/create previews)))

    (doseq [preview payment-previews]
      (is (not (nil? (:id preview)))))
    ))
