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
    <version>0.1.0-alphaX</version>
    <type>pom</type>
</dependency>
```

### Gradle Users

```sh
compile group: 'com.netki', name: 'transactid', version: '0.1.0-alphaX', ext: 'pom'
```

## Adding jcenter

In case you are not using it, make sure to add jcenter as one of the repositories for dependencies, for example for gradle
```groovy
repositories {
	mavenCentral()
	jcenter()
}
```
## General Usage

To use the methods to create the BIP messages you need to fetch an instance with the method:

```kotlin
/**
 * Method to get an instance of this class.
 * The ability to fetch detailed information of the addresses is optional.
 *
 * @param trustStoreLocation Path with the directory that contains the trust certificates chains.
 * This should be accessible and have with read permissions for the app that is running the library.
 * @param authorizationKey Key to connect fetch detailed information of addresses.
 * @param developmentMode set to true if you are using this library in a sandbox environment.
 * @return instance of TransactId.
 */
```
@JvmStatic
@JvmOverloads
fun getInstance(
    trustStoreLocation: String,
    authorizationKey: String? = "",
    developmentMode: Boolean = false
): TransactId
```
The authorizationKey should be a valid Merkle key.

The TrustStore is used for the storage of certificates from the trusted Certificate Authority (CA), which is used in the verification of the certificate provided in the protocol messages.

The **trustStoreLocation** is the path where the TrustStore is located in the machine running the application.

If you are generating your certificates using Netki you can find the CA certificate here https://github.com/netkicorp/transactid-library-java/blob/master/src/main/resources/certificates/certificate_chain_netki_ca.cer

There are three main method types that you'll use: create\*, is\*Valid, and parse\*.

## Invoice Request

Please refer to the [BIP75][2] documentation for detailed requirements for a `InvoiceRequest`.

Create an object for sending like so:

```kotlin
/**
 * Create InvoiceRequest message.
 *
 * @param invoiceRequestParameters data to create the InvoiceRequest.
 */
@Throws(InvalidOwnersException::class, EncryptionException::class)
fun createInvoiceRequest(invoiceRequestParameters: InvoiceRequestParameters): ByteArray = bip75
```

[For detailed documentation of InvoiceRequestParameters click here.](https://github.com/netkicorp/transactid-library-java/blob/master/src/main/java/com/netki/model/InvoiceRequestParameters.kt)

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate
the signature and parse one:

```kotlin
/**
 * Validate if a binary InvoiceRequest is valid.
 *
 * @param invoiceRequestBinary binary data to validate.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception InvalidSignatureException if the signature in the binary is not valid.
 * @exception InvalidCertificateException if there is a problem with the certificates.
 * @exception InvalidCertificateChainException if the certificate chain is not valid.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(
    InvalidObjectException::class,
    InvalidSignatureException::class,
    InvalidCertificateException::class,
    InvalidCertificateChainException::class,
    EncryptionException::class
)
@JvmOverloads
fun isInvoiceRequestValid(
    invoiceRequestBinary: ByteArray,
    recipientParameters: RecipientParameters? = null
): Boolean
```


This will return true if there are no errors in the object and the signature is valid.

To access the data from the `InvoiceRequest` just do:

```kotlin
/**
 * Parse binary InvoiceRequest.
 *
 * @param invoiceRequestBinary binary data with the message to parse.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return InvoiceRequest parsed.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(InvalidObjectException::class, EncryptionException::class)
fun parseInvoiceRequest(
    invoiceRequestBinary: ByteArray,
    recipientParameters: RecipientParameters? = null
): InvoiceRequest
```

And that will return a object with all of the fields of the `InvoiceRequest` and the values that were filled in.

To access the data from the InvoiceRequest including the detailed information of the addresses included in the message (you require to initialize the library), just do:

```kotlin
/**
 * Parse binary InvoiceRequest and also get the detailed information of the addresses.
 *
 * @param invoiceRequestBinary binary data with the message to parse.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return InvoiceRequest parsed with the detailed information for each address.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception AddressProviderErrorException if there is an error fetching the information from the provider.
 * @exception AddressProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(
    InvalidObjectException::class,
    AddressProviderErrorException::class,
    AddressProviderUnauthorizedException::class,
    EncryptionException::class
)
@JvmOverloads
fun parseInvoiceRequestWithAddressesInfo(
    invoiceRequestBinary: ByteArray,
    recipientParameters: RecipientParameters? = null
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
 * @return binary object of the message created.
 * @throws InvalidOwnersException if the provided list of owners is not valid.
 * @throws EncryptionException if there is an error while creating the encrypted message.
 */
@Throws(InvalidOwnersException::class, EncryptionException::class)
fun createPaymentRequest(paymentRequestParameters: PaymentRequestParameters): ByteArray
```

[For detailed documentation of PaymentRequestParameters click here.](https://github.com/netkicorp/transactid-library-java/blob/master/src/main/java/com/netki/model/PaymentRequestParameters.kt)

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate
the signature and parse one:

```kotlin
/**
 * Validate if a binary PaymentRequest is valid.
 *
 * @param paymentRequestBinary binary data to validate.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception InvalidSignatureException if the signature in the binary is not valid.
 * @exception InvalidCertificateException if there is a problem with the certificates.
 * @exception InvalidCertificateChainException if the certificate chain is not valid.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(
    InvalidObjectException::class,
    InvalidSignatureException::class,
    InvalidCertificateException::class,
    InvalidCertificateChainException::class,
    EncryptionException::class
)
@JvmOverloads
fun isPaymentRequestValid(
    paymentRequestBinary: ByteArray,
    recipientParameters: RecipientParameters? = null
): Boolean
```

This will return true if there are no errors in the object and the signature is valid.

To access the data from the PaymentRequest just do:

```kotlin
/**
 * Parse binary PaymentRequest.
 *
 * @param paymentRequestBinary binary data with the message to parse.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return PaymentRequest parsed.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(InvalidObjectException::class, EncryptionException::class)
@JvmOverloads
fun parsePaymentRequest(
    paymentRequestBinary: ByteArray,
    recipientParameters: RecipientParameters? = null
): PaymentRequest
```

And that will return an object with all of the fields of the PaymentRequest and the values that were
filled in.

To access the data from the PaymentRequest including the detailed information of the addresses included in the message (you require to initialize the library), just do:

```kotlin
/**
 * Parse binary PaymentRequest and also get the detailed information of the addresses.
 *
 * @param paymentRequestBinary binary data with the message to parse.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return PaymentRequest parsed with the detailed information for each address.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception AddressProviderErrorException if there is an error fetching the information from the provider.
 * @exception AddressProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(
    InvalidObjectException::class,
    AddressProviderErrorException::class,
    AddressProviderUnauthorizedException::class,
    EncryptionException::class
)
@JvmOverloads
fun parsePaymentRequestWithAddressesInfo(
    paymentRequestBinary: ByteArray,
    recipientParameters: RecipientParameters? = null
): PaymentRequest
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
 * @return binary object of the message created.
 * @throws EncryptionException if there is an error while creating the encrypted message.
 */
@Throws(EncryptionException::class)
fun createPayment(paymentParameters: PaymentParameters): ByteArray
```

[For detailed documentation of PaymentParameters click here.](https://github.com/netkicorp/transactid-library-java/blob/master/src/main/java/com/netki/model/PaymentParameters.kt)

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.
Please note that Payments aren't signed unlike the Invoice/PaymentRequest:


```kotlin
/**
 * Validate if a binary Payment is valid.
 *
 * @param paymentBinary binary data to validate.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception InvalidSignatureException if the signature in the binary is not valid.
 * @exception InvalidCertificateException if there is a problem with the certificates.
 * @exception InvalidCertificateChainException if the certificate chain is not valid.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(
    InvalidObjectException::class,
    InvalidSignatureException::class,
    InvalidCertificateException::class,
    InvalidCertificateChainException::class,
    EncryptionException::class
)
@JvmOverloads
fun isPaymentValid(paymentBinary: ByteArray, recipientParameters: RecipientParameters? = null): Boolean
```

If this return true then we were able to parse the protobuf object.

To access the data from the Payment just do:

```kotlin
/**
 * Parse binary Payment.
 *
 * @param paymentBinary binary data with the message to parse.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return Payment parsed.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(InvalidObjectException::class, EncryptionException::class)
fun parsePayment(paymentBinary: ByteArray, recipientParameters: RecipientParameters? = null): Payment
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
 * @param paymentAckParameters data to create the PaymentAck.
 * @return binary object of the message created.
 * @throws EncryptionException if there is an error while creating the encrypted message.
 */
@Throws(EncryptionException::class)
fun createPaymentAck(paymentAckParameters: PaymentAckParameters): ByteArray
```

[For detailed documentation of PaymentAckParameters click here.](https://github.com/netkicorp/transactid-library-java/blob/master/src/main/java/com/netki/model/PaymentAckParameters.kt)

This will provide you with a serialized binary that you can then send to someone else who is able to
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.  
Please note that PaymentsAck aren't signed unlike the Invoice/PaymentRequest:


```kotlin
/**
 * Validate if a binary PaymentAck is valid.
 *
 * @param paymentAckBinary binary data to validate.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return true if is valid.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(InvalidObjectException::class, EncryptionException::class)
@JvmOverloads
fun isPaymentAckValid(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters? = null): Boolean
```

If this return true then we were able to parse the protobuf object.

To access the data from the PaymentAck just do:

```kotlin
/**
 * Parse binary PaymentAck.
 *
 * @param paymentAckBinary binary data with the message to parse.
 * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
 * @return PaymentAck parsed.
 * @exception InvalidObjectException if the binary is malformed.
 * @exception EncryptionException if there is an error decrypting or validating the encryption.
 */
@Throws(InvalidObjectException::class, EncryptionException::class)
@JvmOverloads
fun parsePaymentAck(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters? = null): PaymentAck
```

And that will return an object with all of the fields of the PaymentACK and the values that were filled in.

## Payment Protocol Status Communication
[Every ProtocolMessage or EncryptedProtocolMessage MUST include a status code which conveys information about the last message received](https://github.com/bitcoin/bips/blob/master/bip-0075.mediawiki#payment-protocol-status-communication), if any (for the first message sent, use a status of 1 "OK" even though there was no previous message). In the case of an error that causes the Payment Protocol process to be stopped or requires that message be retried, a ProtocolMessage or EncryptedProtocolMessage SHOULD be returned by the party generating the error. The content of the message MUST contain the same serialized_message or encrypted_message and identifier (if present) and MUST have the status_code set appropriately.

To be able to change the status of a protocol message you can do it using the following method.

```kotlin
/**
 * Change the status code and/or the message for a protocol message.
 * @param protocolMessage to change status.
 * @param statusCode new status code.
 * @param statusMessage new message.
 * @return binary object of the message created.
 * @exception InvalidObjectException if the binary is malformed.
 */
@Throws(InvalidObjectException::class)
@JvmOverloads
fun changeStatusMessageProtocol(
    protocolMessage: ByteArray,
    statusCode: StatusCode,
    statusMessage: String = ""
) : ByteArray
```

# Key Management system

This library contains a module to create and administrate KeyPairs and certificates.

## General Usage

To use the methods for the key management integration you need to fetch an instance with the method:

```kotlin
/**
 * Method to get an instance of this class.
 * All the parameters are optional depending the functions that want to be used.
 *
 * @param authorizationCertificateProviderKey to connect to the certificate provider.
 * @param authorizationSecureStorageKey to connect to the secure storage.
 * @param addressSecureStorage to connect to the secure storage.
 * @return instance of TidKms.
 */
@JvmStatic
@JvmOverloads
fun getInstance(
    authorizationCertificateProviderKey: String = "",
    authorizationSecureStorageKey: String = "",
    addressSecureStorage: String = ""
): TidKms {
    val keyManagement = KeyManagementFactory.getInstance(
        authorizationCertificateProviderKey,
        authorizationSecureStorageKey,
        addressSecureStorage
    )
    return TidKms(keyManagement)
}
```
To obtain the authorizationCertificateProviderKey please ask for it to your Netki contact.

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
This library includes an optional integration with Hashicorp Vault for key storage using the key/value secrets engine and can be launched as a Docker container.

As a convenience, we have included a basic Vault setup for your consideration in the docker folder.

See Hashicorp's Vault Docker documentation for more info: https://hub.docker.com/_/vault

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

To use the methods for the address information you need to fetch an instance with the method:

```kotlin
/**
 * Method to get an instance of this class.
 *
 * @param authorizationKey Key to connect to an external address provider.
 * @return instance of TidAddressInfo.
 */
@JvmStatic
@JvmOverloads
fun getInstance(authorizationKey: String): TidAddressInfo {
    val addressInformation = AddressInformationFactory.getInstance(authorizationKey)
    return TidAddressInfo(addressInformation)
}
```

The authorizationKey should be a valid Merkle key.

After you have the instance, you can fetch the detailed information of an address with the following method:        

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
