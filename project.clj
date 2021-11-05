(defproject starkbank/sdk "2.5.1"
  :description "SDK to make Clojure integrations with the Stark Bank API easier."
  :url "https://github.com/starkbank/sdk-clojure"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [
    [com.starkbank/sdk "2.8.1"]
    [org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns starkbank.core})
