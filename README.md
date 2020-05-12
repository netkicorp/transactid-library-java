# TransactID Library Java Edition

This library is a wrapper for using [BIP70][1] and [BIP75][2]

## Download

If you are using maven you can download the latest JAR or grab via [Maven][3]:
```
<dependency>
    <groupId>com.netki</groupId>
    <artifactId>transactid</artifactId>
    <version>0.1.0-alpha0</version>
    <type>pom</type>
</dependency>
```
Or using gradle
```
compile group: 'com.netki', name: 'transactid', version: '0.1.0-alpha0', ext: 'pom'
```

## General Usage

To use the methods to create the BIP messages you can use the static methods in the class TransactId for example:

        val transactIdInstance = TransactId
        transactIdInstance.isInvoiceRequestValid(invoiceRequestBinary)
        
        or

        TransactId.isInvoiceRequestValid(invoiceRequestBinary)

There are three main method types that you'll use: create\*, is\*Valid, and parse\*.

## Invoice Request

Please refer to the [BIP75][2] documentation for detailed requirements for a InvoiceRequest.
  
Create an object for sending like so:

        /**
         * Create InvoiceRequest message.
         *
         * @param invoiceRequestParameters data to create the InvoiceRequest.
         * @param ownerParameters of the accounts for this transaction.
         * @param senderParameters of the protocol message.
         * @return binary object of the message created.
         * @throws InvalidOwnersException if the provided list of owners is not valid.
         */
        @Throws(InvalidOwnersException::class)
        fun createInvoiceRequest(
            invoiceRequestParameters: InvoiceRequestParameters,
            ownerParameters: List<OwnerParameters>,
            senderParameters: SenderParameters
        ): ByteArray

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate 
the signature and parse one:

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
    
This will return true if there are no errors in the object and the signature is valid.

To access the data from the InvoiceRequest just do:

        /**
         * Parse binary InvoiceRequest.
         *
         * @param invoiceRequestBinary binary data with the message to parse.
         * @return InvoiceRequest parsed.
         * @exception InvalidObjectException if the binary is malformed.
         */
        @Throws(InvalidObjectException::class)
        fun parseInvoiceRequest(invoiceRequestBinary: ByteArray): InvoiceRequest

And that will return a object with all of the fields of the InvoiceRequest and the values that were 
filled in.

## Payment Request

Please refer to the [BIP70][1] documentation for detailed requirements for a PaymentRequest.

Create an object for sending like so:

        /**
         * Create binary PaymentRequest.
         *
         * @param paymentParameters data to create the PaymentRequest.
         * @param ownerParameters of the accounts for this transaction.
         * @param senderParameters of the protocol message.
         * @param paymentParametersVersion version of the PaymentDetails message.
         * @return binary object of the message created.
         * @throws InvalidOwnersException if the provided list of owners is not valid.
         */
        @Throws(InvalidOwnersException::class)
        fun createPaymentRequest(
            paymentParameters: PaymentParameters,
            ownerParameters: List<OwnerParameters>,
            senderParameters: SenderParameters,
            paymentParametersVersion: Int = 1
        ): ByteArray

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate 
the signature and parse one:

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

This will return true if there are no errors in the object and the signature is valid.

To access the data from the PaymentRequest just do:

        /**
         * Parse binary PaymentRequest.
         *
         * @param paymentRequestBinary binary data with the message to parse.
         * @return PaymentRequest parsed.
         * @exception InvalidObjectException if the binary is malformed.
         */
        @Throws(InvalidObjectException::class)
        fun parsePaymentRequest(paymentRequestBinary: ByteArray): PaymentRequest
        
And that will return an object with all of the fields of the PaymentRequest and the values that were 
filled in.

## Payment

Please refer to the [BIP70][1] documentation for detailed requirements for a Payment. 

Create an object for sending like so:

        /**
         * Create binary Payment.
         *
         * @param payment data to create the Payment.
         * @return binary object of the message created.
         */
        fun createPayment(payment: Payment): ByteArray

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.  
Please note that Payments aren't signed unlike the Invoice/PaymentRequest:

        /**
         * Validate if a binary Payment is valid.
         *
         * @param paymentBinary binary data to validate.
         * @return true if is valid.
         * @exception InvalidObjectException if the binary is malformed.
         */
        @Throws(InvalidObjectException::class)
        fun isPaymentValid(paymentBinary: ByteArray): Boolean

If this return true then we were able to parse the protobuf object.

To access the data from the Payment just do:

        /**
         * Parse binary Payment.
         *
         * @param paymentBinary binary data with the message to parse.
         * @return Payment parsed.
         * @exception InvalidObjectException if the binary is malformed.
         */
        @Throws(InvalidObjectException::class)
        fun parsePayment(paymentBinary: ByteArray): Payment

And that will return an object with all of the fields of the Payment and the values that were 
filled in.

## PaymentACK

Please refer to the [BIP70][1] documentation for detailed requirements for a PaymentACK. PaymentACKs are a bit different 
than other things in the library.  Due to the fact that art of the PaymentACK is the Payment object you are 
acknowledging, it's not possible to create an ACK without first verifying a Payment. 

Create an object for sending like so:

        /**
         * Create binary PaymentAck.
         *
         * @param payment data to create the Payment.
         * @param memo note that should be displayed to the customer.
         * @return binary object of the message created.
         */
        fun createPaymentAck(payment: Payment, memo: String): ByteArray

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.  
Please note that PaymentsAck aren't signed unlike the Invoice/PaymentRequest:

        /**
         * Validate if a binary PaymentAck is valid.
         *
         * @param paymentAckBinary binary data to validate.
         * @return true if is valid.
         * @exception InvalidObjectException if the binary is malformed.
         */
        @Throws(InvalidObjectException::class)
        fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean

If this return true then we were able to parse the protobuf object.

To access the data from the PaymentAck just do:

        /**
         * Parse binary PaymentAck.
         *
         * @param paymentAckBinary binary data with the message to parse.
         * @return PaymentAck parsed.
         * @exception InvalidObjectException if the binary is malformed.
         */
        @Throws(InvalidObjectException::class)
        fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck
    
And that will return a dictionary with all of the fields of the PaymentACK and the values that were 
filled in.

[1]: https://github.com/bitcoin/bips/blob/master/bip-0070.mediawiki
[2]: https://github.com/bitcoin/bips/blob/master/bip-0075.mediawiki
[3]: https://mvnrepository.com/artifact/com.netki/transactid

---
## Vault for Key Storage
This library includes integration with Hashicorp Vault for key storage using the key/value secrets engine and can be launched as a Docker container.
See Hashicorp's Vault Docker documentation here: https://hub.docker.com/_/vault

