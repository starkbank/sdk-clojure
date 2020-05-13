# Stark Bank Clojure SDK Beta

Welcome to the Stark Bank Clojure SDK! This tool is made for Clojure
developers who want to easily integrate with our API.
This SDK version is compatible with the Stark Bank API v2.

If you have no idea what Stark Bank is, check out our [website](https://www.StarkBank.com/)
and discover a world where receiving or making payments
is as easy as sending a text message to your client!

## Supported Clojure Versions

This library supports Clojure versions 1.10+.

## Stark Bank API documentation

Feel free to take a look at our [API docs](https://www.starkbank.com/docs/api).

## Versioning

This project adheres to the following versioning pattern:

Given a version number MAJOR.MINOR.PATCH, increment:

- MAJOR version when the **API** version is incremented. This may include backwards incompatible changes;
- MINOR version when **breaking changes** are introduced OR **new functionalities** are added in a backwards compatible manner;
- PATCH version when backwards compatible bug **fixes** are implemented.

## Setup

### 1. Install our SDK

1.1 Manually download the desired SDK version JARs found in our
[GitHub page](https://github.com/starkbank/sdk-clojure/releases/latest)
and add it to your `project.clj` as `:resource-paths ["resources/starkbank-0.2.0.jar"]`.

1.2 Using Leiningen/Boot:
```sh
[starkbank/sdk "0.2.0"]
```

1.3 Using Clojure CLI/deps.edn:
```sh
starkbank/sdk {:mvn/version "0.2.0"}
```

1.4 Using Gradle:
```sh
compile 'starkbank:sdk:0.2.0'
```

1.5 Using Maven:
```xml
<dependency>
  <groupId>starkbank</groupId>
  <artifactId>sdk</artifactId>
  <version>0.2.0</version>
</dependency>
```

### 2. Create your Private and Public Keys

We use ECDSA. That means you need to generate a secp256k1 private
key to sign your requests to our API, and register your public key
with us so we can validate those requests.

You can use one of following methods:

2.1. Check out the options in our [tutorial](https://starkbank.com/faq/how-to-create-ecdsa-keys).

2.2. Use our SDK:

```clojure
(ns my-lib.core
  (:use starkbank.core))

(def key-pair (starkbank.key/create))
(def private-pem (:private-pem key-pair))
(def public-pem (:public-pem key-pair))

;or, to also save .pem files in a specific path
(def key-pair (starkbank.key/create "file/keys/"))
(def private-pem (:private-pem key-pair))
(def public-pem (:public-pem key-pair))
```

**Note**: When you are creating a new Project, it is recommended that you create the
keys inside the infrastructure that will use it, in order to avoid risky internet
transmissions of your **private-key**. Then you can export the **public-key** alone to the
computer where it will be used in the new Project creation.

### 3. Create a Project

You need a project for direct API integrations. To create one in Sandbox:

3.1. Log into [Starkbank Sandbox](https://sandbox.web.starkbank.com)

3.2. Go to Menu > Usuários (Users) > Projetos (Projects)

3.3. Create a Project: Give it a name and upload the public key you created in section 2.

3.4. After creating the Project, get its Project ID

3.5. Use the Project ID and private key to create the object below:

```clojure
(ns my-lib.core
  (:use starkbank.core))

;Get your private key from an environment variable or an encrypted database.
;This is only an example of a private key content. You should use your own key.
(def private-key-content "
  -----BEGIN EC PARAMETERS-----
  BgUrgQQACg==
  -----END EC PARAMETERS-----
  -----BEGIN EC PRIVATE KEY-----
  MHQCAQEEIMCwW74H6egQkTiz87WDvLNm7fK/cA+ctA2vg/bbHx3woAcGBSuBBAAK
  oUQDQgAE0iaeEHEgr3oTbCfh8U2L+r7zoaeOX964xaAnND5jATGpD/tHec6Oe9U1
  IF16ZoTVt1FzZ8WkYQ3XomRD4HS13A==
  -----END EC PRIVATE KEY-----
  ")

(def project (starkbank.user/project
    "sandbox"
    "5671398416568321"
    private-key-content
  ))

;the function output is just a map, so you can also do:

(def project {
    :environment "sandbox"
    :id "5671398416568321"
    :private-key private-key-content
  })
```

NOTE 1: Never hard-code your private key. Get it from an environment variable or an encrypted database.

NOTE 2: We support `"sandbox"` and `"production"` as environments.

NOTE 3: The project you created in `sandbox` does not exist in `production` and vice versa.


### 4. Setting up the user

There are two kinds of users that can access our API: **Project** and **Member**.

- `Member` is the one you use when you log into our webpage with your e-mail.
- `Project` is designed for integrations and is the one meant for our SDK.

There are two ways to inform the user to the SDK:
 
4.1 Passing the user as argument in all functions as the last argument:

```clojure
(ns my-lib.core
  (:use starkbank.core))

(def balance (starkbank.balance/get project))
```

4.2 Set it as a default user in the SDK:

```clojure
(ns my-lib.core
  (:use starkbank.core))

(def project {
    :environment "sandbox"
    :id "5671398416568321"
    :private-key private-key-content})
(starkbank.user/set project)

(def balance (starkbank.balance/get))
```

Just select the way of passing the project user that is more convenient to you.
On all following examples we will assume a default user has been set in the configs.
We will also assume you are using `(:use starkbank.core)` on your code ns calls to
expose the starkbank SDK namespaces.

## Testing in Sandbox

Your initial balance is zero. For many operations in Stark Bank, you'll need funds
in your account, which can be added to your balance by creating a Boleto.

In the Sandbox environment, 90% of the created Boletos will be automatically paid,
so there's nothing else you need to do to add funds to your account. Just create
a few and wait around a bit.

In Production, you (or one of your clients) will need to actually pay this Boleto
for the value to be credited to your account.


## Usage

Here are a few examples on how to use the SDK. If you have any doubts, use the built-in
`doc` function to get more info on the desired functionality
(for example: `(doc starkbank.boleto)`)

**Note**: Almost all SDK functions also provide a bang (!) version. To simplify the examples, they will be used the most throughout this README.

### Get balance

To know how much money you have in your workspace, run:

```clojure
(def balance (starkbank.balance/get))
(println (double (/ (:amount balance) 100)))
```

### Create boletos

You can create boletos to charge customers or to receive money from accounts
you have in other banks.

```clojure
(def boletos (starkbank.boleto/create [{
  :amount 23571; R$ 235,71
  :name "Buzz Aldrin"
  :tax-id "012.345.678-90"
  :street-line-1 "Av. Paulista 200"
  :street-line-2 "10 andar"
  :district "Bela Vista"
  :city "São Paulo"
  :state-code "SP"
  :zip-code "01310-000"
  :due (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date.))
  :fine 5; 5%
  :interest 2.5; 2.5% per month
}]))
(println boletos)
```

### Get boleto

After its creation, information on a boleto may be retrieved by passing its id.
Its status indicates whether it's been paid.

```clojure
(def boleto (starkbank.boleto/get "6750458353811456"))

(println boleto)
```

### Get boleto PDF

After its creation, a boleto PDF may be retrieved by passing its id.

```clojure
(clojure.java.io/copy
  (starkbank.boleto/pdf "6750458353811456")
  (clojure.java.io/file "boleto.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

### Delete boleto

You can also cancel a boleto by its id.
Note that this is not possible if it has been processed already.

```clojure
(def boleto (starkbank.boleto/delete "5202697619767296"))
(println boleto)
```

### Query boletos

You can get a stream of created boletos given some filters.

```clojure
(def boletos
  (starkbank.boleto/query
    {
      :after (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (- (.getTime (java.util.Date.)) (* 2 86400 1000))))
      :before (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (- (.getTime (java.util.Date.)) (* 1 86400 1000))))
      :limit 10
    }))
(println boletos)
```

### Query boleto logs

Logs are pretty important to understand the life cycle of a boleto.

```clojure
(def logs
  (starkbank.boleto.log/query
    {
      :boleto-ids ["6750458353811456"]
    }))
(println logs)
```

### Get a boleto log

You can get a single log by its id.

```clojure
(def log (starkbank.boleto.log/get "6288576484474880"))
(println log)
```

### Create transfers

You can also create transfers in the SDK (TED/DOC).

```clojure
(def transfers
  (starkbank.transfer/create
    [
      {
        :amount 100
        :bank-code "01"
        :branch-code "0001"
        :account-number "10000-0"
        :tax-id "012.345.678-90"
        :name "Tony Stark"
        :tags ["iron" "suit"]
      }
      {
        :amount 200
        :bank-code "341"
        :branch-code "1234"
        :account-number "123456-7"
        :tax-id "012.345.678-90"
        :name "Jon Snow"
      }
    ]))
(println transfers)
```

### Query transfers

You can query multiple transfers according to filters.

```clojure
(def transfers
  (starkbank.transfer/query
    {
      :after (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (- (.getTime (java.util.Date.)) (* 2 86400 1000))))
      :before (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (- (.getTime (java.util.Date.)) (* 1 86400 1000))))
      :limit 10
    }))

(println transfers)
```

### Get transfer

To get a single transfer by its id, run:

```clojure
(def transfer (starkbank.transfer/get "4882890932355072"))
(println transfer)
```

### Get transfer PDF

A transfer PDF may also be retrieved by passing its id.
This operation is only valid for transfers with "processing" or "success" status.

```clojure
(clojure.java.io/copy
  (starkbank.transfer/pdf "4882890932355072")
  (clojure.java.io/file "transfer.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

### Query transfer logs

You can query transfer logs to better understand transfer life cycles.

```clojure
(def logs
  (starkbank.transfer.log/query
    {
      :limit 50
    }))
(println logs)
```

### Get a transfer log

You can also get a specific log by its id.

```clojure
(def log (starkbank.transfer.log/get "6610264099127296"))
(println log)
```

### Pay a boleto

Paying a boleto is also simple.

```clojure
(def payments
  (starkbank.boleto-payment/create
    [
      {
        :line "34191.09008 64694.017308 71444.640008 1 96610000014500"
        :tax-id "012.345.678-90"
        :scheduled (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (+ (.getTime (java.util.Date.)) (* 10 86400 1000))))
        :description "take my money"
        :tags ["take" "my" "money"]
      }
      {
        :bar-code "34191972300000289001090064694197307144464000"
        :tax-id "012.345.678-90"
        :scheduled (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (+ (.getTime (java.util.Date.)) (* 40 86400 1000))))
        :description "take my money one more time"
        :tags ["again"]
      }
    ]))
(println payments)
```

### Get boleto payment

To get a single boleto payment by its id, run:

```clojure
(def payment (starkbank.boleto-payment/get "5629412477239296"))
(println payment)
```

### Get boleto payment PDF

After its creation, a boleto payment PDF may be retrieved by passing its id.

```clojure
(clojure.java.io/copy
  (starkbank.boleto-payment/pdf "6497770399596544")
  (clojure.java.io/file "boleto-payment.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

### Delete boleto payment

You can also cancel a boleto payment by its id.
Note that this is not possible if it has been processed already.

```clojure
(def payment (starkbank.boleto-payment/delete "5629412477239296"))
(println payment)
```

### Query boleto payments

You can search for boleto payments using filters.

```clojure
(def payments
  (starkbank.boleto-payment/query
    {
      :tags ["company_1", "company_2"]
      :limit 10
    }))
(println payments)
```

### Query boleto payment logs

Searches are also possible with boleto payment logs:

```clojure
(def logs
  (starkbank.boleto-payment.log/query
    {
      :payment-ids ["5629412477239296" "5199478290120704"]
    }))
(println logs)
```

### Get boleto payment log

You can also get a boleto payment log by specifying its id.

```clojure
(def log (starkbank.boleto-payment.log/get "5391671273455616"))
(println log)
```

### Create utility payment

It's also simple to pay utility bills (such as electricity and water bills) in the SDK.

```clojure
(def payments
  (starkbank.utility-payment/create
    [
      {
        :bar-code "83600000001522801380037107172881100021296561"
        :scheduled (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (+ (.getTime (java.util.Date.)) (* 2 86400 1000))))
        :description "paying some bills"
        :tags ["take" "my" "money"]
      }
      {
        :line "83680000001 7 08430138003 0 71070987611 8 00041351685 7"
        :scheduled (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date. (+ (.getTime (java.util.Date.)) (* 3 86400 1000))))
        :description "never ending bills"
        :tags ["again"]
      }
    ]))
(println payments)
```

### Query utility payments

To search for utility payments using filters, run:

```clojure
(def payments
  (starkbank.utility-payment/query
    {
      :tags ["electricity" "gas"]
      :limit 10
    }))
(println payments)
```

### Get utility payment

You can get a specific bill by its id:

```clojure
(def payment (starkbank.utility-payment/get "6619425641857024"))
(println payment)
```

### Get utility payment PDF

After its creation, a utility payment PDF may also be retrieved by passing its id.

```clojure
(clojure.java.io/copy
  (starkbank.utility-payment/pdf "4663663059271680")
  (clojure.java.io/file "utility-payment.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

### Delete utility payment

You can also cancel a utility payment by its id.
Note that this is not possible if it has been processed already.

```clojure
(def payment (starkbank.utility-payment/delete "6619425641857024"))
(println payment)
```

### Query utility bill payment logs

You can search for payments by specifying filters. Use this to understand the
bills life cycles.

```clojure
(def logs
  (starkbank.utility-payment.log/query
    {
      :payment-ids ["6619425641857024" "5738969660653568"]
    }))
(println logs)
```

### Get utility bill payment log

If you want to get a specific payment log by its id, just run:

```clojure
(def log (starkbank.utility-payment.log/get "6197807794880512"))
(println log)
```

### Create transactions

To send money between Stark Bank accounts, you can create transactions:

```clojure
(def transactions
  (starkbank.transaction/create
    [
      {
        :amount 100; (R$ 1,00)
        :receiver-id "5768064935133184"
        :description "Transaction to dear provider"
        :external-id "12345"; so we can block anything you send twice by mistake
        :tags ["provider"]
      }
      {
        :amount 234; (R$ 2,34)
        :receiver-id "5768064935133184"
        :description "Transaction to the other provider"
        :external-id "12346"; so we can block anything you send twice by mistake
        :tags ["provider"]
      }
    ]))
(println transactions)
```

### Query transactions

To understand your balance changes (bank statement), you can query
transactions. Note that our system creates transactions for you when
you receive boleto payments, pay a bill or make transfers, for example.

```clojure
(def transactions
  (starkbank.transaction/query
    {
      :after "2020-03-20"
      :before "2020-03-30"
      :limit 10
    }))
(println transactions)
```

### Get transaction

You can get a specific transaction by its id:

```clojure
(def transaction (starkbank.transaction/get "6677396233125888"))
(println transaction)
```

### Create a webhook subscription

To create a webhook subscription and be notified whenever an event occurs, run:

```clojure
(def webhook
  (starkbank.webhook/create
    {
      :url "https://webhook.site/dd784f26-1d6a-4ca6-81cb-fda0267761ec"
      :subscriptions ["transfer" "boleto" "boleto-payment" "utility-payment"]
    }))
(println webhook)
```

### Query webhooks

To search for registered webhooks, run:

```clojure
(def webhooks (starkbank.webhook/query))
(println webhooks)
```

### Get webhook

You can get a specific webhook by its id.

```clojure
(def webhook (starkbank.webhook/get "6178044066660352"))
(println webhook)
```

### Delete webhook

You can also delete a specific webhook by its id.

```clojure
(def webhook (starkbank.webhook/delete "5083659757420544"))
(println webhook)
```

### Process webhook events

It's easy to process events that have arrived in your webhook. Remember to pass the
signature header so the SDK can make sure it's really StarkBank that has sent you
the event.

```clojure
(def response (listen)); this is the function you made to get the events posted to your webhook

(def event
  (starkbank.event/parse
    (:content response)
    (:Digital-Signature (:headers response))))
(println event)
```

If the data does not check out with the Stark Bank public-key, the function will automatically request the
key from the API and try to validate the signature once more. If it still does not check out, it will raise an error.

### Query webhook events

To search for webhooks events, run:

```clojure
(def events
  (starkbank.event/query
    {
      :after "2020-03-20"
      :is-delivered false
      :limit 10
    }))
(println events)
```

### Get webhook event

You can get a specific webhook event by its id.

```clojure
(def event (starkbank.event/get "6597859067559936"))
(println event)
```

### Delete webhook event

You can also delete a specific webhook event by its id.

```clojure
(def event (starkbank.event/delete "4568139664719872"))
(println event)
```

### Set webhook events as delivered

This can be used in case you've lost events.
With this function, you can manually set events retrieved from the API as
"delivered" to help future event queries with `is_delivered: false`.

```clojure
(def event (starkbank.event/update "5764442407043072" {:is-delivered true}))
(println event)
```

## Handling errors

The SDK may raise one of four types of errors: __InputErrors__, __InternalServerError__, __UnknownException__, __InvalidSignatureException__

__InputErrors__ will be raised whenever the API detects an error in your request (status code 400).
If you catch such an error, you can get its elements to verify each of the
individual errors that were detected in your request by the API.

For example:

```clojure
(try
  (starkbank.transaction/create [
    {
      :amount -200
      :receiver-id "123"
      :description "."
      :external-id "repeated id"
      :tags ["error" "example"]
    }
  ])
  (catch com.starkbank.error.InputErrors e
    (map
      (fn [element] (str "error-code: " (.code element) "\nerror-message: " (.message element)))
      (.errors e))))
```

__InternalServerError__ will be raised if the API runs into an internal error.
If you ever stumble upon this one, rest assured that the development team
is already rushing in to fix the mistake and get you back up to speed.

__UnknownException__ will be raised if a request encounters an error that is
neither __InputErrors__ nor an __InternalServerError__, such as connectivity problems.

__InvalidSignatureException__ will be raised specifically by starkbank.event.parse()
when the provided content and signature do not check out with the Stark Bank public
key.
