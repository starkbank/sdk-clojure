(ns starkbank.invoice.rule
  "The Invoice.Rule map modifies the behavior of an Invoice. Rule has no endpoints of
  its own: pass these maps in the `:rules` key of starkbank.invoice/create, and read
  them back from the `:rules` key of the maps it returns.

  ## Parameters (required):
    - `:key` [string]: rule to be customized, describes what Invoice behavior will be altered. ex: \"allowedTaxIds\"
    - `:value` [list of strings]: value of the rule. ex: [\"012.345.678-90\", \"45.059.493/0001-73\"]")
