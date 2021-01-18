package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.util.*
import com.netki.bip75.util.toMessagePaymentAck
import com.netki.bip75.util.toProtocolMessage
import com.netki.bip75.util.toProtocolMessageEncrypted
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*

internal class PaymentAckProcessor(
    addressInformationService: AddressInformationService,
    certificateValidator: CertificateValidator
) : Bip75MessageProcessor(addressInformationService, certificateValidator) {

    /**
     * {@inheritDoc}
     */
    override fun create(protocolMessageParameters: ProtocolMessageParameters): ByteArray {
        val paymentAckParameters = protocolMessageParameters as PaymentAckParameters
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
    override fun isValid(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?): Boolean {
        val protocolMessageMetadata = protocolMessageBinary.extractProtocolMessageMetadata()
        protocolMessageBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
            .toMessagePaymentAck()

        if (protocolMessageMetadata.encrypted) {
            val isSenderEncryptionSignatureValid = protocolMessageBinary.validateMessageEncryptionSignature()

            check(isSenderEncryptionSignatureValid) {
                throw InvalidSignatureException(ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
            }
        }
        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun parse(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters?): PaymentAck {
        val protocolMessageMetadata = protocolMessageBinary.extractProtocolMessageMetadata()
        val messagePaymentAck =
            protocolMessageBinary.getSerializedMessage(protocolMessageMetadata.encrypted, recipientParameters)
                .toMessagePaymentAck()

        return messagePaymentAck.toPaymentAck(protocolMessageBinary.extractProtocolMessageMetadata())
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
