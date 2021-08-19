(ns starkbank.core
  "SDK to facilitate Clojure integrations with the StarkBank API."
  (:refer-clojure :exclude [get set update])
  (:require [starkbank.user]
            [starkbank.settings]
            [starkbank.key]
            [starkbank.balance]
            [starkbank.transaction]
            [starkbank.invoice]
            [starkbank.deposit]
            [starkbank.boleto]
            [starkbank.boleto-holmes]
            [starkbank.transfer]
            [starkbank.brcode-preview]
            [starkbank.brcode-payment]
            [starkbank.boleto-payment]
            [starkbank.darf-payment]
            [starkbank.tax-payment]
            [starkbank.utility-payment]
            [starkbank.payment-request]
            [starkbank.dict-key]
            [starkbank.institution]
            [starkbank.event]
            [starkbank.webhook]
            [starkbank.workspace]))
