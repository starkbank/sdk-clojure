(ns starkbank.darf-payment-test
  (:use [clojure.test])
  (:require [starkbank.darf-payment :as payment]
            [starkbank.darf-payment.log :as log]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]))

(deftest create-get-pdf-delete-darf-payments
  (testing "create, get, pdf and delete darf payments"
    (user/set-test-project)
    (def payments (payment/create
      [{
        :description "Darf Payment Example"
        :tags ["testing" "clojure"]
        :due (date/future-date 1)
        :competence "2020-04-03"
        :fine-amount 100
        :interest-amount "100"
        :nominal-amount "1000"
        :revenue-code "0201"
        :tax-id "45678350005"
        :scheduled (date/future-date)
      }]))
     (payment/get (:id (first payments)))
     (def file-name "temp/darf-payment.pdf")
     (io/make-parents file-name)
     (io/copy (payment/pdf (:id (first payments))) (io/file file-name))
     (payment/delete (:id (first payments)))
    ))

(deftest query-darf-payments
  (testing "query darf payments"
    (user/set-test-project)
    (def payments (take 200 (payment/query {:limit 3})))
    (is (= 3 (count payments)))))

(deftest page-darf-payments
  (testing "page darf-payments"
    (user/set-test-project)
    (def get-page (fn [params] (payment/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))

(deftest query-get-darf-payment-logs
  (testing "query and get darf payment logs"
    (user/set-test-project)
    (def payment-logs (log/query {:limit 1}))
    (is (= 1 (count payment-logs)))
    (def payment-log (log/get (:id (first payment-logs))))
    (is (not (nil? (:id payment-log))))
    (is (not (nil? (:errors payment-log))))
    (is (string? (:created payment-log)))
    (is (map? (:payment payment-log)))))

(deftest page-darf-payment-logs
  (testing "page darf-payment-logs"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 2 {:limit 2}))
    (is (= 4 (count ids)))))
