package com.netki.bip75.service

import com.netki.model.*
import com.sun.org.apache.xml.internal.security.signature.InvalidSignatureValueException
import java.io.InvalidObjectException

/**
 * Generate Bip75 protocol messages.
 */
interface Bip75Service {

    /**
     * Create binary InvoiceRequest.
     *
     * @param invoiceRequestParameters data to create the InvoiceRequest.
     * @param keyPairPem keys to do crypto operations for the InvoiceRequest.
     * @return binary object of the message created.
     */
    fun createInvoiceRequest(invoiceRequestParameters: InvoiceRequestParameters, keyPairPem: KeyPairPem): ByteArray

    /**
     * Validate if a binary InvoiceRequest is valid.
     *
     * @param invoiceRequestBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     * @exception InvalidSignatureValueException if the signature in the binary is not valid.
     */
    @Throws(
        InvalidObjectException::class,
        InvalidSignatureValueException::class
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
     * Create binary PaymentRequest.
     *
     * @param paymentDetails data to create the PaymentRequest.
     * @param keyPairPem keys to do crypto operations for the invoice request.
     * @param paymentDetailsVersion version of the PaymentDetails message.
     * @return binary object of the message created.
     */
    fun createPaymentRequest(
        paymentDetails: PaymentDetails,
        keyPairPem: KeyPairPem,
        paymentDetailsVersion: Int = 1
    ): ByteArray

    /**
     * Validate if a binary PaymentRequest is valid.
     *
     * @param paymentRequestBinary binary data to validate.
     * @return true if is valid, exception otherwise.
     * @exception InvalidObjectException if the binary is malformed
     * @exception InvalidSignatureValueException if the signature in the binary is not valid
     */
    @Throws(
        InvalidObjectException::class,
        InvalidSignatureValueException::class
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
     * Create binary Payment.
     *
     * @param payment data to create the Payment.
     * @return binary object of the message created.
     */
    fun createPayment(payment: Payment): ByteArray

    /**
     * Validate if a binary Payment is valid.
     *
     * @param paymentBinary binary data to validate.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     */
    @Throws(InvalidObjectException::class)
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
