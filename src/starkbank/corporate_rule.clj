(ns starkbank.corporate-rule
  "The CorporateRule map displays the spending rules of CorporateCards and CorporateHolders
  created in your Workspace. CorporateRule has no endpoints of its own: pass these maps in
  the `:rules` key of starkbank.corporate-card/create and starkbank.corporate-holder/create,
  and read them back from the `:rules` key of the maps those functions return.

  ## Parameters (required):
    - `:name` [string]: rule name. ex: \"Travel\" or \"Food\"
    - `:amount` [integer]: maximum amount that can be spent in the informed interval. ex: 200000 (= R$ 2000.00)

  ## Parameters (optional):
    - `:interval` [string, default \"lifetime\"]: interval after which the rule amount counter will be reset to 0. ex: \"instant\", \"day\", \"week\", \"month\", \"year\" or \"lifetime\"
    - `:schedule` [string, default nil]: schedule time for user to spend. ex: \"every monday, wednesday from 00:00 to 23:59 in America/Sao_Paulo\"
    - `:purposes` [list of strings, default nil]: list of strings representing where the rule can be applied. ex: [\"purchase\", \"withdrawal\"]
    - `:currency-code` [string, default \"BRL\"]: code of the currency that the rule amount refers to. ex: \"BRL\" or \"USD\"
    - `:categories` [list of maps, default nil]: merchant categories accepted by the rule. Each map has `:code` and `:type` keys. ex: [{:code \"food\" :type \"services\"}]
    - `:countries` [list of maps, default nil]: countries accepted by the rule. Each map has `:code`, `:name`, `:number` and `:short-code` keys.
    - `:methods` [list of maps, default nil]: card purchase methods accepted by the rule. Each map has `:code` and `:name` keys. ex: [{:code \"token\" :name \"Token\"}]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when a CorporateRule is created, used to update a specific CorporateRule. ex: \"5656565656565656\"
    - `:counter-amount` [integer]: current rule spent amount. ex: 1000
    - `:currency-symbol` [string]: currency symbol. ex: \"R$\"
    - `:currency-name` [string]: currency name. ex: \"Brazilian Real\"")
