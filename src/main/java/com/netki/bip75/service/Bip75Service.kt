package com.netki.bip75.service

import com.netki.model.*

/**
 * Generate Bip75 protocol messages.
 */
interface Bip75Service {

    /**
     * Create InvoiceRequest message.
     *
     * @param invoiceRequestParameters data to create the InvoiceRequest.
     * @param keyPairPem keys to do crypto operations for the InvoiceRequest.
     * @return binary object of the message created.
     */
    fun createInvoiceRequest(invoiceRequestParameters: InvoiceRequestParameters, keyPairPem: KeyPairPem): ByteArray

    /**
     * Validate if an InvoiceRequest message is valid.
     *
     * @param invoiceRequestBinary binary data to validate.
     * @return true if is valid, false otherwise.
     */
    fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray): Boolean

    /**
     * Parse binary InvoiceRequest message.
     *
     * @param invoiceRequestBinary binary data with the message to parse.
     * @return InvoiceRequest parsed.
     */
    fun parseInvoiceRequest(invoiceRequestBinary: ByteArray): InvoiceRequest

    /**
     * Create PaymentRequest message.
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
     * Validate if a PaymentRequest message is valid.
     *
     * @param paymentRequestBinary binary data to validate.
     * @return true if is valid, false otherwise.
     */
    fun isPaymentRequestValid(paymentRequestBinary: ByteArray): Boolean

    /**
     * Parse binary PaymentRequest message.
     *
     * @param paymentRequestBinary binary data with the message to parse.
     * @return PaymentRequest parsed.
     */
    fun parsePaymentRequest(paymentRequestBinary: ByteArray): PaymentRequest

    /**
     * Create Payment message.
     *
     * @param payment data to create the Payment.
     * @return binary object of the message created.
     */
    fun createPayment(payment: Payment): ByteArray

    /**
     * Validate if a Payment message is valid.
     *
     * @param paymentBinary binary data to validate.
     * @return true if is valid, false otherwise.
     */
    fun isPaymentValid(paymentBinary: ByteArray): Boolean

    /**
     * Parse binary Payment message.
     *
     * @param paymentBinary binary data with the message to parse.
     * @return Payment parsed.
     */
    fun parsePayment(paymentBinary: ByteArray): Payment

    /**
     * Create PaymentAck message.
     *
     * @param payment data to create the Payment.
     * @param memo note that should be displayed to the customer.
     * @return binary object of the message created.
     */
    fun createPaymentAck(payment: Payment, memo: String): ByteArray

    /**
     * Validate if a PaymentAck message is valid.
     *
     * @param paymentAckBinary binary data to validate.
     * @return true if is valid, false otherwise.
     */
    fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean

    /**
     * Parse binary PaymentAck message.
     *
     * @param paymentAckBinary binary data with the message to parse.
     * @return PaymentAck parsed.
     */
    fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck
}
