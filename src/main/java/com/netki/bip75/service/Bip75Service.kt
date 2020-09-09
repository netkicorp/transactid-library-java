package com.netki.bip75.service

import com.netki.exceptions.*
import com.netki.model.*

/**
 * Generate Bip75 protocol messages.
 */
internal interface Bip75Service {

    /**
     * Create binary InvoiceRequest.
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

    /**
     * Parse binary InvoiceRequest.
     *
     * @param invoiceRequestBinary binary data with the message to parse.
     * @return InvoiceRequest parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parseInvoiceRequest(invoiceRequestBinary: ByteArray): InvoiceRequest

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

    /**
     * Create binary PaymentRequest.
     *
     * @param paymentRequestParameters data to create the PaymentRequest.
     * @param ownersParameters of the accounts for this transaction.
     * @param senderParameters of the protocol message.
     * @param paymentParametersVersion version of the PaymentDetails message.
     * @param attestationsRequested list of attestations requested.
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

    /**
     * Validate if a binary PaymentRequest is valid.
     *
     * @param paymentRequestBinary binary data to validate.
     * @return true if is valid, exception otherwise.
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

    /**
     * Parse binary PaymentRequest.
     *
     * @param paymentRequestBinary binary data with the message to parse.
     * @return PaymentRequest parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePaymentRequest(paymentRequestBinary: ByteArray): PaymentRequest

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

    /**
     * Create binary Payment.
     *
     * @param paymentParameters data to create the Payment.
     * @param ownersParameters of the accounts for this transaction.
     * @return binary object of the message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     */
    @Throws(InvalidOwnersException::class)
    fun createPayment(paymentParameters: PaymentParameters, ownersParameters: List<OwnerParameters>): ByteArray

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

    /**
     * Parse binary Payment.
     *
     * @param paymentBinary binary data with the message to parse.
     * @return Payment parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePayment(paymentBinary: ByteArray): Payment

    /**
     * Create binary PaymentAck.
     *
     * @param payment data to create the Payment.
     * @param memo note that should be displayed to the customer.
     * @return binary object of the message created.
     */
    fun createPaymentAck(payment: Payment, memo: String): ByteArray

    /**
     * Validate if a binary PaymentAck is valid.
     *
     * @param paymentAckBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean

    /**
     * Parse binary PaymentAck.
     *
     * @param paymentAckBinary binary data with the message to parse.
     * @return PaymentAck parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck
}
