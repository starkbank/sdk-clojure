(ns starkbank.transfer.rule
  "The Transfer.Rule map modifies the behavior of a Transfer. Rule has no endpoints of
  its own: pass these maps in the `:rules` key of starkbank.transfer/create, and read
  them back from the `:rules` key of the maps it returns.

  ## Parameters (required):
    - `:key` [string]: rule to be customized, describes what Transfer behavior will be altered. ex: \"resendingLimit\"
    - `:value` [integer]: value of the rule. ex: 5")
