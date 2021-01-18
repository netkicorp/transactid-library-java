package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.EncryptionException
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.MessageType
import com.netki.model.PaymentAckParameters
import com.netki.model.StatusCode
import com.netki.security.CertificateValidator
import com.netki.util.ErrorInformation
import com.netki.util.TestData
import com.netki.util.toByteString
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PaymentAckProcessorTest {

    private lateinit var mockAddressInformationService: AddressInformationService
    private val certificateValidator = CertificateValidator("src/test/resources/certificates")
    private lateinit var paymentAckProcessor: PaymentAckProcessor

    @BeforeAll
    fun setUp() {
        mockAddressInformationService = Mockito.mock(AddressInformationService::class.java)
        paymentAckProcessor = PaymentAckProcessor(mockAddressInformationService, certificateValidator)
    }

    @Test
    fun `Create and validate PaymentAckBinary`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO
        )
        val paymentAckBinary = paymentAckProcessor.create(paymentAckParameters)

        assert(paymentAckProcessor.isValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO_PAYMENT_ACK
        )
        val paymentBinary = paymentAckProcessor.create(paymentAckParameters)
        val paymentAck = paymentAckProcessor.parse(paymentBinary)

        assert(paymentAck.payment.merchantData == TestData.Payment.PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == TestData.Payment.PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == TestData.Payment.PAYMENT.outputs)
        assert(paymentAck.payment.originators.size == TestData.Payment.PAYMENT.originators.size)
        assert(paymentAck.payment.memo == TestData.Payment.PAYMENT.memo)
        assert(paymentAck.payment.protocolMessageMetadata == null)
        assert(paymentAck.memo == TestData.Payment.MEMO_PAYMENT_ACK)
        assert(!paymentAck.protocolMessageMetadata.identifier.isBlank())
        assert(paymentAck.protocolMessageMetadata.version == 1L)
        assert(paymentAck.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(paymentAck.protocolMessageMetadata.statusMessage.isEmpty())
        assert(paymentAck.protocolMessageMetadata.messageType == MessageType.PAYMENT_ACK)
    }

    @Test
    fun `Create and validate PaymentAckBinary with message information`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO
        )
        val paymentAckBinary = paymentAckProcessor.create(paymentAckParameters)

        assert(paymentAckProcessor.isValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck with message information`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO_PAYMENT_ACK,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL
        )
        val paymentBinary = paymentAckProcessor.create(paymentAckParameters)
        val paymentAck = paymentAckProcessor.parse(paymentBinary)

        assert(paymentAck.payment.merchantData == TestData.Payment.PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == TestData.Payment.PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == TestData.Payment.PAYMENT.outputs)
        assert(paymentAck.payment.originators.size == TestData.Payment.PAYMENT.originators.size)
        assert(paymentAck.payment.memo == TestData.Payment.PAYMENT.memo)
        assert(paymentAck.payment.protocolMessageMetadata == null)
        assert(paymentAck.memo == TestData.Payment.MEMO_PAYMENT_ACK)
        assert(!paymentAck.protocolMessageMetadata.identifier.isBlank())
        assert(paymentAck.protocolMessageMetadata.version == 1L)
        assert(paymentAck.protocolMessageMetadata.statusCode == StatusCode.CANCEL)
        assert(paymentAck.protocolMessageMetadata.statusMessage == TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(paymentAck.protocolMessageMetadata.messageType == MessageType.PAYMENT_ACK)
    }

    @Test
    fun `Validate invalid PaymentAckBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            paymentAckProcessor.isValid("fakePaymentAckBinary".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary Encrypted, Owners and Sender with PkiData`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )
        val paymentAckBinary = paymentAckProcessor.create(paymentAckParameters)

        assert(
            paymentAckProcessor.isValid(
                paymentAckBinary,
                TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
            )
        )
    }

    @Test
    fun `Create and validate PaymentAckBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentAckBinary = paymentAckProcessor.create(paymentAckParameters)

        assertThrows(EncryptionException::class.java) {
            paymentAckProcessor.isValid(paymentAckBinary)
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentAckBinary = paymentAckProcessor.create(paymentAckParameters)

        val protocolMessageCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(paymentAckBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(
                paymentAckProcessor.isValid(
                    protocolMessageCorrupted,
                    TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
                )
            )
        }

        assert(exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate PaymentAckBinary encrypted, without sender's public and private key`() {
        val exception = assertThrows(EncryptionException::class.java) {
            val paymentAckParameters = PaymentAckParameters(
                payment = TestData.Payment.PAYMENT,
                memo = TestData.Payment.MEMO,
                messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
                senderParameters = TestData.Senders.SENDER_PKI_X509SHA256,
                recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
            )

            paymentAckProcessor.create(paymentAckParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate PaymentAckBinary encrypted, without recipient's public key`() {
        val exception = assertThrows(EncryptionException::class.java) {
            val paymentAckParameters = PaymentAckParameters(
                payment = TestData.Payment.PAYMENT,
                memo = TestData.Payment.MEMO,
                messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
                senderParameters = TestData.Senders.SENDER_PKI_X509SHA256,
                recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS
            )

            paymentAckProcessor.create(paymentAckParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse PaymentAckBinary encrypted to PaymentAck`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = TestData.Payment.PAYMENT,
            memo = TestData.Payment.MEMO_PAYMENT_ACK,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = paymentAckProcessor.create(paymentAckParameters)
        val paymentAck = paymentAckProcessor.parse(
            paymentBinary,
            TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        assert(paymentAck.payment.merchantData == TestData.Payment.PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == TestData.Payment.PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == TestData.Payment.PAYMENT.outputs)
        assert(paymentAck.payment.originators.size == TestData.Payment.PAYMENT.originators.size)
        assert(paymentAck.payment.memo == TestData.Payment.PAYMENT.memo)
        assert(paymentAck.payment.protocolMessageMetadata == null)
        assert(paymentAck.memo == TestData.Payment.MEMO_PAYMENT_ACK)
        assert(!paymentAck.protocolMessageMetadata.identifier.isBlank())
        assert(paymentAck.protocolMessageMetadata.version == 1L)
        assert(paymentAck.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(paymentAck.protocolMessageMetadata.messageType == MessageType.PAYMENT_ACK)
        assertTrue(paymentAck.protocolMessageMetadata.encrypted)
        assert(!paymentAck.protocolMessageMetadata.encryptedMessage.isNullOrBlank())
        assert(!paymentAck.protocolMessageMetadata.recipientPublicKeyPem.isNullOrBlank())
        assert(!paymentAck.protocolMessageMetadata.senderPublicKeyPem.isNullOrBlank())
        assert(!paymentAck.protocolMessageMetadata.signature.isNullOrBlank())
        assert(paymentAck.protocolMessageMetadata.nonce!! > 0L)
        assertTrue(paymentAck.payment.beneficiaries.size == TestData.Payment.PAYMENT.beneficiaries.size)
    }


    @Test
    fun `Test parseWithAddressInfo not implemented`() {
        assertThrows(NotImplementedError::class.java) {
            paymentAckProcessor.parseWithAddressesInfo("test".toByteArray())
        }
    }
}
