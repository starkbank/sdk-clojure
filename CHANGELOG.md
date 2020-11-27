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
- Invoice resource to load your account with dynamic QR Codes
- DictKey resource to get DICT (PIX) key parameters
- Deposit resource to receive transfers passively
- PIX support in Transfer resource
- BrcodePayment and BrcodePreview resources to pay static and dynamic PIX QR Codes
- BoletoHolmes to investigate boleto status according to CIP
- ids parameter to Transaction.query
- ids parameter to Transfer.query
- hiddenFields parameter to Boleto.pdf
- ourNumber attribute to Boleto
- PaymentRequest resource to pass payments through manual approval flow
- Boleto.Discount & Boleto.Description parsing on Boleto API responses
- Travis CI configuration
- Transfer.scheduled parameter to allow Transfer scheduling
- StarkBank.Transfer.delete to cancel scheduled Transfers
- Transaction query by tags
- Transfer query taxId parameter
- Global error language setting
- Boleto PDF layout option
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
