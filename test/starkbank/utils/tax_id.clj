(ns starkbank.utils.tax-id
  "Generates valid CPF numbers for tests. The API caps VerifiedAccount creation at
  three per tax id every 24h, so fixtures that create bank-details VerifiedAccounts
  need a fresh tax id on every run instead of a fixed one.")

(defn- check-digit [digits]
  (let [weight (inc (count digits))
        sum (reduce + (map-indexed (fn [i d] (* d (- weight i))) digits))
        remainder (mod sum 11)]
    (if (< remainder 2) 0 (- 11 remainder))))

(defn random-cpf
  "Generates a random, check-digit-valid CPF, formatted as \"012.345.678-90\"."
  []
  (let [base (repeatedly 9 #(rand-int 10))
        first-check (check-digit base)
        with-first (concat base [first-check])
        second-check (check-digit with-first)
        digits (vec (concat with-first [second-check]))]
    (apply format "%d%d%d.%d%d%d.%d%d%d-%d%d" digits)))
