package com.netki

import com.netki.bip75.config.Bip75Factory
import com.netki.exceptions.*
import com.netki.model.*

/**
 * Generate Bip75 protocol messages.
 *
 * @see https://github.com/bitcoin/bips/blob/master/bip-0075.mediawiki
 */
object TransactId {

    /**
     * Key to connect fetch detailed information of addresses.
     */
    private var authorizationKey: String? = null

    /**
     * Instance to generate Bip75 protocol messages.
     */
    private val bip75 by lazy {
        Bip75Factory.getInstance(authorizationKey)
    }

    /**
     * Method to initialize the library with the ability to fetch detailed information of the addresses.
     * You need to initialize it only if address detailed info is required.
     *
     * @param authorizationKey to fetch the required data.
     */
    fun init(authorizationKey: String) {
        this.authorizationKey = authorizationKey
    }

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
    ): ByteArray =
        bip75.createInvoiceRequest(invoiceRequestParameters, ownersParameters, senderParameters, attestationsRequested)

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
    ): ByteArray =
        bip75.createPaymentRequest(
            paymentRequestParameters,
            ownersParameters,
            senderParameters,
            attestationsRequested,
            paymentParametersVersion
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
     * @return binary object of the message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     */
    @Throws(InvalidOwnersException::class)
    fun createPayment(
        paymentParameters: PaymentParameters,
        ownersParameters: List<OwnerParameters>
    ): ByteArray = bip75.createPayment(paymentParameters, ownersParameters)

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
     * @return binary object of the message created.
     */
    fun createPaymentAck(payment: Payment, memo: String): ByteArray = bip75.createPaymentAck(payment, memo)

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
