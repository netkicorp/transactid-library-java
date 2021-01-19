package com.netki.bip75.main

import com.netki.exceptions.*
import com.netki.model.*

/**
 * Generate Bip75 protocol messages.
 */
interface Bip75 {

    /**
     * Create InvoiceRequest message.
     *
     * @param invoiceRequestParameters data to create the InvoiceRequest.
     * @return binary object of the message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     * @throws EncryptionException if there is an error while creating the encrypted message.
     */
    @Throws(InvalidOwnersException::class, EncryptionException::class)
    fun createInvoiceRequest(invoiceRequestParameters: InvoiceRequestParameters): ByteArray

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
    fun isInvoiceRequestValid(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters? = null
    ): Boolean

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
    fun parseInvoiceRequestWithAddressesInfo(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters? = null
    ): InvoiceRequest

    /**
     * Create binary PaymentRequest.
     *
     * @param paymentRequestParameters data to create the PaymentRequest.
     * @param identifier for the transaction, this should be the same than the one included in the Protocol Message wrapping the initial InvoiceRequest.
     * @return binary object of the message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     * @throws EncryptionException if there is an error while creating the encrypted message.
     */
    @Throws(InvalidOwnersException::class, EncryptionException::class)
    fun createPaymentRequest(paymentRequestParameters: PaymentRequestParameters, identifier: String): ByteArray

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
    fun isPaymentRequestValid(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters? = null
    ): Boolean

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
    fun parsePaymentRequest(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters? = null
    ): PaymentRequest

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
    fun parsePaymentRequestWithAddressesInfo(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters? = null
    ): PaymentRequest

    /**
     * Create binary Payment.
     *
     * @param paymentParameters data to create the Payment.
     * @param identifier for the transaction, this should be the same than the one included in the Protocol Message wrapping the initial InvoiceRequest.
     * @return binary object of the message created.
     * @throws EncryptionException if there is an error while creating the encrypted message.
     */
    @Throws(EncryptionException::class)
    fun createPayment(paymentParameters: PaymentParameters, identifier: String): ByteArray

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
    fun isPaymentValid(paymentBinary: ByteArray, recipientParameters: RecipientParameters? = null): Boolean

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

    /**
     * Create binary PaymentAck.
     *
     * @param paymentAckParameters data to create the PaymentAck.
     * @param identifier for the transaction, this should be the same than the one included in the Protocol Message wrapping the initial InvoiceRequest.
     * @return binary object of the message created.
     * @throws EncryptionException if there is an error while creating the encrypted message.
     */
    @Throws(EncryptionException::class)
    fun createPaymentAck(paymentAckParameters: PaymentAckParameters, identifier: String): ByteArray

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
    fun isPaymentAckValid(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters? = null): Boolean

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
    fun parsePaymentAck(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters? = null): PaymentAck

    /**
     * Change the status code and/or the message for a protocol message.
     * @param protocolMessage to change status.
     * @param statusCode new status code.
     * @param statusMessage new message.
     * @return binary object of the message created.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun changeStatusProtocolMessage(
        protocolMessage: ByteArray,
        statusCode: StatusCode,
        statusMessage: String = ""
    ): ByteArray

    /**
     * Method to extract the metadata related to a protocol message.
     * @param protocolMessage to extract metadata from.
     * @return metadata related to the message.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun getProtocolMessageMetadata(protocolMessage: ByteArray): ProtocolMessageMetadata
}
