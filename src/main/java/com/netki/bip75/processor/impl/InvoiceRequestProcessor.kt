package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.bip75.util.*
import com.netki.bip75.util.toMessageBeneficiaryBuilderWithoutAttestations
import com.netki.bip75.util.toMessageInvoiceRequestBuilderUnsigned
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*

internal class InvoiceRequestProcessor(
    addressInformationService: AddressInformationService,
    certificateValidator: CertificateValidator
) : Bip75MessageProcessor(addressInformationService, certificateValidator) {

    /**
     * {@inheritDoc}
     */
    override fun create(protocolMessageParameters: ProtocolMessageParameters): ByteArray {
        val invoiceRequestParameters = protocolMessageParameters as InvoiceRequestParameters
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
    override fun isValid(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        val protocolMessageMetadata = protocolMessageBinary.extractProtocolMessageMetadata()
        val messageInvoiceRequest =
            protocolMessageBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessageInvoiceRequest()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = protocolMessageBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
            }
        }

        val messageInvoiceRequestUnsigned =
            messageInvoiceRequest.removeMessageSenderSignature() as Messages.InvoiceRequest

        val isCertificateChainValid = validateCertificate(
            messageInvoiceRequest.getMessagePkiType(),
            messageInvoiceRequest.senderPkiData.toStringLocal()
        )

        check(isCertificateChainValid) {
            throw InvalidCertificateChainException(ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
        }

        val isSenderSignatureValid =
            messageInvoiceRequestUnsigned.validateMessageSignature(messageInvoiceRequest.senderSignature.toStringLocal())

        check(isSenderSignatureValid) {
            throw InvalidSignatureException(ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
        }

        val senderEvCert = messageInvoiceRequest.senderEvCert.toStringLocal()
        if (!senderEvCert.isBlank()) {
            val isEvCert = isEvCertificate(senderEvCert)
            check(isEvCert) {
                throw InvalidCertificateException(ErrorInformation.CERTIFICATE_VALIDATION_EV_NOT_VALID)
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

        messageInvoiceRequestUnsigned.beneficiariesList.forEach { beneficiaryMessage ->
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
    override fun parse(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?) =
        parseInvoiceRequestBinary(protocolMessageBinary, recipientParameters)

    /**
     * {@inheritDoc}
     */
    override fun parseWithAddressesInfo(
        protocolMessageBinary: ByteArray,
        recipientParameters: RecipientParameters?
    ): InvoiceRequest {
        val invoiceRequest = parseInvoiceRequestBinary(protocolMessageBinary, recipientParameters)
        invoiceRequest.originatorsAddresses.forEach { originatorAddress ->
            originatorAddress.addressInformation =
                getAddressInformation(originatorAddress.currency, originatorAddress.script)
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
}
