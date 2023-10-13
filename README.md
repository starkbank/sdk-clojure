# Stark Bank Clojure SDK

Welcome to the Stark Bank Clojure SDK! This tool is made for Clojure
developers who want to easily integrate with our API.
This SDK version is compatible with the Stark Bank API v2.

If you have no idea what Stark Bank is, check out our [website](https://www.StarkBank.com/)
and discover a world where receiving or making payments
is as easy as sending a text message to your client!

# Introduction

# Index

- [Introduction](#introduction)
    - [Supported Clojure versions](#supported-clojure-versions)
    - [API documentation](#stark-bank-api-documentation)
    - [Versioning](#versioning)
- [Setup](#setup)
    - [Install our SDK](#1-install-our-sdk)
    - [Create your Private and Public Keys](#2-create-your-private-and-public-keys)
    - [Register your user credentials](#3-register-your-user-credentials)
    - [Setting up the user](#4-setting-up-the-user)
    - [Setting up the error language](#5-setting-up-the-error-language)
    - [Resource listing and manual pagination](#6-resource-listing-and-manual-pagination)
- [Testing in Sandbox](#testing-in-sandbox) 
- [Usage](#usage)
    - [Transactions](#create-transactions): Account statement entries
    - [Balance](#get-balance): Account balance
    - [Transfers](#create-transfers): Wire transfers (TED and manual Pix)
    - [DictKeys](#get-dict-key): Pix Key queries to use with Transfers
    - [Institutions](#query-bacen-institutions): Instutitions recognized by the Central Bank
    - [Invoices](#create-invoices): Reconciled receivables (dynamic PIX QR Codes)
    - [Deposits](#query-deposits): Other cash-ins (static PIX QR Codes, manual PIX, etc)
    - [Boletos](#create-boletos): Boleto receivables
    - [BoletoHolmes](#investigate-a-boleto): Boleto receivables investigator
    - [BrcodePayments](#pay-a-br-code): Pay Pix QR Codes
    - [BoletoPayments](#pay-a-boleto): Pay Boletos
    - [UtilityPayments](#create-utility-payments): Pay Utility bills (water, light, etc.)
    - [TaxPayments](#create-tax-payments): Pay taxes
    - [PaymentPreviews](#preview-payment-information-before-executing-the-payment): Preview all sorts of payments
    - [Webhooks](#create-a-webhook-subscription): Configure your webhook endpoints and subscriptions
    - [WebhookEvents](#process-webhook-events): Manage webhook events
    - [WebhookEventAttempts](#query-failed-webhook-event-delivery-attempts-information): Query failed webhook event deliveries
    - [Workspaces](#create-a-new-workspace): Manage your accounts
- [Handling errors](#handling-errors)
- [Help and Feedback](#help-and-feedback)

# Supported Clojure Versions

This library supports Clojure versions 1.10+.

# Stark Bank API documentation

Feel free to take a look at our [API docs](https://www.starkbank.com/docs/api).

# Versioning

This project adheres to the following versioning pattern:

Given a version number MAJOR.MINOR.PATCH, increment:

- MAJOR version when the **API** version is incremented. This may include backwards incompatible changes;
- MINOR version when **breaking changes** are introduced OR **new functionalities** are added in a backwards compatible manner;
- PATCH version when backwards compatible bug **fixes** are implemented.

# Setup

## 1. Install our SDK

1.1 Manually download the desired SDK version JARs found in our
[GitHub page](https://github.com/starkbank/sdk-clojure/releases/latest)
and add it to your `project.clj` as `:resource-paths ["resources/starkbank-2.5.2.jar"]`.

1.2 Using Leiningen/Boot:
```sh
[starkbank/sdk "2.5.2"]
```

1.3 Using Clojure CLI/deps.edn:
```sh
starkbank/sdk {:mvn/version "2.5.2"}
```

1.4 Using Gradle:
```sh
compile 'starkbank:sdk:2.5.2'
```

1.5 Using Maven:
```xml
<dependency>
  <groupId>starkbank</groupId>
  <artifactId>sdk</artifactId>
  <version>2.5.2</version>
</dependency>
```

## 2. Create your Private and Public Keys

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
(def private-key (:private-key key-pair))
(def public-key (:public-key key-pair))

;or, to also save .pem files in a specific path
(def key-pair (starkbank.key/create "file/keys/"))
(def private-key (:private-key key-pair))
(def public-key (:public-key key-pair))
```

**NOTE**: When you are creating new credentials, it is recommended that you create the
keys inside the infrastructure that will use it, in order to avoid risky internet
transmissions of your **private-key**. Then you can export the **public-key** alone to the
computer where it will be used in the new Project creation.

## 3. Register your user credentials

You can interact directly with our API using two types of users: Projects and Organizations.

- **Projects** are workspace-specific users, that is, they are bound to the workspaces they are created in.
One workspace can have multiple Projects.
- **Organizations** are general users that control your entire organization.
They can control all your Workspaces and even create new ones. The Organization is bound to your company's tax ID only.
Since this user is unique in your entire organization, only one credential can be linked to it.

3.1. To create a Project in Sandbox:

3.1.1. Log into [Starkbank Sandbox](https://web.sandbox.starkbank.com)

3.1.2. Go to Menu > Integrations

3.1.3. Click on the "New Project" button

3.1.4. Create a Project: Give it a name and upload the public key you created in section 2

3.1.5. After creating the Project, get its Project ID

3.1.6. Use the Project ID and private key to create the object below:

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
```

3.2. To create Organization credentials in Sandbox:

3.2.1. Log into [Starkbank Sandbox](https://web.sandbox.starkbank.com)

3.2.2. Go to Menu > Integrations

3.2.3. Click on the "Organization public key" button

3.2.4. Upload the public key you created in section 2 (only a legal representative of the organization can upload the public key)

3.2.5. Click on your profile picture and then on the "Organization" menu to get the Organization ID

3.2.6. Use the Organization ID and private key to create the object below:

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

(def organization (starkbank.user/organization
    "sandbox"
    "5671398416568321"
    private-key-content
    nil; You only need to set the workspace-id when you are operating a specific workspace-id
  ))

;To dynamically use your organization credentials in a specific workspace-id,
;you can use the user/organization-replace function:
(starkbank.balance/get (starkbank.user/organization-replace organization "4848484848484848"))
```

NOTE 1: Never hard-code your private key. Get it from an environment variable or an encrypted database.

NOTE 2: We support `'sandbox'` and `'production'` as environments.

NOTE 3: The credentials you registered in `sandbox` do not exist in `production` and vice versa.


## 4. Setting up the user

There are three kinds of users that can access our API: **Organization**, **Project** and **Member**.

- `Project` and `Organization` are designed for integrations and are the ones meant for our SDKs.
- `Member` is the one you use when you log into our webpage with your e-mail.

There are two ways to inform the user to the SDK:
 
4.1 Passing the user as argument in all functions as the last argument:

```clojure
(ns my-lib.core
  (:use starkbank.core))

(def user (starkbank.user/project; or organization
    "sandbox"
    "5671398416568321"
    private-key-content))

(def balance (starkbank.balance/get user))
```

4.2 Set it as a default user in the SDK:

```clojure
(ns my-lib.core
  (:use starkbank.core))

(def user (starkbank.user/project; or organization
    "sandbox"
    "5671398416568321"
    private-key-content))

(starkbank.settings/user user)

(def balance (starkbank.balance/get))
```

Just select the way of passing the project user that is more convenient to you.
On all following examples we will assume a default user has been set in the configs.
We will also assume you are using `(:use starkbank.core)` on your code ns calls to
expose the starkbank SDK namespaces.

# 5. Setting up the error language

The error language can also be set in the same way as the default user:

```clojure
(:require [starkbank.settings :as settings])

(settings/language "pt-BR")
```

Language options are "en-US" for english and "pt-BR" for brazilian portuguese. English is default.

## 6. Resource listing and manual pagination

Almost all SDK resources provide a `query` and a `page` function.

- The `query` function provides a straight forward way retrieve results that match the filters you inform, seamlessly retrieving all the filtered elements from the API.
If you are not worried about data volume or processing time, this is the way to go.

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

- The `page` function gives you full control over the API pagination. With each function call, you receive up to
100 results and the cursor to retrieve the next batch of elements. This allows you to stop your queries and
pick up from where you left off whenever it is convenient. When there are no more elements to be retrieved, the returned cursor will be `nil`.

```clojure
(defn get-page
  [iterations, cursor]
    (when (> iterations 0)
      (def page (transaction/page {:limit 2}))
      (def new-cursor (get page :cursor))
      (def new-entities (get page :transactions))
      (concat new-entities (get-page (- iterations 1) new-cursor))))

(println (get-page 3 nil))
```

To simplify the following SDK examples, we will only use the `query` function, but feel free to use `page` instead.

# Testing in Sandbox

Your initial balance is zero. For many operations in Stark Bank, you'll need funds
in your account, which can be added to your balance by creating an Invoice or a Boleto. 

In the Sandbox environment, most of the created Invoices and Boletos will be automatically paid,
so there's nothing else you need to do to add funds to your account. Just create
a few Invoices and wait around a bit.

In Production, you (or one of your clients) will need to actually pay this Invoice or Boleto
for the value to be credited to your account.


# Usage

Here are a few examples on how to use the SDK. If you have any doubts, use the built-in
`doc` function to get more info on the desired functionality
(for example: `(doc starkbank.boleto)`)

## Create transactions

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

## Query transactions

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

## Get transaction

You can get a specific transaction by its id:

```clojure
(def transaction (starkbank.transaction/get "6677396233125888"))

(println transaction)
```

## Get balance

To know how much money you have in your workspace, run:

```clojure
(def balance (starkbank.balance/get))

(println (double (/ (:amount balance) 100)))
```

## Create transfers

You can also create transfers in the SDK (TED/Pix).

```clojure
(def transfers
  (starkbank.transfer/create
    [
      {
        :amount 100
        :bank-code "20018183"; Pix
        :branch-code "0001"
        :account-number "10000-0"
        :account-type "checking"
        :external-id "my-internal-id-12345"
        :tax-id "012.345.678-90"
        :name "Tony Stark"
        :tags ["iron" "suit"]
      }
      {
        :amount 200
        :bank-code "341"; TED
        :branch-code "1234"
        :account-number "123456-7"
        :tax-id "012.345.678-90"
        :name "Jon Snow"
      }
    ]))

(println transfers)
```

## Query transfers

You can query multiple transfers according to filters.

```clojure
(def transfers
  (starkbank.transfer/query
    {
      :after "2020-11-01"
      :before "2020-12-01"
      :limit 10
    }))

(println transfers)
```

## Get a transfer

To get a single transfer by its id, run:

```clojure
(def transfer (starkbank.transfer/get "4882890932355072"))

(println transfer)
```

## Get a transfer PDF

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

## Query transfer logs

You can query transfer logs to better understand transfer life cycles.

```clojure
(def logs
  (starkbank.transfer.log/query
    {
      :limit 50
    }))

(println logs)
```

## Get a transfer log

You can also get a specific log by its id.

```clojure
(def log (starkbank.transfer.log/get "6610264099127296"))

(println log)
```

## Get DICT key

You can get Pix key's parameters by its id.

```clojure
(def dict-key (starkbank.dict-key/get "tony@starkbank.com"))

(println dict-key)
```

## Query your DICT keys

To take a look at the Pix keys linked to your workspace, just run the following:

```clojure
(def dict-keys (starkbank.dict-key/query {:limit 5}))

(doseq [dict-key dict-keys]
  (printn dict-key))
```

## Query Bacen institutions

 You can query institutions registered by the Brazilian Central Bank for Pix and TED transactions.

 ```clojure
(def institutions (institution/query {:search "stark"}))
(println institutions)
 ```

## Create invoices

You can create dynamic QR Code invoices to charge customers or to receive money from accounts you have in other banks.

Since the banking system only understands value modifiers (discounts, fines and interest) when dealing with **dates** (instead of **datetimes**), these values will only show up in the end user banking interface if you use **dates** in the "due" and "discounts" fields.

If you use **datetimes** instead, our system will apply the value modifiers in the same manner, but the end user will only see the final value to be paid on his interface.

Also, other banks will most likely only allow payment scheduling on invoices defined with **dates** instead of **datetimes**.

```clojure
(def invoices (starkbank.invoice/create
  [{
    :tags [ "immediate" ]
    :amount 400000
    :due "2020-12-25T19:32:35.418698+00:00"
    :tax-id "012.345.678-90"
    :name "Iron Bank S.A."
    :expiration 123456789
    :fine 2.5
    :interest 1.3
    :descriptions [
      {
        :key "Product X"
        :value "big"
      }
    ]
  }
  {
    :tags [ "scheduled" ]
    :amount 23571
    :due "2021-11-28"
    :taxId "012.345.678-90"
    :name "Buzz Aldrin"
    :expiration 123456789
    :fine 5
    :interest 2.5
    :discounts [
      {
        :percentage 5
        :due "2021-11-27"
      }
    ]
  }]))

(doseq [invoice invoices]
  (println invoice))
```

## Get an invoice

After its creation, information on an invoice may be retrieved by its id. 
Its status indicates whether it's been paid.

```clojure
(def invoice (starkbank.invoice/get "6750458353811456"))

(println invoice)
```

## Get an invoice PDF

After its creation, an invoice PDF may be retrieved by its id. 

```clojure
(clojure.java.io/copy
  (starkbank.invoice/pdf "6750458353811456")
  (clojure.java.io/file "invoice.pdf"))
```

## Get an invoice QR Code

After its creation, an Invoice QR Code may be retrieved by its id. 

```clojure
(clojure.java.io/copy
  (starkbank.invoice/qrcode "6750458353811456")
  (clojure.java.io/file "invoice.png"))
```

Be careful not to accidentally enforce any encoding on the raw png content,
as it may yield abnormal results in the final file.

## Cancel an invoice

You can also cancel an invoice by its id.
Note that this is not possible if it has been paid already.

```clojure
(def invoice (starkbank.invoice/update "6750458353811456"))
  {
    :status "canceled"
  }))

(println invoice)
```

## Update an invoice

You can update an invoice's amount, due date and expiration by its id.
Note that this is not possible if it has been paid already.

```clojure
(def invoices (starkbank.invoice/update "6750458353811456"))
  {
    :amount 10
    :expiration 600
    :due "2020-12-20T19:32:35.418698+00:00"
  })))

(doseq [invoice invoices]
  (println invoice))
```

## Query invoices

You can get a list of created invoices given some filters.

```clojure

(def invoices (starkbank.invoice/query
  {
    :limit 5
    :status "created"
  }))

(doser [invoice invoices]
  (println invoice))
```

## Query invoice logs

Logs are pretty important to understand the life cycle of an invoice.

```clojure
(def logs (starkbank.invoice.log/query
  {
    :limit 5
  }))

(doseq [log logs]
  (println log))
```

## Get an invoice log

You can get a single log by its id.

```clojure
(def log (starkbank.invoice.log/get "6288576484474880"))

(println log)
```

## Get a reversed invoice log PDF

Whenever an Invoice is successfully reversed, a reversed log will be created.
To retrieve a specific reversal receipt, you can request the corresponding log PDF:

```clojure
(clojure.java.io/copy
  (starkbank.invoice.log/pdf "6750458353811456")
  (clojure.java.io/file "invoice-log.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

## Get an invoice payment information

Once an invoice has been paid, you can get the payment information using the Invoice.Payment sub-resource:

```clojure
(def payment (starkbank.invoice/payment "4187263519823719"))

(println payment)
```

## Query deposits

You can get a list of created deposits given some filters.

```clojure
(def deposits (starkbank.deposit/query
  {
    :limit 5
  }))

(doseq [deposit deposits]
  (println deposit))
```

## Update a Deposit

Update a deposit by passing its id to be partially or fully reversed.

```clojure
(def deposits (starkbank.deposit/update "6750458353811456"))
  {
    :amount 0
  })))

(doseq [deposit deposits]
  (println deposit))
```

## Query deposit logs

Logs are pretty important to understand the life cycle of a deposit.

```clojure
(def logs (starkbank.deposit.log/query
  {
    :limit 10
  }))

(doseq [log logs]
  (println log))
```

## Get a deposit log

You can get a single log by its id.

```clojure
(def log (starkbank.deposit.log/get "6532638269505536"))

(println log)
```

## Create boletos

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
  :due "2020-05-20"
  :fine 5; 5%
  :interest 2.5; 2.5% per month
}]))

(println boletos)
```

## Get a boleto

After its creation, information on a boleto may be retrieved by passing its id.
Its status indicates whether it's been paid.

```clojure
(def boleto (starkbank.boleto/get "6750458353811456"))

(println boleto)
```

## Get a boleto PDF

After its creation, a boleto PDF may be retrieved by passing its id.

```clojure
(clojure.java.io/copy
  (starkbank.boleto/pdf "6750458353811456")
  (clojure.java.io/file "boleto.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

## Delete a boleto

You can also cancel a boleto by its id.
Note that this is not possible if it has been processed already.

```clojure
(def boleto (starkbank.boleto/delete "5202697619767296"))

(println boleto)
```

## Query boletos

You can get a stream of created boletos given some filters.

```clojure
(def boletos
  (starkbank.boleto/query
    {
      :after "2020-09-05"
      :before "2020-10-02"
      :limit 10
    }))

(println boletos)
```

## Query boleto logs

Logs are pretty important to understand the life cycle of a boleto.

```clojure
(def logs
  (starkbank.boleto.log/query
    {
      :boleto-ids ["6750458353811456"]
    }))

(println logs)
```

## Get a boleto log

You can get a single log by its id.

```clojure
(def log (starkbank.boleto.log/get "6288576484474880"))

(println log)
```

## Investigate a boleto

You can discover if a StarkBank boleto has been recently paid before we receive the response on the next day. This can be done by creating a BoletoHolmes object, which fetches the updated status of the corresponding Boleto object according to CIP to check, for example, whether it is still payable or not. The investigation happens asynchronously and the most common way to retrieve the results is to register a "boleto-holmes" webhook subscription, although polling is also possible. 

```clojure
(def holmes (starkbank.boleto-holmes/create
  [{
    :boleto-id "5656565656565656"
    :tags ["investigating product x"]
  }]))

(doseq [sherlock holmes]
  (println sherlock))
```

## Get a boleto holmes

To get a single boleto holmes by its id, run:

```clojure
(def sherlock (starkbank.boleto-holmes/get "5656565656565656"))

(println sherlock)
```

## Query boleto holmes

You can search for boleto holmes using filters. 

```clojure
(def holmes (starkbank.boleto-holmes/query {
  :limit 10
  :after "2020-11-01"
  :before "2020-12-01"
}))

(doseq [sherlock holmes]
  (println sherlock))
```

## Query boleto holmes logs

Searches are also possible with boleto holmes logs:

```clojure
(def logs (starkbank.boleto-holmes.log/query
  {
    :limit 10
    :type "solved"
  }))

(doseq [log logs]
  (println log))
```

## Get a boleto holmes log

You can also get a boleto holmes log by specifying its id.

```clojure
(def log (starkbank.boleto-holmes.log/get "5656565656565656"))

(println log)
```

## Pay a BR Code

Paying a BR Code is also simple.

```clojure
(def payments (starkbank.brcode-payment/create
  [{
    :brcode "00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A"
    :tax-id "20.018.183/0001-80"
    :description "Tony Stark's Suit"
    :amount 123456
    :scheduled "2020-12-20T19:32:35.418698+00:00"
    :tags ["Stark" "Suit"]
  }]))

(doseq [payment payments]
  (println payment))
```

## Query BR Code payments

You can search for brcode payments using filters. 

```clojure
(def payments (starkbank.brcode-payment/query
  {
    :limit 10
  }))

(doseq [payment payments]
  (println payment))
```

## Get a BR Code payment

To get a single BR Code payment by its id, run:

```clojure
(def payment (starkbank.brcode-payment/get "6532638269505536"))

(println payment)
```

## Cancel a BR Code payment

You can cancel a BR Code payment by changing its status to "canceled".
Note that this is not possible if it has been processed already.

```clojure
(def payment (starkbank.brcode-payment/update "6532638269505536"
  {
    :status "canceled"
  }))

(println payment)
```

## Get a BR Code payment PDF

After its creation, a boleto payment PDF may be retrieved by its id. 

```clojure
(clojure.java.io/copy
  (starkbank.brcode-payment/pdf "6750458353811456")
  (clojure.java.io/file "payment.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

## Query BR Code payment logs

Searches are also possible with BR Code payment logs:

```clojure
(def logs (starkbank.brcode-payment.log/query
  {
    :limit 10
  }))

(doseq [log logs]
  (println log))
```

## Get a BR Code payment log

You can also get a BR Code payment log by specifying its id.

```clojure
(def log (starkbank.brcode-payment.log/get "6532638269505536"))

(println log)
```

## Pay a boleto

Paying a boleto is also simple.

```clojure
(def payments
  (starkbank.boleto-payment/create
    [
      {
        :line "34191.09008 64694.017308 71444.640008 1 96610000014500"
        :tax-id "012.345.678-90"
        :scheduled "2020-05-07"
        :description "take my money"
        :tags ["take" "my" "money"]
      }
      {
        :bar-code "34191972300000289001090064694197307144464000"
        :tax-id "012.345.678-90"
        :scheduled "2020-06-10"
        :description "take my money one more time"
        :tags ["again"]
      }
    ]))

(println payments)
```

## Get a boleto payment

To get a single boleto payment by its id, run:

```clojure
(def payment (starkbank.boleto-payment/get "5629412477239296"))

(println payment)
```

## Get a boleto payment PDF

After its creation, a boleto payment PDF may be retrieved by passing its id.

```clojure
(clojure.java.io/copy
  (starkbank.boleto-payment/pdf "6497770399596544")
  (clojure.java.io/file "boleto-payment.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

## Delete a boleto payment

You can also cancel a boleto payment by its id.
Note that this is not possible if it has been processed already.

```clojure
(def payment (starkbank.boleto-payment/delete "5629412477239296"))

(println payment)
```

## Query boleto payments

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

## Query boleto payment logs

Searches are also possible with boleto payment logs:

```clojure
(def logs
  (starkbank.boleto-payment.log/query
    {
      :payment-ids ["5629412477239296" "5199478290120704"]
    }))

(println logs)
```

## Get a boleto payment log

You can also get a boleto payment log by specifying its id.

```clojure
(def log (starkbank.boleto-payment.log/get "5391671273455616"))

(println log)
```

## Create utility payments

It's also simple to pay utility bills (such as electricity and water bills) in the SDK.

```clojure
(def payments
  (starkbank.utility-payment/create
    [
      {
        :bar-code "83600000001522801380037107172881100021296561"
        :scheduled "2020-04-11"
        :description "paying some bills"
        :tags ["take" "my" "money"]
      }
      {
        :line "83680000001 7 08430138003 0 71070987611 8 00041351685 7"
        :scheduled "2020-05-11"
        :description "never ending bills"
        :tags ["again"]
      }
    ]))

(println payments)
```

## Query utility payments

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

## Get a utility payment

You can get a specific bill by its id:

```clojure
(def payment (starkbank.utility-payment/get "6619425641857024"))

(println payment)
```

## Get a utility payment PDF

After its creation, a utility payment PDF may also be retrieved by passing its id.

```clojure
(clojure.java.io/copy
  (starkbank.utility-payment/pdf "4663663059271680")
  (clojure.java.io/file "utility-payment.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

## Delete a utility payment

You can also cancel a utility payment by its id.
Note that this is not possible if it has been processed already.

```clojure
(def payment (starkbank.utility-payment/delete "6619425641857024"))

(println payment)
```

## Query utility payment logs

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

## Get a utility payment log

If you want to get a specific payment log by its id, just run:

```clojure
(def log (starkbank.utility-payment.log/get "6197807794880512"))

(println log)
```

## Create tax payments

It is also simple to pay taxes (such as ISS and DAS) using this SDK.

```clojure
(def payments
  (starkbank.tax-payment/create
    [
      {
        :bar-code "85660000001549403280074119002551100010601813"
        :scheduled "2021-07-13"
        :description "paying some bills"
        :tags ["take" "my" "money"]
      }
      {
        :line "85800000003 0 28960328203 1 56072020190 5 22109674804 0"
        :scheduled "2021-07-13"
        :description "build the hospital, hopefully"
        :tags ["expensive"]
      }
    ]))

(println payments)
```

## Query tax payments

To search for tax payments using filters, run:

```clojure
(def payments
  (starkbank.tax-payment/query
    { :limit 10 }))

(println payments)
```

## Get tax payment

You can get a specific tax payment by its id:

```clojure
(def payment (starkbank.tax-payment/get "5155165527080960"))

(println payment)
```

## Get tax payment PDF

After its creation, a tax payment PDF may also be retrieved by its id.

```php
(clojure.java.io/copy
  (starkbank.tax-payment/pdf "5155165527080960")
  (clojure.java.io/file "tax-payment.pdf"))
```

Be careful not to accidentally enforce any encoding on the raw pdf content,
as it may yield abnormal results in the final file, such as missing images
and strange characters.

## Delete tax payment

You can also cancel a tax payment by its id.
Note that this is not possible if it has been processed already.

```clojure
(def payment (starkbank.tax-payment/delete "5155165527080960"))

(println payment)
```

## Query tax payment logs

You can search for payment logs by specifying filters. Use this to understand each payment life cycle.

```clojure
(def logs
  (starkbank.tax-payment.log/query
    { :limit 10 }))

(println logs)
```

## Get tax payment log

If you want to get a specific payment log by its id, just run:

```clojure
(def log (starkbank.tax-payment.log/get "1902837198237992"))

(println log)
```

**Note**: Some taxes can't be payed with bar codes. Since they have specific parameters, each one of them has its own
resource and routes, which are all analogous to the TaxPayment resource. The ones we currently support are:
- darf-payment, for DARFs

## Preview payment information before executing the payment

You can preview multiple types of payment to confirm any information before actually paying.
If the "scheduled" parameter is not informed, today will be assumed as the intended payment date.
Right now, the "scheduled" parameter only has effect on BrcodePreviews.
This resource is able to preview the following types of payment:
"brcode-payment", "boleto-payment", "utility-payment" and "tax-payment"

```clojure
(def previews
  [
    {:id "00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A" :scheduled "2021-08-10" }
    {:id "34191.09008 61207.727308 71444.640008 5 81310001234321" }
  ])

(def payment-previews (payment-preview/create previews))

(doseq [preview payment-previews]
  (println preview))
```

## Create payment requests to be approved by authorized people in a cost center 

You can also request payments that must pass through a specific cost center approval flow to be executed. In certain structures, this allows double checks for cash-outs and also gives time to load your account with the required amount before the payments take place. The approvals can be granted at our website and must be performed according to the rules specified in the cost center.

**Note**: The value of the center-id parameter can be consulted by logging into our website and going
to the desired cost center page.

```clojure
(def payment {
      :amount 200
      :name "Dumlocks von z'Blurbows"
      :tax-id "012.345.678-90"
      :bank-code "60701190"
      :branch-code "0001"
      :account-number "00000-0"
      :tags ["testing" "clojure"]
    })

(def requests (starkbank.payment-request/create
  [{
    :type type
    :payment payment
    :center-id "5656565656565656"
    :tags ["testing" "clojure"]
    :due "2020-12-11"
  }]))

(doseq [request requests]
  (println request))
```

## Query payment requests

To search for payment requests, run:

```clojure
(def requests (starkbank.payment-request/query {
  :limit 3
  :status ["success"]
  :center-id "5656565656565656")})

(doseq [request requests]
  (println request))
```

## Create a webhook subscription

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

## Query webhook subscriptions

To search for registered webhooks, run:

```clojure
(def webhooks (starkbank.webhook/query))

(println webhooks)
```

## Get a webhook subscription

You can get a specific webhook by its id.

```clojure
(def webhook (starkbank.webhook/get "6178044066660352"))

(println webhook)
```

## Delete a webhook subscription

You can also delete a specific webhook by its id.

```clojure
(def webhook (starkbank.webhook/delete "5083659757420544"))

(println webhook)
```

## Process webhook events

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

## Query webhook events

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

## Get a webhook event

You can get a specific webhook event by its id.

```clojure
(def event (starkbank.event/get "6597859067559936"))

(println event)
```

## Delete a webhook event

You can also delete a specific webhook event by its id.

```clojure
(def event (starkbank.event/delete "4568139664719872"))

(println event)
```

## Query failed webhook event delivery attempts information

You can also get information on failed webhook event delivery attempts.

```clojure
(def attempts (starkbank.event.attempt/query {:after ["2020-03-20"]}))

(printn attempts)
```

## Get a failed webhook event delivery attempt information

To retrieve information on a single attempt, use the following function:

```clojure
(def attempt (starkbank.event.attempt/get "1616161616161616"))

(print attempt)
```

## Set webhook events as delivered

This can be used in case you've lost events.
With this function, you can manually set events retrieved from the API as
"delivered" to help future event queries with `is-delivered: false`.

```clojure
(def event (starkbank.event/update "5764442407043072"
  {
    :is-delivered true
  }))

(println event)
```

## Create a new Workspace

The Organization user allows you to create new Workspaces (bank accounts) under your organization.
Workspaces have independent balances, statements, operations and users.
The only link between your Workspaces is the Organization that controls them.

**Note**: This route will only work if the Organization user is used with `:workspace-id nil`.

```clojure
(def workspace
  (starkbank.workspace/create
    {
      :username "iron-bank-workspace-1"
      :name "Iron Bank Workspace 1"
    }))

(println workspace)
```

## List your Workspaces

This route lists Workspaces. If no parameter is passed, all the workspaces the user has access to will be listed, but
you can also find other Workspaces by searching for their usernames or IDs directly.

```clojure
(def workspaces (starkbank.workspace/query))

(println workspaces)
```

## Get a Workspace

You can get a specific Workspace by its id.

```clojure
(def workspace (starkbank.workspace/get "6178044066660352"))

(println workspace)
```

## Update a Workspace

You can update a specific Workspace by its id.

```clojure
(def workspace (starkbank.workspace/update
  "6178044066660352"
  {
    :username "new-username-test"
    :name "Updated workspace test"
    :allowed-tax-ids ["359.536.680-82", "20.018.183/0001-80"]
  }))
(print workspace)
```

# Handling errors

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

# Help and Feedback

If you have any questions about our SDK, just send us an email.
We will respond you quickly, pinky promise. We are here to help you integrate with us ASAP.
We also love feedback, so don't be shy about sharing your thoughts with us.

Email: developers@starkbank.com
