package com.netki.bip75.service.impl

import com.netki.bip75.protocol.Messages
import com.netki.bip75.service.Bip75Service
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.util.*
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE

/**
 * {@inheritDoc}
 */
class Bip75ServiceNetki : Bip75Service {

    /**
     * {@inheritDoc}
     */
    override fun createInvoiceRequest(
        invoiceRequestParameters: InvoiceRequestParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters
    ): ByteArray {

        ownersParameters.validate()

        val messageInvoiceRequestBuilder =
            invoiceRequestParameters.toMessageInvoiceRequestBuilderUnsigned(senderParameters)

        ownersParameters.forEach { owner ->
            val ownerMessage = owner.toMessageOwnerBuilderWithoutSignatures()

            owner.pkiDataParametersSets.forEach { pkiData ->
                ownerMessage.addSignatures(pkiData.toMessageSignature(owner.isPrimaryForTransaction))
            }

            messageInvoiceRequestBuilder.addOwners(ownerMessage)
        }

        val messageInvoiceRequest = messageInvoiceRequestBuilder.build()

        return messageInvoiceRequest.signMessage(senderParameters).toByteArray()
    }

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequest(invoiceRequestBinary: ByteArray): InvoiceRequest {
        val messageInvoiceRequest = invoiceRequestBinary.toMessageInvoiceRequest()
        return messageInvoiceRequest.toInvoiceRequest()
    }

    /**
     * {@inheritDoc}
     */
    override fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray): Boolean {
        val messageInvoiceRequest = invoiceRequestBinary.toMessageInvoiceRequest()

        val messageInvoiceRequestUnsigned =
            messageInvoiceRequest.removeMessageSenderSignature() as Messages.InvoiceRequest

        val isCertificateChainValid = messageInvoiceRequest.senderPkiData.toStringLocal()
            .validateCertificateChain(messageInvoiceRequest.getMessagePkiType())

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messageInvoiceRequestUnsigned.validateMessageSignature(messageInvoiceRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }

        messageInvoiceRequestUnsigned.ownersList.forEach { ownerMessage ->
            ownerMessage.signaturesList.forEach { signatureMessage ->
                val isCertificateOwnerChainValid =
                    signatureMessage.pkiData.toStringLocal()
                        .validateCertificateChain(signatureMessage.getSignaturePkiType())

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            signatureMessage.attestation
                        )
                    )
                }

                val isSignatureValid = signatureMessage.validateMessageSignature(ownerMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(signatureMessage.attestation))
                }

            }
        }

        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentRequest(
        paymentParameters: PaymentParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters,
        paymentParametersVersion: Int
    ): ByteArray {

        ownersParameters.validate()

        val messagePaymentRequestBuilder = paymentParameters
            .toMessagePaymentDetails()
            .toPaymentRequest(senderParameters, paymentParametersVersion)

        ownersParameters.forEach { owner ->
            val ownerMessage = owner.toMessageOwnerBuilderWithoutSignatures()

            owner.pkiDataParametersSets.forEach { pkiData ->
                ownerMessage.addSignatures(pkiData.toMessageSignature(owner.isPrimaryForTransaction))
            }

            messagePaymentRequestBuilder.addOwners(ownerMessage)
        }

        val messagePaymentRequest = messagePaymentRequestBuilder.build()

        return messagePaymentRequest.signMessage(senderParameters).toByteArray()
    }

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequest(paymentRequestBinary: ByteArray): PaymentRequest {
        val messagePaymentRequest = paymentRequestBinary.toMessagePaymentRequest()
        return messagePaymentRequest.toPaymentRequest()
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentRequestValid(paymentRequestBinary: ByteArray): Boolean {
        val messagePaymentRequest = paymentRequestBinary.toMessagePaymentRequest()

        val messagePaymentRequestUnsigned =
            messagePaymentRequest.removeMessageSenderSignature() as Messages.PaymentRequest

        val isCertificateChainValid = messagePaymentRequest.senderPkiData.toStringLocal()
            .validateCertificateChain(messagePaymentRequest.getMessagePkiType())

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messagePaymentRequestUnsigned.validateMessageSignature(messagePaymentRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }
        messagePaymentRequestUnsigned.ownersList.forEach { ownerMessage ->
            ownerMessage.signaturesList.forEach { signatureMessage ->
                val isCertificateOwnerChainValid =
                    signatureMessage.pkiData.toStringLocal()
                        .validateCertificateChain(signatureMessage.getSignaturePkiType())

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            signatureMessage.attestation
                        )
                    )
                }

                val isSignatureValid = signatureMessage.validateMessageSignature(ownerMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(signatureMessage.attestation))
                }
            }
        }
        
        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPayment(payment: Payment): ByteArray = payment.toMessagePayment().toByteArray()

    /**
     * {@inheritDoc}
     */
    override fun parsePayment(paymentBinary: ByteArray): Payment {
        val messagePayment = paymentBinary.toMessagePayment()
        return messagePayment.toPayment()
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentValid(paymentBinary: ByteArray): Boolean {
        paymentBinary.toMessagePayment()
        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentAck(payment: Payment, memo: String): ByteArray =
        payment.toMessagePaymentAck().toByteArray()

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck {
        val messagePaymentAck = paymentAckBinary.toMessagePaymentAck()
        return messagePaymentAck.toPaymentAck()
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean {
        paymentAckBinary.toMessagePaymentAck()
        return true
    }
}

