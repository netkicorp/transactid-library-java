package com.netki.bip75.service.impl

import com.netki.bip75.protocol.Messages
import com.netki.bip75.service.Bip75Service
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CryptoModule
import com.netki.util.*
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE

/**
 * {@inheritDoc}
 */
class Bip75ServiceNetki() : Bip75Service {

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

        ownersParameters.forEachIndexed { index, owner ->
            messageInvoiceRequestBuilder.addOwners(index, owner.toMessageOwner())
        }

        val messageInvoiceRequest = messageInvoiceRequestBuilder.build()

        val ownersSignatures = ownersParameters.signMessage(messageInvoiceRequest)

        val messageInvoiceRequestWithOwnersBuilder = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(messageInvoiceRequest)

        ownersParameters.forEachIndexed { index, owner ->
            val ownerSignatures = ownersSignatures[index]
            messageInvoiceRequestWithOwnersBuilder.removeOwners(index)
            messageInvoiceRequestWithOwnersBuilder.addOwners(index, owner.toOwnerMessageWithSignature(ownerSignatures))
        }

        val messageInvoiceRequestWithOwners = messageInvoiceRequestWithOwnersBuilder.build()

        return messageInvoiceRequestWithOwners.signMessage(senderParameters).toByteArray()
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
            .validateCertificateChain(messageInvoiceRequest.getMessageSenderPkiType())

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messageInvoiceRequestUnsigned.validateMessageSignature(messageInvoiceRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }

        messageInvoiceRequestUnsigned.ownersList.forEach { owner ->
            owner.signaturesList.forEach { signature ->
                val isCertificateOwnerChainValid =
                    signature.pkiData.toStringLocal().validateCertificateChain(signature.getSignaturePkiType())

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            signature.attestation
                        )
                    )
                }
            }
        }

        val invoiceRequestWithoutOwnersSignatureBuilder = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(messageInvoiceRequestUnsigned)

        val signatures = messageInvoiceRequestUnsigned.ownersList.getSignatures()
        val ownersWithoutSignature = messageInvoiceRequestUnsigned.ownersList.removeOwnersSignatures()

        invoiceRequestWithoutOwnersSignatureBuilder.clearOwners()
        invoiceRequestWithoutOwnersSignatureBuilder.addAllOwners(ownersWithoutSignature)

        val invoiceRequestWithoutOwnerSignatureHash =
            CryptoModule.getHash256(invoiceRequestWithoutOwnersSignatureBuilder.build().toByteArray())

        signatures.forEach { signature ->
            signature.forEach { (key, value) ->
                val isOwnerSignatureValid = CryptoModule.validateSignature(
                    value.second,
                    invoiceRequestWithoutOwnerSignatureHash,
                    value.first
                )
                check(isOwnerSignatureValid) {
                    throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(key))
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

        ownersParameters.forEachIndexed { index, owner ->
            messagePaymentRequestBuilder.addOwners(index, owner.toMessageOwner())
        }

        val messagePaymentRequest = messagePaymentRequestBuilder.build()

        val ownersSignatures = ownersParameters.signMessage(messagePaymentRequest)

        val messagePaymentRequestWithOwnersBuilder = Messages.PaymentRequest.newBuilder()
            .mergeFrom(messagePaymentRequest)

        ownersParameters.forEachIndexed { index, owner ->
            val ownerSignatures = ownersSignatures[index]
            messagePaymentRequestWithOwnersBuilder.removeOwners(index)
            messagePaymentRequestWithOwnersBuilder.addOwners(index, owner.toOwnerMessageWithSignature(ownerSignatures))
        }

        val messagePaymentRequestWithOwners = messagePaymentRequestWithOwnersBuilder.build()

        return messagePaymentRequestWithOwners.signMessage(senderParameters).toByteArray()
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
            .validateCertificateChain(messagePaymentRequest.getMessageSenderPkiType())

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messagePaymentRequestUnsigned.validateMessageSignature(messagePaymentRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }

        messagePaymentRequestUnsigned.ownersList.forEach { owner ->
            owner.signaturesList.forEach { signature ->
                val isCertificateOwnerChainValid =
                    signature.pkiData.toStringLocal().validateCertificateChain(signature.getSignaturePkiType())

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            signature.attestation
                        )
                    )
                }
            }
        }

        val paymentRequestWithoutOwnersSignatureBuilder = Messages.PaymentRequest.newBuilder()
            .mergeFrom(messagePaymentRequestUnsigned)

        val signatures = messagePaymentRequestUnsigned.ownersList.getSignatures()
        val ownersWithoutSignature = messagePaymentRequestUnsigned.ownersList.removeOwnersSignatures()

        paymentRequestWithoutOwnersSignatureBuilder.clearOwners()
        paymentRequestWithoutOwnersSignatureBuilder.addAllOwners(ownersWithoutSignature)

        val invoiceRequestWithoutOwnerSignatureHash =
            CryptoModule.getHash256(paymentRequestWithoutOwnersSignatureBuilder.build().toByteArray())

        signatures.forEach { signature ->
            signature.forEach { (key, value) ->
                val isOwnerSignatureValid = CryptoModule.validateSignature(
                    value.second,
                    invoiceRequestWithoutOwnerSignatureHash,
                    value.first
                )
                check(isOwnerSignatureValid) {
                    throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(key))
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

