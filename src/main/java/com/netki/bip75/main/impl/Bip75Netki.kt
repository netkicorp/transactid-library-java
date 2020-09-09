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
    override fun createInvoiceRequest(
        invoiceRequestParameters: InvoiceRequestParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters,
        attestationsRequested: List<Attestation>
    ) = bip75Service.createInvoiceRequest(
        invoiceRequestParameters,
        ownersParameters,
        senderParameters,
        attestationsRequested
    )

    /**
     * {@inheritDoc}
     */
    override fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray) =
        bip75Service.isInvoiceRequestValid(invoiceRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequest(invoiceRequestBinary: ByteArray) =
        bip75Service.parseInvoiceRequest(invoiceRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequestWithAddressesInfo(
        invoiceRequestBinary: ByteArray
    ) = bip75Service.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun createPaymentRequest(
        paymentRequestParameters: PaymentRequestParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters,
        attestationsRequested: List<Attestation>,
        paymentParametersVersion: Int
    ) = bip75Service.createPaymentRequest(
        paymentRequestParameters,
        ownersParameters,
        senderParameters,
        attestationsRequested,
        paymentParametersVersion
    )

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequest(paymentRequestBinary: ByteArray) =
        bip75Service.parsePaymentRequest(paymentRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequestWithAddressesInfo(paymentRequestBinary: ByteArray) =
        bip75Service.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentRequestValid(paymentRequestBinary: ByteArray) =
        bip75Service.isPaymentRequestValid(paymentRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun createPayment(
        paymentParameters: PaymentParameters,
        ownersParameters: List<OwnerParameters>
    ) = bip75Service.createPayment(paymentParameters, ownersParameters)

    /**
     * {@inheritDoc}
     */
    override fun parsePayment(paymentBinary: ByteArray) = bip75Service.parsePayment(paymentBinary)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentValid(paymentBinary: ByteArray) = bip75Service.isPaymentValid(paymentBinary)

    /**
     * {@inheritDoc}
     */
    override fun createPaymentAck(payment: Payment, memo: String) = bip75Service.createPaymentAck(payment, memo)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentAck(paymentAckBinary: ByteArray) = bip75Service.parsePaymentAck(paymentAckBinary)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray) = bip75Service.isPaymentAckValid(paymentAckBinary)
}
