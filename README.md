# TransactID Library Java Edition

![Netki Logo](images/netki.png)


[![Maven Central](https://img.shields.io/maven-central/v/com.netki/transactid)](https://mvnrepository.com/artifact/com.netki/transactid)

![Build Status](https://teamcity.myverify.io/app/rest/builds/buildType\(id:TransactIdLibraryJava_VerifyPrMaster\)/statusIcon)

This Netki TransactID Library provides convenient and simple implementation of the [BIP70][1] and [BIP75][2] protocol definitions.

We have packaged all the necessary functionality needed to deploy a protocol compliant version of the BIP.  This library will take care of the difficult cryptographic communication and implementation parts of implementing the above protocols.

What we provide:

* Networking layers
* [Protocol Buffers][4] wiring and definitions
* Key signing with [EC cryptography][5]


## Travel Rule

International regulatory bodies have determined that the cryptocurrency and blockchain finance sectors are beholden to the same rules as the traditional finance institutions.  We have provided this library to help with the implementation of the ["Travel Rule" 31 CFR 103.33(g) ][6] clause of the [Bank Secrecy Act][7].

## TransactID Travel Rule Features

We have taken a security and privacy first approach to the development of this library.  Your customers' data and secrets are very important to us.

We are also blockchain people at heart.  We value the philosophies and strategies of the decentralized nature of the blockchain and will NOT lock you into a vendor specific protocol. Which is why we relay on standardized formats.

* Blockchain and coin agnostic - any coin, any chain, any token
* Identity safety - the identity of the parties is only shared between the parties and not a centralized outside source
* PII retention - the PII of your customers will never be written to the blockchain
* PII control - the protocol and library give you the ability to control how much data is shared
* PII security - we employ the highest level of cryptographic and architectural security


## Basics of BIP75

Implementation that might otherwise be complicated is taken care of by the library itself.  It is, however, important to understand some of the basic algorithms and workflow dynamics that are going on under the hood.


### Terminology

We have standardized on the terminology referenced in both [BIP70][1] and [BIP75][2].  It is beneficial to understand these terms so that they may be referenced below.

Normally the communication will happen between two parties.  Two VASPs or parties are represented in the models.  It can be thought of as a buyer/seller arragement but does not necessarily have to be.

* InvoiceRequest - VASP A (payer) will start the transaction
* PaymentRequest - VASP B (receiver) resposne asking for payment of a defined value
* Payment - this model represents the actual transaction that goes onto the blockchain
* PaymentACK - the response back from VASP B letting A know that the payment was acknowledged

### Workflow

A graph of the workflow is probably the best way to visualize what is going on.

![Transact ID Workflow](images/workflow_transactid.png)


## Quick Start

Installation of the Netki TransactID Travel Rule Library is straight forward. We support `Maven` and `gradle`


### Maven Users

If you are using maven you can download the latest JAR or grab via [Maven][3]:

```xml
<dependency>
    <groupId>com.netki</groupId>
    <artifactId>transactid</artifactId>
    <version>0.1.0-alpha0</version>
    <type>pom</type>
</dependency>
```

### Gradle Users

```sh
compile group: 'com.netki', name: 'transactid', version: '0.1.0-alpha0', ext: 'pom'
```

## General Usage

To use the methods to create the BIP messages you can use the static methods in the class TransactId for example:

```kotlin
val transactIdInstance = TransactId
transactIdInstance.isInvoiceRequestValid(invoiceRequestBinary)
```

Alternatively:

```kotlin
TransactId.isInvoiceRequestValid(invoiceRequestBinary)
```

There are three main method types that you'll use: create\*, is\*Valid, and parse\*.

## Initializing the library

Note: The initialization of the library is only needed if you want to also be able to fetch the detailed information about the addresses that are shared across the different messages.

To be able to use any method that also fetches the detailed information, you need to initialize this library with the following method:

```kotlin
/**
 * Method to initialize the library with the ability to fetch detailed information of the addresses.
 * You need to initialize it only if address detailed info is required.
 *
 * @param authorizationKey to fetch the required data.
 */
fun init(authorizationKey: String) {
    this.authorizationKey = authorizationKey
}
```

To obtain the required key please ask to it to your Netki contact.
 
## Invoice Request

Please refer to the [BIP75][2] documentation for detailed requirements for a `InvoiceRequest`.

Create an object for sending like so:

```kotlin
/**
 * Create InvoiceRequest message.
 *
 * @param invoiceRequestParameters data to create the InvoiceRequest.
 * @param ownersParameters of the accounts for this transaction.
 * @param senderParameters of the protocol message.
 * @param attestationsRequested list of attestations requested.
 * @return binary object of the message created.
 * @throws InvalidOwnersException if the provided list of owners is not valid.
 */
@Throws(InvalidOwnersException::class)
fun createInvoiceRequest(
    invoiceRequestParameters: InvoiceRequestParameters,
    ownersParameters: List<OwnerParameters>,
    senderParameters: SenderParameters,
    attestationsRequested: List<Attestation>
): ByteArray
```

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate
the signature and parse one:

```kotlin
/**
 * Validate if a binary InvoiceRequest is valid.
 *
 * @param invoiceRequestBinary binary data to validate.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception InvalidSignatureException if the signature in the binary is not valid.
 * @exception InvalidCertificateException if there is a problem with the certificates.
 * @exception InvalidCertificateChainException if the certificate chain is not valid.
 */
@Throws(
    InvalidObjectException::class,
    InvalidSignatureException::class,
    InvalidCertificateException::class,
    InvalidCertificateChainException::class
)
fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray): Boolean
```


This will return true if there are no errors in the object and the signature is valid.

To access the data from the `InvoiceRequest` just do:

```kotlin
/**
 * Parse binary InvoiceRequest.
 *
 * @param invoiceRequestBinary binary data with the message to parse.
 * @return InvoiceRequest parsed.
 * @exception InvalidObjectException if the binary is malformed.
 */
@Throws(InvalidObjectException::class)
fun parseInvoiceRequest(invoiceRequestBinary: ByteArray): InvoiceRequest
```

And that will return a object with all of the fields of the `InvoiceRequest` and the values that were filled in.

To access the data from the InvoiceRequest including the detailed information of the addresses included in the message (you require to initialize the library), just do:

```kotlin
/**
 * Parse binary InvoiceRequest and also get the detailed information of the addresses.
 *
 * @param invoiceRequestBinary binary data with the message to parse.
 * @return InvoiceRequest parsed with the detailed information for each address.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception AddressProviderErrorException if there is an error fetching the information from the provider.
 * @exception AddressProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
 */
@Throws(
    InvalidObjectException::class,
    AddressProviderErrorException::class,
    AddressProviderUnauthorizedException::class
)
fun parseInvoiceRequestWithAddressesInfo(
    invoiceRequestBinary: ByteArray
): InvoiceRequest 
```

And that will return a object with all of the fields of the InvoiceRequest and the values that were filled in.

## Payment Request

Please refer to the [BIP70][1] documentation for detailed requirements for a `PaymentRequest`.

Create an object for sending like so:

```kotlin
/**
 * Create binary PaymentRequest.
 *
 * @param paymentRequestParameters data to create the PaymentRequest.
 * @param ownersParameters of the accounts for this transaction.
 * @param senderParameters of the protocol message.
 * @param attestationsRequested list of attestations requested.
 * @param paymentParametersVersion version of the PaymentDetails message.
 * @return binary object of the message created.
 * @throws InvalidOwnersException if the provided list of owners is not valid.

 */
@Throws(InvalidOwnersException::class)
fun createPaymentRequest(
    paymentRequestParameters: PaymentRequestParameters,
    ownersParameters: List<OwnerParameters>,
    senderParameters: SenderParameters,
    attestationsRequested: List<Attestation>,
    paymentParametersVersion: Int = 1
): ByteArray
```

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate
the signature and parse one:

```kotlin
/**
 * Validate if a binary PaymentRequest is valid.
 *
 * @param paymentRequestBinary binary data to validate.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception InvalidSignatureException if the signature in the binary is not valid.
 * @exception InvalidCertificateException if there is a problem with the certificates.
 * @exception InvalidCertificateChainException if the certificate chain is not valid.
 */
@Throws(
    InvalidObjectException::class,
    InvalidSignatureException::class,
    InvalidCertificateException::class,
    InvalidCertificateChainException::class
)
fun isPaymentRequestValid(paymentRequestBinary: ByteArray): Boolean
```

This will return true if there are no errors in the object and the signature is valid.

To access the data from the PaymentRequest just do:

```kotlin
/**
 * Parse binary PaymentRequest.
 *
 * @param paymentRequestBinary binary data with the message to parse.
 * @return PaymentRequest parsed.
 * @exception InvalidObjectException if the binary is malformed.
 */
@Throws(InvalidObjectException::class)
fun parsePaymentRequest(paymentRequestBinary: ByteArray): PaymentRequest
```

And that will return an object with all of the fields of the PaymentRequest and the values that were
filled in.

To access the data from the PaymentRequest including the detailed information of the addresses included in the message (you require to initialize the library), just do:

```kotlin
/**
 * Parse binary PaymentRequest and also get the detailed information of the addresses.
 *
 * @param paymentRequestBinary binary data with the message to parse.
 * @return PaymentRequest parsed with the detailed information for each address.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception AddressProviderErrorException if there is an error fetching the information from the provider.
 * @exception AddressProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
 */
@Throws(
    InvalidObjectException::class,
    AddressProviderErrorException::class,
    AddressProviderUnauthorizedException::class
)
fun parsePaymentRequestWithAddressesInfo(paymentRequestBinary: ByteArray): PaymentRequest
```

And that will return a object with all of the fields of the PaymentRequest and the values that were filled in.

## Payment

Please refer to the [BIP70][1] documentation for detailed requirements for a Payment.

Create an object for sending like so:

```kotlin
/**
 * Create binary Payment.
 *
 * @param paymentParameters data to create the Payment.
 * @param ownersParameters of the accounts for this transaction.
 * @return binary object of the message created.
 * @throws InvalidOwnersException if the provided list of owners is not valid.
 */
@Throws(InvalidOwnersException::class)
fun createPayment(
    paymentParameters: PaymentParameters,
    ownersParameters: List<OwnerParameters>
): ByteArray
```

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.
Please note that Payments aren't signed unlike the Invoice/PaymentRequest:


```kotlin
/**
 * Validate if a binary Payment is valid.
 *
 * @param paymentBinary binary data to validate.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception InvalidSignatureException if the signature in the binary is not valid.
 * @exception InvalidCertificateException if there is a problem with the certificates.
 * @exception InvalidCertificateChainException if the certificate chain is not valid.
 */
@Throws(
    InvalidObjectException::class,
    InvalidSignatureException::class,
    InvalidCertificateException::class,
    InvalidCertificateChainException::class
)
fun isPaymentValid(paymentBinary: ByteArray): Boolean
```

If this return true then we were able to parse the protobuf object.

To access the data from the Payment just do:

```kotlin
/**
 * Parse binary Payment.
 *
 * @param paymentBinary binary data with the message to parse.
 * @return Payment parsed.
 * @exception InvalidObjectException if the binary is malformed.
 */
@Throws(InvalidObjectException::class)
fun parsePayment(paymentBinary: ByteArray): Payment
```

And that will return an object with all of the fields of the Payment and the values that were
filled in.

## PaymentACK

Please refer to the [BIP70][1] documentation for detailed requirements for a PaymentACK. PaymentACKs are a bit different
than other things in the library.  Due to the fact that part of the PaymentACK is the Payment object you are
acknowledging, it's not possible to create an ACK without first verifying a Payment.

Create an object for sending like so:

```kotlin
/**
 * Create binary PaymentAck.
 *
 * @param payment data to create the Payment.
 * @param memo note that should be displayed to the customer.
 * @return binary object of the message created.
 */
fun createPaymentAck(payment: Payment, memo: String): ByteArray
```

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.  
Please note that PaymentsAck aren't signed unlike the Invoice/PaymentRequest:


```kotlin
/**
 * Validate if a binary PaymentAck is valid.
 *
 * @param paymentAckBinary binary data to validate.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 */
@Throws(InvalidObjectException::class)
fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean
```

If this return true then we were able to parse the protobuf object.

To access the data from the PaymentAck just do:

```kotlin
/**
 * Parse binary PaymentAck.
 *
 * @param paymentAckBinary binary data with the message to parse.
 * @return PaymentAck parsed.
 * @exception InvalidObjectException if the binary is malformed.
 */
@Throws(InvalidObjectException::class)
fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck
```

And that will return an object with all of the fields of the PaymentACK and the values that were filled in.

# Key Management system

This library contains a module to create and administrate KeyPairs and certificates.

## General Usage

To use the key management integration you can use the static methods in the class TidKms, for example:

```kotlin
val tidKmsInstance = TidKms
tidKmsInstance.storeCertificatePem("certificate")
```

Alternatively:

```kotlin
TidKms.storeCertificatePem("certificate")
```

The first step to use this module is initialize it, to do that call the method:


```kotlin
    /**
     * Method to initialize the key management system.
     * All the parameters are optional depending the functions that want to be used.
     * Make sure to call this method before any other one in this class.
     *
     * @param authorizationCertificateProviderKey to connect to the certificate provider.
     * @param authorizationSecureStorageKey to connect to the secure storage.
     * @param addressSecureStorage to connect to the secure storage.
     */
    @JvmOverloads
    fun init(
        authorizationCertificateProviderKey: String = "",
        authorizationSecureStorageKey: String = "",
        addressSecureStorage: String = ""
    ) {
        this.authorizationCertificateProviderKey = authorizationCertificateProviderKey
        this.authorizationSecureStorageKey = authorizationSecureStorageKey
        this.addressSecureStorage = addressSecureStorage
    }
```

To get access to the authorizationCertificateProviderKey please get in touch with your Netki contact.

If you want to use the Certificate generation functions make sure to pass a valid authorizationCertificateProviderKey. 
If you want to use the Storage make sure to pass a valid authorizationSecureStorageKey and addressSecureStorage.
 
## Certificate generation

You can generate certificates and private keys corresponding to different attestations with this library, to do that you can use the following method:

```kotlin
    /**
     * Generate a certificate for each one of the attestations provided.
     *
     * @param attestationsInformation list of attestations with their corresponding data.
     * @return list of certificate per attestation.
     * @throws CertificateProviderException if there is an error creating the certificates.
     * @throws CertificateProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
     */
    @Throws(CertificateProviderException::class, CertificateProviderUnauthorizedException::class)
    fun generateCertificates(attestationsInformation: List<AttestationInformation>): List<AttestationCertificate>
```

## Vault for Key Storage
This library includes integration with Hashicorp Vault for key storage using the key/value secrets engine and can be launched as a Docker container.
See Hashicorp's Vault Docker documentation here: https://hub.docker.com/_/vault

This library stores keys and certs in specific locations. Please ensure that once Vault is set up, you enable these paths:

```sh
vault secrets enable -path=keys/ kv
vault secrets enable -path=certs/ kv
```


## Storing certificates and keys

To store certificates the library has the following methods that in general can store certificate and keys in PEM formats or in java objects formats and then return a unique Id that will be used to fetch the values in the future.

The methods available are:

Store a X509Certificate in PEM format.

```kotlin
/**
 * Store a X509Certificate in PEM format.
 *
 * @param certificatePem certificate in PEM format to store.
 * @return id to fetch the certificate in the future.
 * @throws InvalidCertificateException if the provided certificate is not a valid X509Certificate.
 * @throws KeyManagementStoreException if there is an error while storing the certificate.
 */
@Throws(
    InvalidCertificateException::class,
    KeyManagementStoreException::class
)
fun storeCertificatePem(certificatePem: String): String = keyManagement.storeCertificatePem(certificatePem)
```

Store a X509Certificate java object.

```kotlin
/**
 * Store a X509Certificate java object.
 *
 * @param certificate X509Certificate to store.
 * @return id to fetch the certificate in the future.
 * @throws InvalidCertificateException if the provided certificate is not a valid X509Certificate.
 * @throws KeyManagementStoreException if there is an error while storing the certificate.
 */
@Throws(
    InvalidCertificateException::class,
    KeyManagementStoreException::class
)
fun storeCertificate(certificate: X509Certificate): String = keyManagement.storeCertificate(certificate)
```

Store a Private key in PEM format.

```kotlin
/**
 * Store a private key in PEM format.
 *
 * @param privateKeyPem private key in PEM format to store.
 * @return id to fetch the private key in the future.
 * @throws InvalidPrivateKeyException if the provided key is not a valid private key.
 * @throws KeyManagementStoreException if there is an error while storing the private key.
 */
@Throws(
    InvalidPrivateKeyException::class,
    KeyManagementStoreException::class
)
fun storePrivateKeyPem(privateKeyPem: String): String = keyManagement.storePrivateKeyPem(privateKeyPem)
```

Store a Private key java object.      

```kotlin
/**
 * Store a PrivateKey java object.
 *
 * @param privateKey PrivateKey to store.
 * @return PrivateKey object.
 * @throws InvalidPrivateKeyException if the provided key is not a valid PrivateKey.
 * @throws KeyManagementStoreException if there is an error while storing the PrivateKey.
 */
@Throws(
    InvalidPrivateKeyException::class,
    KeyManagementStoreException::class
)
fun storePrivateKey(privateKey: PrivateKey): String = keyManagement.storePrivateKey(privateKey)
```


## Fetching certificates and keys

The library has methods to fetch the certificate and keys using the unique id to identify the required object. It can return the values in PEM format or as a java object.

The methods available are:

Fetch a X509Certificate in PEM format.

```kotlin
/**
 * Fetch a X509Certificate in PEM format.
 *
 * @param certificateId id of the certificate.
 * @return X509Certificate in PEM format.
 * @throws ObjectNotFoundException if there is not a certificate associated to the provided certificateId.
 * @throws InvalidCertificateException if the fetched object is not a valid X509Certificate.
 * @throws KeyManagementFetchException if there is an error while fetching the certificate.
 */
@Throws(
    ObjectNotFoundException::class,
    InvalidCertificateException::class,
    KeyManagementFetchException::class
)
fun fetchCertificatePem(certificateId: String): String = keyManagement.fetchCertificatePem(certificateId)
```

Fetch a X509Certificate java object.

```kotlin
/**
 * Fetch a X509Certificate java object.
 *
 * @param certificateId id of the certificate.
 * @return X509Certificate object.
 * @throws ObjectNotFoundException if there is not a certificate associated to the provided certificateId.
 * @throws InvalidCertificateException if the fetched object is not a valid X509Certificate.
 * @throws KeyManagementFetchException if there is an error while fetching the certificate.
 */
@Throws(
    ObjectNotFoundException::class,
    InvalidCertificateException::class,
    KeyManagementFetchException::class
)
fun fetchCertificate(certificateId: String): X509Certificate = keyManagement.fetchCertificate(certificateId)
```

Fetch a Private key in PEM format.

```kotlin
/**
 * Fetch a private key in PEM format.
 *
 * @param privateKeyId id of the private key.
 * @return private key in PEM format.
 * @throws ObjectNotFoundException if there is not a private key associated to the provided privateKeyId.
 * @throws InvalidPrivateKeyException if the fetched object is not a valid PrivateKey.
 * @throws KeyManagementFetchException if there is an error while fetching the private key.
 */
@Throws(
    ObjectNotFoundException::class,
    InvalidPrivateKeyException::class,
    KeyManagementFetchException::class
)
fun fetchPrivateKeyPem(privateKeyId: String): String = keyManagement.fetchPrivateKeyPem(privateKeyId)
```

Fetch a Private key java object.

```kotlin
/**
 * Fetch a PrivateKey java object.
 *
 * @param privateKeyId id of the PrivateKey.
 * @return PrivateKey object.
 * @throws ObjectNotFoundException if there is not a PrivateKey associated to the provided privateKeyId.
 * @throws InvalidPrivateKeyException if the fetched object is not a valid PrivateKey.
 * @throws KeyManagementFetchException if there is an error while fetching the private key.
 */
@Throws(
    ObjectNotFoundException::class,
    InvalidPrivateKeyException::class,
    KeyManagementFetchException::class
)
fun fetchPrivateKey(privateKeyId: String): PrivateKey = keyManagement.fetchPrivateKey(privateKeyId)
```

# Address information provider

This library contains a module to consult the detailed information of a given address.

## General Usage

The first step to utilize this module is to initialize it, to obtain the required key please ask to it to your Netki contact.

```kotlin
/**
 * Method to initialize the address info provider.
 * Make sure to call this method before any other one in this class.
 */
fun init(authorizationKey: String) {
    this.authorizationKey = authorizationKey
}
```

After the module is initialized, you can fetch the detailed information of an address with the following method:        

```kotlin
/**
 * Fetch the information of a given address.
 *
 * @param currency of the address.
 * @param address to fetch the information.
 * @throws AddressProviderErrorException if there is an error fetching the information from the provider.
 * @throws AddressProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
 * @return information of the address.
 */
@Throws(AddressProviderErrorException::class, AddressProviderUnauthorizedException::class)
fun getAddressInformation(currency: AddressCurrency, address: String): AddressInformation
```

The supported currencies are:

```kotlin
/**
 * Type of currency for an address.
 */
enum class AddressCurrency(val id: Int) {
    BITCOIN(0),
    ETHEREUM(1),
    LITECOIN(2),
    BITCOIN_CASH(3);
}
```

The information returned for the addresses is:

```kotlin
/**
 * Detailed information about an address.
 */
data class AddressInformation(

    /**
     * Address.
     * If blank or empty, not information was found for this address.
     */
    val identifier: String? = "",

    /**
     * Describes all alerts fired for this address.
     */
    val alerts: List<Alert>? = emptyList(),

    /**
     * Total amount in cryptocurrency available with address.
     */
    val balance: Double? = 0.0,

    /**
     * The currency code for the blockchain this address was searched on, [-1] if could not get the currency of the address.
     */
    val currency: Int? = -1,

    /**
     * The currency name for the blockchain this address was searched on.
     */
    val currencyVerbose: String? = "",

    /**
     * Date on which address has made its first transaction.
     */
    val earliestTransactionTime: String? = "",

    /**
     * Date on which address has made its last transaction.
     */
    val latestTransactionTime: String? = "",

    /**
     * An integer indicating if this address is Low Risk [1], Medium Risk [2] or High Risk [3] address or if no risks were detected [0], [-1] if could not fetch the risk level.
     */
    val riskLevel: Int? = -1,

    /**
     * Indicates if this address is Low Risk, Medium Risk , High Risk or if no risks were detected.
     */
    val riskLevelVerbose: String? = "",

    /**
     * Total amount received by the address in cryptocurrency.
     */
    val totalIncomingValue: String? = "",

    /**
     * Total amount received by the address in USD.
     */
    val totalIncomingValueUsd: String? = "",

    /**
     * Total amount sent by the address in cryptocurrency.
     */
    val totalOutgoingValue: String? = "",

    /**
     * Total amount sent by the address in USD.
     */
    val totalOutgoingValueUsd: String? = "",

    /**
     * UTC Timestamp for when this resource was created by you.
     */
    val createdAt: String? = "",

    /**
     * UTC Timestamp for most recent lookup of this resource.
     */
    val updatedAt: String? = ""
)
```

[1]: https://github.com/bitcoin/bips/blob/master/bip-0070.mediawiki
[2]: https://github.com/bitcoin/bips/blob/master/bip-0075.mediawiki
[3]: https://mvnrepository.com/artifact/com.netki/transactid
[4]: https://developers.google.com/protocol-buffers/
[5]: https://en.wikipedia.org/wiki/Elliptic-curve_cryptography
[6]: https://www.sec.gov/about/offices/ocie/aml2007/31cfr103.33g.pdf
[7]: https://www.occ.treas.gov/topics/supervision-and-examination/bsa/index-bsa.html
