package com.netki.bip75.service.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.bip75.service.Bip75Service
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE

/**
 * {@inheritDoc}
 */
internal class Bip75ServiceNetki(
    private val certificateValidator: CertificateValidator,
    private val addressInformationService: AddressInformationService
) : Bip75Service {

    /**
     * {@inheritDoc}
     */
    override fun createInvoiceRequest(
        invoiceRequestParameters: InvoiceRequestParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters,
        attestationsRequested: List<Attestation>,
        recipientParameters: RecipientParameters?,
        messageInformation: MessageInformation
    ): ByteArray {

        ownersParameters.validate()

        val messageInvoiceRequestBuilder =
            invoiceRequestParameters.toMessageInvoiceRequestBuilderUnsigned(
                senderParameters,
                attestationsRequested,
                recipientParameters
            )

        ownersParameters.forEach { owner ->
            val ownerMessage = owner.toMessageOwnerBuilderWithoutAttestations()

            owner.pkiDataParametersSets.forEach { pkiData ->
                ownerMessage.addAttestations(pkiData.toMessageAttestation(owner.isPrimaryForTransaction))
            }

            messageInvoiceRequestBuilder.addOwners(ownerMessage)
        }

        val messageInvoiceRequest = messageInvoiceRequestBuilder.build()

        val invoiceRequest = messageInvoiceRequest.signMessage(senderParameters).toByteArray()
        return when (messageInformation.encryptMessage) {
            true -> invoiceRequest.toProtocolMessageEncrypted(
                MessageType.INVOICE_REQUEST,
                messageInformation,
                senderParameters,
                recipientParameters
            )
            false -> invoiceRequest.toProtocolMessage(
                MessageType.INVOICE_REQUEST,
                messageInformation,
                senderParameters,
                recipientParameters
            )
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequest(invoiceRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        parseInvoiceRequestBinary(invoiceRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequestWithAddressesInfo(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): InvoiceRequest {
        val invoiceRequest = parseInvoiceRequestBinary(invoiceRequestBinary, recipientParameters)
        invoiceRequest.outputs.forEach { output ->
            val addressInfo = addressInformationService.getAddressInformation(output.currency, output.script)
            output.addressInformation = addressInfo
        }
        return invoiceRequest
    }

    private fun parseInvoiceRequestBinary(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): InvoiceRequest {
        val protocolMessageMetadata = invoiceRequestBinary.extractProtocolMessageMetadata()
        val messageInvoiceRequest =
            invoiceRequestBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessageInvoiceRequest()
        return messageInvoiceRequest.toInvoiceRequest(protocolMessageMetadata)
    }

    /**
     * {@inheritDoc}
     */
    override fun isInvoiceRequestValid(
        invoiceRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): Boolean {
        val protocolMessageMetadata = invoiceRequestBinary.extractProtocolMessageMetadata()
        val messageInvoiceRequest =
            invoiceRequestBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessageInvoiceRequest()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = invoiceRequestBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
            }
        }

        val messageInvoiceRequestUnsigned =
            messageInvoiceRequest.removeMessageSenderSignature() as Messages.InvoiceRequest

        val isCertificateChainValid = validateCertificateChain(
            messageInvoiceRequest.getMessagePkiType(),
            messageInvoiceRequest.senderPkiData.toStringLocal()
        )

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messageInvoiceRequestUnsigned.validateMessageSignature(messageInvoiceRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }

        messageInvoiceRequestUnsigned.ownersList.forEach { ownerMessage ->
            ownerMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificateChain(
                    attestationMessage.getAttestationPkiType(),
                    attestationMessage.pkiData.toStringLocal()
                )

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            attestationMessage.attestation
                        )
                    )
                }

                val isSignatureValid = attestationMessage.validateMessageSignature(ownerMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(
                        SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(
                            attestationMessage.attestation
                        )
                    )
                }
            }
        }

        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentRequest(
        paymentRequestParameters: PaymentRequestParameters,
        ownersParameters: List<OwnerParameters>,
        senderParameters: SenderParameters,
        attestationsRequested: List<Attestation>,
        paymentParametersVersion: Int,
        messageInformation: MessageInformation,
        recipientParameters: RecipientParameters?
    ): ByteArray {

        ownersParameters.validate()

        val messagePaymentRequestBuilder = paymentRequestParameters
            .toMessagePaymentDetails()
            .toPaymentRequest(senderParameters, paymentParametersVersion, attestationsRequested)

        ownersParameters.forEach { owner ->
            val ownerMessage = owner.toMessageOwnerBuilderWithoutAttestations()

            owner.pkiDataParametersSets.forEach { pkiData ->
                ownerMessage.addAttestations(pkiData.toMessageAttestation(owner.isPrimaryForTransaction))
            }

            messagePaymentRequestBuilder.addOwners(ownerMessage)
        }

        val messagePaymentRequest = messagePaymentRequestBuilder.build()

        val paymentRequest = messagePaymentRequest.signMessage(senderParameters).toByteArray()

        return when (messageInformation.encryptMessage) {
            true -> paymentRequest.toProtocolMessageEncrypted(
                MessageType.PAYMENT_REQUEST,
                messageInformation,
                senderParameters,
                recipientParameters
            )
            false -> paymentRequest.toProtocolMessage(
                MessageType.PAYMENT_REQUEST,
                messageInformation,
                senderParameters,
                recipientParameters
            )
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequest(paymentRequestBinary: ByteArray, recipientParameters: RecipientParameters?) =
        parsePaymentRequestBinary(paymentRequestBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequestWithAddressesInfo(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): PaymentRequest {
        val paymentRequest = parsePaymentRequestBinary(paymentRequestBinary, recipientParameters)
        paymentRequest.paymentRequestParameters.outputs.forEach { output ->
            val addressInfo = addressInformationService.getAddressInformation(output.currency, output.script)
            output.addressInformation = addressInfo
        }
        return paymentRequest
    }

    private fun parsePaymentRequestBinary(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): PaymentRequest {
        val protocolMessageMetadata = paymentRequestBinary.extractProtocolMessageMetadata()
        val messagePaymentRequest =
            paymentRequestBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessagePaymentRequest()
        return messagePaymentRequest.toPaymentRequest(protocolMessageMetadata)
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentRequestValid(
        paymentRequestBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): Boolean {
        val protocolMessageMetadata = paymentRequestBinary.extractProtocolMessageMetadata()
        val messagePaymentRequest =
            paymentRequestBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessagePaymentRequest()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = paymentRequestBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
            }
        }

        val messagePaymentRequestUnsigned =
            messagePaymentRequest.removeMessageSenderSignature() as Messages.PaymentRequest

        val isCertificateChainValid = validateCertificateChain(
            messagePaymentRequest.getMessagePkiType(),
            messagePaymentRequest.senderPkiData.toStringLocal()
        )

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messagePaymentRequestUnsigned.validateMessageSignature(messagePaymentRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }
        messagePaymentRequestUnsigned.ownersList.forEach { ownerMessage ->
            ownerMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificateChain(
                    attestationMessage.getAttestationPkiType(),
                    attestationMessage.pkiData.toStringLocal()
                )

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            attestationMessage.attestation
                        )
                    )
                }

                val isSignatureValid = attestationMessage.validateMessageSignature(ownerMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(
                        SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(
                            attestationMessage.attestation
                        )
                    )
                }
            }
        }

        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPayment(
        paymentParameters: PaymentParameters,
        ownersParameters: List<OwnerParameters>,
        messageInformation: MessageInformation,
        senderParameters: SenderParameters?,
        recipientParameters: RecipientParameters?
    ): ByteArray {
        ownersParameters.validate()

        val paymentBuilder = paymentParameters.toMessagePaymentBuilder()

        ownersParameters.forEach { owner ->
            val ownerMessage = owner.toMessageOwnerBuilderWithoutAttestations()

            owner.pkiDataParametersSets.forEach { pkiData ->
                ownerMessage.addAttestations(pkiData.toMessageAttestation(owner.isPrimaryForTransaction))
            }

            paymentBuilder.addOwners(ownerMessage)
        }

        val payment = paymentBuilder.build().toByteArray()

        return when (messageInformation.encryptMessage) {
            true -> payment.toProtocolMessageEncrypted(
                MessageType.PAYMENT,
                messageInformation,
                senderParameters,
                recipientParameters
            )
            false -> payment.toProtocolMessage(
                MessageType.PAYMENT,
                messageInformation,
                senderParameters,
                recipientParameters
            )
        }
    }


    /**
     * {@inheritDoc}
     */
    override fun parsePayment(paymentBinary: ByteArray, recipientParameters: RecipientParameters?): Payment {
        val protocolMessageMetadata = paymentBinary.extractProtocolMessageMetadata()
        val messagePayment = paymentBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
            .toMessagePayment()
        return messagePayment.toPayment(protocolMessageMetadata)
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentValid(paymentBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        val protocolMessageMetadata = paymentBinary.extractProtocolMessageMetadata()
        val payment = paymentBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
            .toMessagePayment()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = paymentBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
            }
        }

        payment.ownersList.forEach { ownerMessage ->
            ownerMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificateChain(
                    attestationMessage.getAttestationPkiType(),
                    attestationMessage.pkiData.toStringLocal()
                )

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            attestationMessage.attestation
                        )
                    )
                }

                val isSignatureValid = attestationMessage.validateMessageSignature(ownerMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(
                        SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(
                            attestationMessage.attestation
                        )
                    )
                }
            }
        }

        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentAck(
        payment: Payment,
        memo: String,
        messageInformation: MessageInformation,
        senderParameters: SenderParameters?,
        recipientParameters: RecipientParameters?
    ): ByteArray {
        val paymentAck = payment.toMessagePaymentAck(memo).toByteArray()

        return when (messageInformation.encryptMessage) {
            true -> paymentAck.toProtocolMessageEncrypted(
                MessageType.PAYMENT_ACK,
                messageInformation,
                senderParameters,
                recipientParameters
            )
            false -> paymentAck.toProtocolMessage(
                MessageType.PAYMENT_ACK,
                messageInformation,
                senderParameters,
                recipientParameters
            )
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentAck(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters?): PaymentAck {
        val protocolMessageMetadata = paymentAckBinary.extractProtocolMessageMetadata()
        val messagePaymentAck =
            paymentAckBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessagePaymentAck()

        return messagePaymentAck.toPaymentAck(paymentAckBinary.extractProtocolMessageMetadata())
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        val protocolMessageMetadata = paymentAckBinary.extractProtocolMessageMetadata()
        paymentAckBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
            .toMessagePaymentAck()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = paymentAckBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
            }
        }
        return true
    }

    /**
     * Validate if a certificate belongs to a valid Certificate chain.
     *
     * @return true if yes, false otherwise.
     */
    private fun validateCertificateChain(pkiType: PkiType, certificate: String): Boolean {
        return when (pkiType) {
            PkiType.NONE -> true
            PkiType.X509SHA256 -> {
                certificateValidator.validateCertificateChain(certificate)
            }
        }
    }
}
