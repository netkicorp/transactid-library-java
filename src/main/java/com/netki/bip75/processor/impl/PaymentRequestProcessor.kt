package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*

internal class PaymentRequestProcessor(
    addressInformationService: AddressInformationService,
    certificateValidator: CertificateValidator
) : Bip75MessageProcessor(addressInformationService, certificateValidator) {

    /**
     * {@inheritDoc}
     */
    override fun create(protocolMessageParameters: ProtocolMessageParameters): ByteArray {
        val paymentRequestParameters = protocolMessageParameters as PaymentRequestParameters
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
    override fun isValid(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        val protocolMessageMetadata = protocolMessageBinary.extractProtocolMessageMetadata()
        val messagePaymentRequest =
            protocolMessageBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessagePaymentRequest()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = protocolMessageBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
            }
        }

        val messagePaymentRequestUnsigned =
            messagePaymentRequest.removeMessageSenderSignature() as Messages.PaymentRequest

        val isCertificateChainValid = validateCertificate(
            messagePaymentRequest.getMessagePkiType(),
            messagePaymentRequest.senderPkiData.toStringLocal()
        )

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messagePaymentRequestUnsigned.validateMessageSignature(messagePaymentRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }
        messagePaymentRequestUnsigned.beneficiariesList.forEach { beneficiaryMessage ->
            beneficiaryMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificate(
                    attestationMessage.getAttestationPkiType(),
                    attestationMessage.pkiData.toStringLocal()
                )

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(
                            attestationMessage.attestation
                        )
                    )
                }

                val isSignatureValid =
                    attestationMessage.validateMessageSignature(beneficiaryMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(
                        ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(
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
    override fun parse(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?) =
        parsePaymentRequestBinary(protocolMessageBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parseWithAddressesInfo(
        protocolMessageBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): PaymentRequest {
        val paymentRequest = parsePaymentRequestBinary(protocolMessageBinary, recipientParameters)
        paymentRequest.beneficiariesAddresses.forEach { output ->
            val addressInfo = getAddressInformation(output.currency, output.script)
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
}
