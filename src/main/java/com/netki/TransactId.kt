package com.netki

import com.netki.bip75.config.Bip75Factory
import com.netki.bip75.main.Bip75
import com.netki.exceptions.*
import com.netki.model.*

/**
 * Generate Bip75 protocol messages.
 *
 * @see https://github.com/bitcoin/bips/blob/master/bip-0075.mediawiki
 */
class TransactId(private var bip75: Bip75) {

    companion object {
        /**
         * Method to get an instance of this class.
         * The ability to fetch detailed information of the addresses is optional.
         *
         * @param trustStoreLocation Path with the directory that contains the trust certificates chains.
         * This should be accessible and have with read permissions for the app that is running the library.
         * @param authorizationKey Key to connect fetch detailed information of addresses.
         * @return instance of TransactId.
         */
        @JvmStatic
        @JvmOverloads
        fun getInstance(trustStoreLocation: String, authorizationKey: String? = ""): TransactId {
            val bip75 = Bip75Factory.getInstance(trustStoreLocation, authorizationKey)
            return TransactId(bip75)
        }
    }

    /**
     * Create InvoiceRequest message.
     *
     * @param invoiceRequestParameters data to create the InvoiceRequest.
     * @param ownersParameters of the accounts for this transaction.
     * @param senderParameters of the protocol message.
     * @param attestationsRequested list of attestations requested.
     * @param recipientParameters information of the recipient of the message.
     * @param messageInformation status and information of the message status, by default "OK".
     * @return binary object of the message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     */
    @Throws(InvalidOwnersException::class)
    @JvmOverloads
    fun createInvoiceRequest(
        invoiceRequestParameters: InvoiceRequestParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters,
        attestationsRequested: List<Attestation>,
        recipientParameters: RecipientParameters? = null,
        messageInformation: MessageInformation = MessageInformation()
    ): ByteArray = bip75.createInvoiceRequest(
        invoiceRequestParameters,
        ownersParameters,
        senderParameters,
        attestationsRequested,
        recipientParameters,
        messageInformation
    )

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
    fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray): Boolean =
        bip75.isInvoiceRequestValid(invoiceRequestBinary)

    /**
     * Parse binary InvoiceRequest.
     *
     * @param invoiceRequestBinary binary data with the message to parse.
     * @return InvoiceRequest parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parseInvoiceRequest(invoiceRequestBinary: ByteArray): InvoiceRequest =
        bip75.parseInvoiceRequest(invoiceRequestBinary)

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
    ): InvoiceRequest = bip75.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)

    /**
     * Create binary PaymentRequest.
     *
     * @param paymentRequestParameters data to create the PaymentRequest.
     * @param ownersParameters of the accounts for this transaction.
     * @param senderParameters of the protocol message.
     * @param attestationsRequested list of attestations requested.
     * @param paymentParametersVersion version of the PaymentDetails message.
     * @param messageInformation status and information of the message status, by default "OK".
     * @return binary object of the message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     */
    @Throws(InvalidOwnersException::class)
    @JvmOverloads
    fun createPaymentRequest(
        paymentRequestParameters: PaymentRequestParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters,
        attestationsRequested: List<Attestation>,
        paymentParametersVersion: Int = 1,
        messageInformation: MessageInformation = MessageInformation()
    ): ByteArray = bip75.createPaymentRequest(
        paymentRequestParameters,
        ownersParameters,
        senderParameters,
        attestationsRequested,
        paymentParametersVersion,
        messageInformation
    )

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
    fun isPaymentRequestValid(paymentRequestBinary: ByteArray): Boolean =
        bip75.isPaymentRequestValid(paymentRequestBinary)

    /**
     * Parse binary PaymentRequest.
     *
     * @param paymentRequestBinary binary data with the message to parse.
     * @return PaymentRequest parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePaymentRequest(paymentRequestBinary: ByteArray): PaymentRequest =
        bip75.parsePaymentRequest(paymentRequestBinary)

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
    fun parsePaymentRequestWithAddressesInfo(paymentRequestBinary: ByteArray): PaymentRequest =
        bip75.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)

    /**
     * Create binary Payment.
     *
     * @param paymentParameters data to create the Payment.
     * @param ownersParameters of the accounts for this transaction.
     * @param messageInformation status and information of the message status, by default "OK".
     * @return binary object of the message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     */
    @Throws(InvalidOwnersException::class)
    fun createPayment(
        paymentParameters: PaymentParameters,
        ownersParameters: List<OwnerParameters>,
        messageInformation: MessageInformation = MessageInformation()
    ): ByteArray = bip75.createPayment(paymentParameters, ownersParameters, messageInformation)

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
    fun isPaymentValid(paymentBinary: ByteArray): Boolean = bip75.isPaymentValid(paymentBinary)

    /**
     * Parse binary Payment.
     *
     * @param paymentBinary binary data with the message to parse.
     * @return Payment parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePayment(paymentBinary: ByteArray): Payment = bip75.parsePayment(paymentBinary)

    /**
     * Create binary PaymentAck.
     *
     * @param payment data to create the Payment.
     * @param memo note that should be displayed to the customer.
     * @param messageInformation status and information of the message status, by default "OK".
     * @return binary object of the message created.
     */
    fun createPaymentAck(
        payment: Payment,
        memo: String,
        messageInformation: MessageInformation = MessageInformation()
    ): ByteArray = bip75.createPaymentAck(payment, memo, messageInformation)

    /**
     * Validate if a binary PaymentAck is valid.
     *
     * @param paymentAckBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean = bip75.isPaymentAckValid(paymentAckBinary)

    /**
     * Parse binary PaymentAck.
     *
     * @param paymentAckBinary binary data with the message to parse.
     * @return PaymentAck parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck = bip75.parsePaymentAck(paymentAckBinary)
}
