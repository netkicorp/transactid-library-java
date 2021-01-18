package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.util.*
import com.netki.bip75.util.toMessageBeneficiaryBuilderWithoutAttestations
import com.netki.bip75.util.toMessagePaymentBuilder
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*

internal class PaymentProcessor(
    addressInformationService: AddressInformationService,
    certificateValidator: CertificateValidator
) : Bip75MessageProcessor(addressInformationService, certificateValidator) {

    /**
     * {@inheritDoc}
     */
    override fun create(protocolMessageParameters: ProtocolMessageParameters): ByteArray {
        val paymentParameters = protocolMessageParameters as PaymentParameters
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
    override fun isValid(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        val protocolMessageMetadata = protocolMessageBinary.extractProtocolMessageMetadata()
        val payment = protocolMessageBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
            .toMessagePayment()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = protocolMessageBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
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
                        ErrorInformation.CERTIFICATE_VALIDATION_INVALID_ORIGINATOR_CERTIFICATE_CA.format(
                            attestationMessage.attestation
                        )
                    )
                }

                val isSignatureValid =
                    attestationMessage.validateMessageSignature(originatorMessage.primaryForTransaction)

                check(isSignatureValid) {
                    throw InvalidSignatureException(
                        ErrorInformation.SIGNATURE_VALIDATION_INVALID_ORIGINATOR_SIGNATURE.format(
                            attestationMessage.attestation
                        )
                    )
                }
            }
        }

        payment.beneficiariesList.forEach { beneficiaryMessage ->
            beneficiaryMessage.attestationsList.forEach { attestationMessage ->
                val isCertificateOwnerChainValid = validateCertificate(
                    attestationMessage.getAttestationPkiType(),
                    attestationMessage.pkiData.toStringLocal()
                )

                check(isCertificateOwnerChainValid) {
                    throw InvalidCertificateChainException(
                        ErrorInformation.CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA.format(
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
    override fun parse(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?): Payment {
        val protocolMessageMetadata = protocolMessageBinary.extractProtocolMessageMetadata()
        val messagePayment =
            protocolMessageBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessagePayment()
        return messagePayment.toPayment(protocolMessageMetadata)
    }

    /**
     * {@inheritDoc}
     */
    override fun parseWithAddressesInfo(
        protocolMessageBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): ProtocolMessage {
        throw NotImplementedError("Method not supported for this message")
    }
}
