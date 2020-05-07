(defproject starkbank/sdk "0.1.1"
  :description "SDK to make Clojure integrations with the Stark Bank API easier."
  :url "https://github.com/starkbank/sdk-clojure"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :resource-paths ["resources/starkbank-java-0.2.1.jar"]
  :repl-options {:init-ns starkbank.core})
