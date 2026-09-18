(ns starkbank.merchant-session.allowed-installment
  "The MerchantSession.AllowedInstallment map declares an amount and installment-count
  combination that a MerchantSession accepts. It has no endpoints of its own: pass these
  maps in the `:allowed-installments` key of starkbank.merchant-session/create, and read
  them back from the same key of the maps it returns.

  A purchase created through starkbank.merchant-session/purchase must carry an `:amount`
  and `:installment-count` pair matching one of these, or the API answers
  amountInstallmentCountMismatch.

  ## Parameters (required):
    - `:total-amount` [integer]: total amount that can be charged in this many installments, in cents. ex: 5000 (= R$ 50.00)
    - `:count` [integer]: number of installments. ex: 1")
