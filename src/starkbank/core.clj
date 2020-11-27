(ns starkbank.core
  "SDK to facilitate Clojure integrations with the StarkBank API."
  (:refer-clojure :exclude [get set update])
  (:require [starkbank.user]
            [starkbank.settings]
            [starkbank.key]
            [starkbank.balance]
            [starkbank.transaction]
            [starkbank.boleto]
            [starkbank.transfer]
            [starkbank.boleto-payment]
            [starkbank.utility-payment]
            [starkbank.event]
            [starkbank.webhook]))
