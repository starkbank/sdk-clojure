(ns starkbank.payment-preview-test
  (:use [clojure.test])
  (:require [starkbank.payment-preview :as payment-preview]
            [starkbank.boleto-payment :as boleto-payment]
            [starkbank.boleto :as boleto]
            [starkbank.user-test :as user]
            [clojure.java.io :as io]
            [starkbank.utils.date :as date]
            [starkbank.utils.page :as page]))

(deftest create-payment-previews
  (testing "create payment-previews"
    (user/set-test-project)
    (def previews
      [
        {:id "00020101021226890014br.gov.bcb.pix2567invoice-h.sandbox.starkbank.com/v2/d5b00b1994454706ba90a0387ff39b7952040000530398654040.005802BR5925Afel Tec Servicos Adminis6009Sao Paulo62070503***630475CE"}
        {:id (:line (rand-nth (boleto/query {:limit 200})))}
        {:id (str "8566000" (format "%08d" (rand-int 20000000)) "00640074119002551100010601813")}
        {:id (str "8364000" (format "%08d" (rand-int 20000000)) "01380076105302611108067159411")}
      ])

    (def payment-previews (take 4 (payment-preview/create previews)))
    
    (doseq [preview payment-previews]
      (is (not (nil? (:id preview)))))
  ))
