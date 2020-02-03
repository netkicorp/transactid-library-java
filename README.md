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

To use the methods to create the BIP messages you can use the static methods in the class TransactId.

There are three main method types that you'll use: create\*, is\*Valid, and parse\*.

## Invoice Request

Please refer to the [BIP75][2] documentation for detailed requirements for a InvoiceRequest.  When it comes 
to using this library to create one you'll need to provide the following:

* InvoiceRequestParameters
    * amount: Long
    * privateKeyPem: String
    * notificationUrl: String

* KeyPairPem
    * certificatePem: String
    * memo: str
    * type: PkiType
    
Create an object for sending like so:

    val invoiceRequestBinary = TransactId.createInvoiceRequest(invoiceRequestParameters, keyPairPem)

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate 
the signature and parse one:

    TransactId.isInvoiceRequestValid(invoiceRequestBinary)
    
This will return true if there are no errors in the object and the signature is valid.

To access the data from the InvoiceRequest just do:

    val invoiceRequest = TransactId.parseInvoiceRequest(invoiceRequestBinary)

And that will return a object with all of the fields of the InvoiceRequest and the values that were 
filled in.

* InvoiceRequest
    * senderPublicKey: String
    * amount: Long
    * pkiType: String
    * pkiData: String
    * memo: String
    * notificationUrl: String
    * signature: String

## Payment Request

Please refer to the [BIP70][1] documentation for detailed requirements for a PaymentRequest.
When it comes to using this library to create one you'll need to provide the following:

* PaymentDetails
    * network: String
    * outputs: List<Output>
    * time: Timestamp
    * expires: Timestamp
    * memo: String
    * paymentUrl: String
    * merchantData: String
    
 Where: 
 
 * Output
    * amount: Long
    * script: String

* KeyPairPem
    * certificatePem: String
    * memo: str
    * type: PkiType
    
* paymentDetailsVersion: Int

Create an object for sending like so:

    val paymentRequestBinary = TransactId.createPaymentRequest(paymentDetails, keyPairPem, paymentDetailsVersion)

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to validate 
the signature and parse one:

    TransactId.isPaymentRequestValid(paymentRequestBinary)

This will return true if there are no errors in the object and the signature is valid.

To access the data from the InvoiceRequest just do:

    val paymentRequest = TransactId.parsePaymentRequest(paymentRequestBinary)

And that will return an object with all of the fields of the PaymentRequest and the values that were 
filled in.

* PaymentRequest
    * paymentDetailsVersion: Int
    * pkiType: String
    * pkiData: String
    * paymentDetails: PaymentDetails
    * signature: String
 
 Where:
 
 * PaymentDetails
    * network: String
    * outputs: List<Output>
    * time: Timestamp
    * expires: Timestamp
    * memo: String
    * paymentUrl: String
    * merchantData: String
    
Where: 
 
 * Output
    * amount: Long
    * script: String

## Payment

Please refer to the [BIP70][1] documentation for detailed requirements for a Payment. When it comes to using this 
library to create one you'll need to provide the following:

* Payment
    * merchantData: String
    * transactions: List<ByteArray>
    * outputs: List<Output>
    * memo: String
    
Where: 
 
 * Output
    * amount: Long
    * script: String

Create an object for sending like so:

    val paymentBinary = TransactId.createPayment(payment)

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.  
Please note that Payments aren't signed unlike the Invoice/PaymentRequest:

    TransactId.isPaymentValid(paymentBinary)

If this return true then we were able to parse the protobuf object.

To access the data from the Payment just do:

    val payment = TransactId.parsePayment(paymentBinary)

And that will return an object with all of the fields of the Payment and the values that were 
filled in.

* Payment
    * merchantData: String
    * transactions: List<ByteArray>
    * outputs: List<Output>
    * memo: String

Where: 
 
 * Output
    * amount: Long
    * script: String
    
## PaymentACK

Please refer to the [BIP70][1] documentation for detailed requirements for a PaymentACK. PaymentACKs are a bit different 
than other things in the library.  Due to the fact that art of the PaymentACK is the Payment object you are 
acknowledging, it's not possible to create an ACK without first verifying a Payment. 
When it comes to using this library to create one you'll need to provide the following:

* Payment
    * merchantData: String
    * transactions: List<ByteArray>
    * outputs: List<Output>
    * memo: String

Where: 
 
* Output
    * amount: Long
    * script: String
    
* memo: String

Create an object for sending like so:

    val paymentAckBinary = TransactId.createPaymentAck(payment, memo)

This will provide you with a serialized binary that you can then send to someone else who is able to 
parse and validate one of these things.

When you are on the receiving end of one of those binary strings you can do the following to parse one.  
Please note that Payments aren't signed unlike the Invoice/PaymentRequest:

    TransactId.isPaymentAckValid(paymentAckBinary)

If this return true then we were able to parse the protobuf object.

To access the data from the PaymentAck just do:

    val paymentAck = TransactId.parsePaymentAck(paymentAckBinary)
    
And that will return a dictionary with all of the fields of the PaymentACK and the values that were 
filled in.

* PaymentAck:

    * payment: Payment
    * memo: String
 
Where: 

* Payment
    * merchantData: String
    * transactions: List<ByteArray>
    * outputs: List<Output>
    * memo: String 
 
* Output
    * amount: Long
    * script: String


[1]: https://github.com/bitcoin/bips/blob/master/bip-0070.mediawiki
[2]: https://github.com/bitcoin/bips/blob/master/bip-0075.mediawiki
[3]: https://mvnrepository.com/artifact/com.netki/transactid
