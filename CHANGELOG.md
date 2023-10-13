# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to the following versioning pattern:

Given a version number MAJOR.MINOR.PATCH, increment:

- MAJOR version when the **API** version is incremented. This may include backwards incompatible changes;
- MINOR version when **breaking changes** are introduced OR **new functionalities** are added in a backwards compatible manner;
- PATCH version when backwards compatible bug **fixes** are implemented.


## [Unreleased]
### Added
- update function to Deposit resource

## [2.5.2] - 2021-11-10
### Changed
- java sdk version to 2.8.2

## [2.5.1] - 2021-11-04
### Changed
- java sdk version to 2.8.1

## [2.5.0] - 2021-08-20
### Added
- "payment" account type for Pix related resources
- missing parameters to boleto, brcode-payment, dict-key, event, transfer and workspace resources
- workspace/update to allow parameter updates
- event.attempt sub-resource to allow retrieval of information on failed webhook event delivery attempts
- pdf method for retrieving PDF receipts from reversed invoice logs
- page functions as a manual-pagination alternative to queries
- institution resource to allow query of institutions recognized by the Brazilian Central Bank for Pix and TED transactions
- tax-payment resource
- darf-payment resource
- support for scheduled invoices, which will display discounts, fine, interest, etc. on the users banking interface when dates are used instead of datetimes
- payment-preview resource to preview multiple types of payments before confirmation: brcode-preview, boleto-preview, utility-preview and tax-preview

## [2.4.0] - 2021-03-04
### Added
- invoice.transaction-ids property to allow invoice ledger transaction look-up
- invoice.link property to allow easy access to invoice webpage

## [2.3.1] - 2021-03-04
### Fixed
- bug when no expiration is passed to invoice resource

## [2.3.0] - 2021-02-08
### Added
- invoice.payment sub-resource to allow retrieval of invoice payment information

## [2.2.0] - 2021-01-25
### Added
- organization user
- workspace resource
- transfer.account-type property to allow "checking", "salary" or "savings" account specification
- transfer.external-id property to allow users to take control over duplication filters
### Fixed
- missing brcode-payment in payment-request

## [2.1.0] - 2020-12-14
### Change
- starkbank.user/set-default-user to starkbank.settings/user

## [2.0.0] - 2020-12-01
### Added
- invoice resource to load your account with dynamic QR Codes
- dict-key resource to get DICT (PIX) key parameters
- deposit resource to receive transfers passively
- brcode-payment and BrcodePreview resources to pay static and dynamic PIX QR Codes
- PIX support in transfer resource
- boleto-holmes to investigate boleto status according to CIP
- our-number attribute to Boleto
- payment-request resource to pass payments through manual approval flow
- transfer.scheduled parameter to allow transfer scheduling
- transfer/delete to cancel scheduled transfers
- transaction query by ids
- transaction query by tags
- transfer query by ids
- transfer query by tax-id
- boleto/pdf layout and hidden-fields options
- Global error language setting
- Travis CI configuration
### Change
- starkbank.user/set to starkbank.settings/set-default-user

## [0.2.0] - 2020-05-12
### Added
- "receiver-name" & "receiver-tax-id" properties to Boleto entities

## [0.1.4] - 2020-05-09
### Changed
- SDK Java repo to com.starkbank/sdk

## [0.1.3] - 2020-05-07
### Fixed
- boleto amount type

## [0.1.2] - 2020-05-07
### Changed
- Local Java SDK JAR to remote (Maven Central)

## [0.1.1] - 2020-05-07
### Fixed
- Warnings

## [0.1.0] - 2020-05-07
### Added
- Full Stark Bank API v2 compatibility
