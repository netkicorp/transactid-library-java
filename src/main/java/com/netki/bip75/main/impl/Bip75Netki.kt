package com.netki.bip75.main.impl

import com.netki.bip75.main.Bip75
import com.netki.bip75.service.Bip75Service
import com.netki.model.*

/**
 * {@inheritDoc}
 */
internal class Bip75Netki(private val bip75Service: Bip75Service) : Bip75 {

    /**
     * {@inheritDoc}
     */
    override fun createInvoiceRequest(invoiceRequestParameters: InvoiceRequestParameters) =
        bip75Service.createInvoiceRequest(invoiceRequestParameters)

    /**
     * {@inheritDoc}
     */
    override fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.isInvoiceRequestValid(invoiceRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequest(invoiceRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.parseInvoiceRequest(invoiceRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequestWithAddressesInfo(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ) = bip75Service.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun createPaymentRequest(paymentRequestParameters: PaymentRequestParameters) =
        bip75Service.createPaymentRequest(paymentRequestParameters)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequest(paymentRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.parsePaymentRequest(paymentRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequestWithAddressesInfo(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ) =
        bip75Service.parsePaymentRequestWithAddressesInfo(paymentRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentRequestValid(paymentRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.isPaymentRequestValid(paymentRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun createPayment(paymentParameters: PaymentParameters) = bip75Service.createPayment(paymentParameters)

    /**
     * {@inheritDoc}
     */
    override fun parsePayment(paymentBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.parsePayment(paymentBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentValid(paymentBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.isPaymentValid(paymentBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun createPaymentAck(paymentAckParameters: PaymentAckParameters) =
        bip75Service.createPaymentAck(paymentAckParameters)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentAck(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.parsePaymentAck(paymentAckBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters?) =
        bip75Service.isPaymentAckValid(paymentAckBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun changeStatusProtocolMessage(
        protocolMessage: ByteArray,
        statusCode: StatusCode,
        statusMessage: String
    ) = bip75Service.changeStatusProtocolMessage(protocolMessage, statusCode, statusMessage)

    /**
     * {@inheritDoc}
     */
    override fun getProtocolMessageMetadata(protocolMessage: ByteArray): ProtocolMessageMetadata =
        bip75Service.getProtocolMessageMetadata(protocolMessage)
}
