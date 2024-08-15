(defproject org.clojars.luistarkbank/starkbank "0.0.3"
  :description "SDK to make Clojure integrations with the Stark Bank API easier."
  :url "https://github.com/starkbank/sdk-clojure"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :plugins [[lein-release "1.0.9"]]
  :dependencies [[com.starkbank/sdk "2.8.2"]
                 [org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns starkbank.core})
  :repositories [["releases" {:url "https://repo.clojars.org/"
                            :creds :gpg}]]