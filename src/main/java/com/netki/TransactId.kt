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
object TransactId {

    /**
     * Instance to generate Bip75 protocol messages.
     */
    private var bip75: Bip75 = Bip75Factory.getInstance()

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
    ) =
        bip75.createInvoiceRequest(invoiceRequestParameters, ownerParameters, senderParameters)

    /**
     * Validate if a binary InvoiceRequest is valid.
     *
     * @param invoiceRequestBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     * @exception InvalidSignatureException if the signature in the binary is not valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     * @exception InvalidCertificateChainException if the certificate chain is not valid.
     * @exception InvalidKeystoreException if there is a problem with the Keystore containing the certificate chain.
     */
    @Throws(
        InvalidObjectException::class,
        InvalidSignatureException::class,
        InvalidCertificateException::class,
        InvalidCertificateChainException::class,
        InvalidKeystoreException::class
    )
    fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray) = bip75.isInvoiceRequestValid(invoiceRequestBinary)

    /**
     * Parse binary InvoiceRequest.
     *
     * @param invoiceRequestBinary binary data with the message to parse.
     * @return InvoiceRequest parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parseInvoiceRequest(invoiceRequestBinary: ByteArray) = bip75.parseInvoiceRequest(invoiceRequestBinary)

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
    ) = bip75.createPaymentRequest(paymentParameters, ownerParameters, senderParameters, paymentParametersVersion)

    /**
     * Validate if a binary PaymentRequest is valid.
     *
     * @param paymentRequestBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     * @exception InvalidSignatureException if the signature in the binary is not valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     * @exception InvalidCertificateChainException if the certificate chain is not valid.
     * @exception InvalidKeystoreException if there is a problem with the Keystore containing the certificate chain.
     */
    @Throws(
        InvalidObjectException::class,
        InvalidSignatureException::class,
        InvalidCertificateException::class,
        InvalidCertificateChainException::class,
        InvalidKeystoreException::class
    )
    fun isPaymentRequestValid(paymentRequestBinary: ByteArray) = bip75.isPaymentRequestValid(paymentRequestBinary)

    /**
     * Parse binary PaymentRequest.
     *
     * @param paymentRequestBinary binary data with the message to parse.
     * @return PaymentRequest parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePaymentRequest(paymentRequestBinary: ByteArray) = bip75.parsePaymentRequest(paymentRequestBinary)

    /**
     * Create binary Payment.
     *
     * @param payment data to create the Payment.
     * @return binary object of the message created.
     */
    fun createPayment(payment: Payment) = bip75.createPayment(payment)

    /**
     * Validate if a binary Payment is valid.
     *
     * @param paymentBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun isPaymentValid(paymentBinary: ByteArray) = bip75.isPaymentValid(paymentBinary)

    /**
     * Parse binary Payment.
     *
     * @param paymentBinary binary data with the message to parse.
     * @return Payment parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePayment(paymentBinary: ByteArray) = bip75.parsePayment(paymentBinary)

    /**
     * Create binary PaymentAck.
     *
     * @param payment data to create the Payment.
     * @param memo note that should be displayed to the customer.
     * @return binary object of the message created.
     */
    fun createPaymentAck(payment: Payment, memo: String) = bip75.createPaymentAck(payment, memo)

    /**
     * Validate if a binary PaymentAck is valid.
     *
     * @param paymentAckBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun isPaymentAckValid(paymentAckBinary: ByteArray) = bip75.isPaymentAckValid(paymentAckBinary)

    /**
     * Parse binary PaymentAck.
     *
     * @param paymentAckBinary binary data with the message to parse.
     * @return PaymentAck parsed.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
    fun parsePaymentAck(paymentAckBinary: ByteArray) = bip75.parsePaymentAck(paymentAckBinary)
}
