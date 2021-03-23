package com.netki.bip75.service.impl

import com.netki.bip75.service.Bip75Service
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_ORIGINATOR_CERTIFICATE_CA
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.message.main.Message
import com.netki.model.*
import com.netki.security.CertificateValidator

/**
 * {@inheritDoc}
 */
internal class Bip75ServiceNetki(
    private val message: Message,
    private val certificateValidator: CertificateValidator
) : Bip75Service {

    /**
     * {@inheritDoc}
     */
    override fun createInvoiceRequest(invoiceRequestParameters: InvoiceRequestParameters) =
        message.createInvoiceRequest(invoiceRequestParameters)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequest(invoiceRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        message.parseInvoiceRequest(invoiceRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequestWithAddressesInfo(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ) = message.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun isInvoiceRequestValid(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): Boolean {
        val invoiceRequest = parseInvoiceRequest(invoiceRequestBinary, recipientParameters)

        check(validateCertificateChain(invoiceRequest.senderPkiData, invoiceRequest.senderPkiType)) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        invoiceRequest.originators.forEach { originator ->
            originator.pkiDataSet.forEach { attestation ->
                check(validateCertificateChain(attestation.certificatePem, attestation.type)) {
                    throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_ORIGINATOR_CERTIFICATE_CA.format(attestation.attestation))
                }
            }
        }

        invoiceRequest.beneficiaries.forEach { beneficiary ->
            beneficiary.pkiDataSet.forEach { attestation ->
                check(validateCertificateChain(attestation.certificatePem, attestation.type)) {
                    throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA.format(attestation.attestation))
                }
            }
        }

        return message.isInvoiceRequestValid(invoiceRequestBinary, recipientParameters)
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentRequest(paymentRequestParameters: PaymentRequestParameters, identifier: String) =
        message.createPaymentRequest(paymentRequestParameters, identifier)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequest(paymentRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        message.parsePaymentRequest(paymentRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequestWithAddressesInfo(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ) = message.parsePaymentRequestWithAddressesInfo(paymentRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentRequestValid(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): Boolean {
        val paymentRequest = parsePaymentRequest(paymentRequestBinary, recipientParameters)

        check(validateCertificateChain(paymentRequest.senderPkiData, paymentRequest.senderPkiType)) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        paymentRequest.beneficiaries.forEach { beneficiary ->
            beneficiary.pkiDataSet.forEach { attestation ->
                check(validateCertificateChain(attestation.certificatePem, attestation.type)) {
                    throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA.format(attestation.attestation))
                }
            }
        }

        return message.isPaymentRequestValid(paymentRequestBinary, recipientParameters)
    }

    /**
     * {@inheritDoc}
     */
    override fun createPayment(paymentParameters: PaymentParameters, identifier: String) =
        message.createPayment(paymentParameters, identifier)

    /**
     * {@inheritDoc}
     */
    override fun parsePayment(paymentBinary: ByteArray, recipientParameters: RecipientParameters?) =
        message.parsePayment(paymentBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentValid(paymentBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        val payment = parsePayment(paymentBinary, recipientParameters)

        payment.originators.forEach { originator ->
            originator.pkiDataSet.forEach { attestation ->
                check(validateCertificateChain(attestation.certificatePem, attestation.type)) {
                    throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_ORIGINATOR_CERTIFICATE_CA.format(attestation.attestation))
                }
            }
        }

        payment.beneficiaries.forEach { beneficiary ->
            beneficiary.pkiDataSet.forEach { attestation ->
                check(validateCertificateChain(attestation.certificatePem, attestation.type)) {
                    throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA.format(attestation.attestation))
                }
            }
        }

        return message.isPaymentValid(paymentBinary, recipientParameters)
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentAck(paymentAckParameters: PaymentAckParameters, identifier: String) =
        message.createPaymentAck(paymentAckParameters, identifier)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentAck(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters?) =
        message.parsePaymentAck(paymentAckBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        return message.isPaymentAckValid(paymentAckBinary, recipientParameters)
    }

    /**
     * {@inheritDoc}
     */
    override fun changeStatusProtocolMessage(
        protocolMessage: ByteArray,
        statusCode: StatusCode,
        statusMessage: String
    ) = message.changeStatusProtocolMessage(protocolMessage, statusCode, statusMessage)

    /**
     * {@inheritDoc}
     */
    override fun getProtocolMessageMetadata(protocolMessage: ByteArray) =
        message.getProtocolMessageMetadata(protocolMessage)

    private fun validateCertificateChain(clientCertificatesPem: String?, pkiType: PkiType?): Boolean {
        return when (pkiType) {
            null, PkiType.NONE -> true
            PkiType.X509SHA256 -> certificateValidator.validateCertificateChain(
                clientCertificatesPem ?: throw InvalidCertificateException(
                    CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND
                )
            )
        }
    }
}
