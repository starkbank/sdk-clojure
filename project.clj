(defproject starkbank/sdk "2.5.2"
  :description "SDK to make Clojure integrations with the Stark Bank API easier."
  :url "https://github.com/starkbank/sdk-clojure"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [com.starkbank/sdk "2.8.2"]
                 [clj-http "3.12.3"]
                 [org.clojars.stark-mtkgs/core-clojure "0.1.0"]
                 [org.clojure/data.json "2.4.0"]
                 [com.starkbank.ellipticcurve/starkbank-ecdsa "1.0.2"]
                 [clj-time "0.15.2"]
                 [cheshire "5.10.0"]]
  :repl-options {:init-ns starkbank.core})
