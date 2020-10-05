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
        return invoiceRequest.toProtocolMessage(MessageType.INVOICE_REQUEST, messageInformation)
    }

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequest(invoiceRequestBinary: ByteArray) = parseInvoiceRequestBinary(invoiceRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequestWithAddressesInfo(
        invoiceRequestBinary: ByteArray
    ): InvoiceRequest {
        val invoiceRequest = parseInvoiceRequestBinary(invoiceRequestBinary)
        invoiceRequest.outputs.forEach { output ->
            val addressInfo = addressInformationService.getAddressInformation(output.currency, output.script)
            output.addressInformation = addressInfo
        }
        return invoiceRequest
    }

    private fun parseInvoiceRequestBinary(invoiceRequestBinary: ByteArray): InvoiceRequest {
        val messageInvoiceRequest = invoiceRequestBinary.getSerializedMessage().toMessageInvoiceRequest()
        return messageInvoiceRequest.toInvoiceRequest(invoiceRequestBinary.extractProtocolMessageMetadata())
    }

    /**
     * {@inheritDoc}
     */
    override fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray): Boolean {
        val messageInvoiceRequest = invoiceRequestBinary.getSerializedMessage().toMessageInvoiceRequest()

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
        messageInformation: MessageInformation
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
        return paymentRequest.toProtocolMessage(MessageType.PAYMENT_REQUEST, messageInformation)
    }

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequest(paymentRequestBinary: ByteArray) = parsePaymentRequestBinary(paymentRequestBinary)

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequestWithAddressesInfo(paymentRequestBinary: ByteArray): PaymentRequest {
        val paymentRequest = parsePaymentRequestBinary(paymentRequestBinary)
        paymentRequest.paymentRequestParameters.outputs.forEach { output ->
            val addressInfo = addressInformationService.getAddressInformation(output.currency, output.script)
            output.addressInformation = addressInfo
        }
        return paymentRequest
    }

    private fun parsePaymentRequestBinary(paymentRequestBinary: ByteArray): PaymentRequest {
        val messagePaymentRequest = paymentRequestBinary.getSerializedMessage().toMessagePaymentRequest()
        return messagePaymentRequest.toPaymentRequest(paymentRequestBinary.extractProtocolMessageMetadata())
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentRequestValid(paymentRequestBinary: ByteArray): Boolean {
        val messagePaymentRequest = paymentRequestBinary.getSerializedMessage().toMessagePaymentRequest()

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
        messageInformation: MessageInformation
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
        return payment.toProtocolMessage(MessageType.PAYMENT, messageInformation)
    }


    /**
     * {@inheritDoc}
     */
    override fun parsePayment(paymentBinary: ByteArray): Payment {
        val messagePayment = paymentBinary.getSerializedMessage().toMessagePayment()
        return messagePayment.toPayment(paymentBinary.extractProtocolMessageMetadata())
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentValid(paymentBinary: ByteArray): Boolean {
        val payment = paymentBinary.getSerializedMessage().toMessagePayment()

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
        messageInformation: MessageInformation
    ): ByteArray {
        val paymentAck = payment.toMessagePaymentAck(memo).toByteArray()
        return paymentAck.toProtocolMessage(MessageType.PAYMENT_ACK, messageInformation)
    }

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck {
        val messagePaymentAck = paymentAckBinary.getSerializedMessage().toMessagePaymentAck()
        return messagePaymentAck.toPaymentAck(paymentAckBinary.extractProtocolMessageMetadata())
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean {
        paymentAckBinary.getSerializedMessage().toMessagePaymentAck()
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
