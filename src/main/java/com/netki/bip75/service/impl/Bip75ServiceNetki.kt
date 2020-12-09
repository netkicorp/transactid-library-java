package com.netki.bip75.service.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.bip75.service.Bip75Service
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_EV_NOT_VALID
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
    override fun createInvoiceRequest(invoiceRequestParameters: InvoiceRequestParameters): ByteArray {

        invoiceRequestParameters.originatorParameters.validate(true, OwnerType.ORIGINATOR)
        invoiceRequestParameters.beneficiaryParameters?.validate(false, OwnerType.BENEFICIARY)

        val messageInvoiceRequestBuilder =
            invoiceRequestParameters.toMessageInvoiceRequestBuilderUnsigned(
                invoiceRequestParameters.senderParameters,
                invoiceRequestParameters.attestationsRequested,
                invoiceRequestParameters.recipientParameters
            )

        invoiceRequestParameters.beneficiaryParameters?.forEach { beneficiary ->
            val beneficiaryMessage = beneficiary.toMessageBeneficiaryBuilderWithoutAttestations()

            beneficiary.pkiDataParametersSets.forEach { pkiData ->
                beneficiaryMessage.addAttestations(pkiData.toMessageAttestation(false))
            }

            messageInvoiceRequestBuilder.addBeneficiaries(beneficiaryMessage)
        }

        invoiceRequestParameters.originatorParameters.forEach { originator ->
            val originatorMessage = originator.toMessageOriginatorBuilderWithoutAttestations()

            originator.pkiDataParametersSets.forEach { pkiData ->
                originatorMessage.addAttestations(pkiData.toMessageAttestation(originator.isPrimaryForTransaction))
            }

            messageInvoiceRequestBuilder.addOriginators(originatorMessage)
        }

        val messageInvoiceRequest = messageInvoiceRequestBuilder.build()

        val invoiceRequest = messageInvoiceRequest.signMessage(invoiceRequestParameters.senderParameters).toByteArray()
        return when (invoiceRequestParameters.messageInformation.encryptMessage) {
            true -> invoiceRequest.toProtocolMessageEncrypted(
                MessageType.INVOICE_REQUEST,
                invoiceRequestParameters.messageInformation,
                invoiceRequestParameters.senderParameters,
                invoiceRequestParameters.recipientParameters
            )
            false -> invoiceRequest.toProtocolMessage(
                MessageType.INVOICE_REQUEST,
                invoiceRequestParameters.messageInformation,
                invoiceRequestParameters.senderParameters,
                invoiceRequestParameters.recipientParameters
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
        invoiceRequest.originatorsAddresses.forEach { output ->
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

        val isCertificateChainValid = validateCertificate(
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

        val senderEvCert = messageInvoiceRequest.senderEvCert.toStringLocal()
        if (!senderEvCert.isBlank()) {
            val isEvCert = certificateValidator.isEvCertificate(senderEvCert)
            check(isEvCert) {
                throw InvalidCertificateException(CERTIFICATE_VALIDATION_EV_NOT_VALID)
            }
        }

        messageInvoiceRequestUnsigned.originatorsList.forEach { originatorMessage ->
            originatorMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificate(
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

                val isSignatureValid =
                    attestationMessage.validateMessageSignature(originatorMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(
                        SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(
                            attestationMessage.attestation
                        )
                    )
                }
            }
        }

        messageInvoiceRequestUnsigned.originatorsList.forEach { beneficiaryMessage ->
            beneficiaryMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificate(
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
            }
        }

        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentRequest(paymentRequestParameters: PaymentRequestParameters): ByteArray {

        paymentRequestParameters.beneficiaryParameters.validate(true, OwnerType.BENEFICIARY)

        val messagePaymentRequestBuilder = paymentRequestParameters
            .toMessagePaymentDetails()
            .toPaymentRequest(
                paymentRequestParameters.senderParameters,
                paymentRequestParameters.paymentParametersVersion,
                paymentRequestParameters.attestationsRequested
            )

        paymentRequestParameters.beneficiaryParameters.forEach { beneficiary ->
            val beneficiaryMessage = beneficiary.toMessageBeneficiaryBuilderWithoutAttestations()

            beneficiary.pkiDataParametersSets.forEach { pkiData ->
                beneficiaryMessage.addAttestations(pkiData.toMessageAttestation(beneficiary.isPrimaryForTransaction))
            }

            messagePaymentRequestBuilder.addBeneficiaries(beneficiaryMessage)
        }

        val messagePaymentRequest = messagePaymentRequestBuilder.build()

        val paymentRequest = messagePaymentRequest.signMessage(paymentRequestParameters.senderParameters).toByteArray()

        return when (paymentRequestParameters.messageInformation.encryptMessage) {
            true -> paymentRequest.toProtocolMessageEncrypted(
                MessageType.PAYMENT_REQUEST,
                paymentRequestParameters.messageInformation,
                paymentRequestParameters.senderParameters,
                paymentRequestParameters.recipientParameters
            )
            false -> paymentRequest.toProtocolMessage(
                MessageType.PAYMENT_REQUEST,
                paymentRequestParameters.messageInformation,
                paymentRequestParameters.senderParameters,
                paymentRequestParameters.recipientParameters
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
        paymentRequest.beneficiariesAddresses.forEach { output ->
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

        val isCertificateChainValid = validateCertificate(
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
        messagePaymentRequestUnsigned.beneficiariesList.forEach { beneficiaryMessage ->
            beneficiaryMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificate(
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

                val isSignatureValid =
                    attestationMessage.validateMessageSignature(beneficiaryMessage.primaryForTransaction)

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
    override fun createPayment(paymentParameters: PaymentParameters): ByteArray {
        paymentParameters.originatorParameters.validate(true, OwnerType.ORIGINATOR)
        paymentParameters.beneficiaryParameters?.validate(false, OwnerType.BENEFICIARY)

        val paymentBuilder = paymentParameters.toMessagePaymentBuilder()

        paymentParameters.beneficiaryParameters?.forEach { beneficiary ->
            val beneficiaryMessage = beneficiary.toMessageBeneficiaryBuilderWithoutAttestations()

            beneficiary.pkiDataParametersSets.forEach { pkiData ->
                beneficiaryMessage.addAttestations(pkiData.toMessageAttestation(false))
            }

            paymentBuilder.addBeneficiaries(beneficiaryMessage)
        }

        paymentParameters.originatorParameters.forEach { originator ->
            val originatorMessage = originator.toMessageOriginatorBuilderWithoutAttestations()

            originator.pkiDataParametersSets.forEach { pkiData ->
                originatorMessage.addAttestations(pkiData.toMessageAttestation(originator.isPrimaryForTransaction))
            }

            paymentBuilder.addOriginators(originatorMessage)
        }

        val payment = paymentBuilder.build().toByteArray()

        return when (paymentParameters.messageInformation.encryptMessage) {
            true -> payment.toProtocolMessageEncrypted(
                MessageType.PAYMENT,
                paymentParameters.messageInformation,
                paymentParameters.senderParameters,
                paymentParameters.recipientParameters
            )
            false -> payment.toProtocolMessage(
                MessageType.PAYMENT,
                paymentParameters.messageInformation,
                paymentParameters.senderParameters,
                paymentParameters.recipientParameters
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

        payment.originatorsList.forEach { originatorMessage ->
            originatorMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificate(
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

                val isSignatureValid =
                    attestationMessage.validateMessageSignature(originatorMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(
                        SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(
                            attestationMessage.attestation
                        )
                    )
                }
            }
        }

        payment.originatorsList.forEach { beneficiaryMessage ->
            beneficiaryMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificate(
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
            }
        }

        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentAck(paymentAckParameters: PaymentAckParameters): ByteArray {
        val paymentAck = paymentAckParameters.payment.toMessagePaymentAck(paymentAckParameters.memo).toByteArray()

        return when (paymentAckParameters.messageInformation.encryptMessage) {
            true -> paymentAck.toProtocolMessageEncrypted(
                MessageType.PAYMENT_ACK,
                paymentAckParameters.messageInformation,
                paymentAckParameters.senderParameters,
                paymentAckParameters.recipientParameters
            )
            false -> paymentAck.toProtocolMessage(
                MessageType.PAYMENT_ACK,
                paymentAckParameters.messageInformation,
                paymentAckParameters.senderParameters,
                paymentAckParameters.recipientParameters
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
     * {@inheritDoc}
     */
    override fun changeStatusMessageProtocol(
        protocolMessage: ByteArray,
        statusCode: StatusCode,
        statusMessage: String
    ) = protocolMessage.changeStatus(statusCode, statusMessage)

    /**
     * Validate if a certificate is valid.
     *
     * @return true if yes, an exception with the detail otherwise.
     */
    @Throws(
        InvalidCertificateException::class,
        InvalidCertificateChainException::class
    )
    private fun validateCertificate(pkiType: PkiType, certificate: String): Boolean {
        return when (pkiType) {
            PkiType.NONE -> true
            PkiType.X509SHA256 -> {
                certificateValidator.validateCertificate(certificate)
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getProtocolMessageMetadata(protocolMessage: ByteArray) =
        protocolMessage.extractProtocolMessageMetadata()
}
