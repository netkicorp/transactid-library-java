package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.EncryptionException
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.MessageType
import com.netki.model.PaymentParameters
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
internal class PaymentProcessorTest {

    private lateinit var mockAddressInformationService: AddressInformationService
    private val certificateValidator = CertificateValidator("src/test/resources/certificates")
    private lateinit var paymentProcessor: PaymentProcessor

    @BeforeAll
    fun setUp() {
        mockAddressInformationService = Mockito.mock(AddressInformationService::class.java)
        paymentProcessor = PaymentProcessor(mockAddressInformationService, certificateValidator)
    }


    @Test
    fun `Create and validate PaymentBinary`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries
        )

        val paymentBinary = paymentProcessor.create(paymentParameters)

        assert(paymentProcessor.isValid(paymentBinary))
    }

    @Test
    fun `Create and parse PaymentBinary to Payment`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries
        )

        val paymentBinary = paymentProcessor.create(paymentParameters)
        val payment = paymentProcessor.parse(paymentBinary)

        assert(payment.merchantData == paymentParameters.merchantData)
        assert(payment.transactions.size == paymentParameters.transactions.size)
        assert(payment.outputs == paymentParameters.outputs)
        assert(payment.memo == paymentParameters.memo)
        assert(payment.originators.size == originators.size)
        assert(payment.beneficiaries.size == beneficiaries.size)
        assert(!payment.protocolMessageMetadata!!.identifier.isBlank())
        assert(payment.protocolMessageMetadata?.version == 1L)
        assert(payment.protocolMessageMetadata?.statusCode == StatusCode.OK)
        assert(payment.protocolMessageMetadata?.statusMessage.isNullOrBlank())
        assert(payment.protocolMessageMetadata?.messageType == MessageType.PAYMENT)
    }

    @Test
    fun `Create and parse PaymentBinary to Payment with message information`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL
        )
        val paymentBinary = paymentProcessor.create(paymentParameters)
        val payment = paymentProcessor.parse(paymentBinary)

        assert(payment.merchantData == paymentParameters.merchantData)
        assert(payment.transactions.size == paymentParameters.transactions.size)
        assert(payment.outputs == paymentParameters.outputs)
        assert(payment.memo == paymentParameters.memo)
        assert(payment.originators.size == originators.size)
        assert(payment.beneficiaries.isEmpty())
        assert(!payment.protocolMessageMetadata!!.identifier.isBlank())
        assert(payment.protocolMessageMetadata?.version == 1L)
        assert(payment.protocolMessageMetadata?.statusCode == StatusCode.CANCEL)
        assert(payment.protocolMessageMetadata?.statusMessage == TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(payment.protocolMessageMetadata?.messageType == MessageType.PAYMENT)
    }

    @Test
    fun `Create and validate PaymentBinary Encrypted, Owners and Sender with PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = paymentProcessor.create(paymentParameters)

        assertTrue(paymentProcessor.isValid(paymentBinary, TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
    }

    @Test
    fun `Create and validate PaymentBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = paymentProcessor.create(paymentParameters)

        assertThrows(EncryptionException::class.java) {
            paymentProcessor.isValid(paymentBinary)
        }
    }

    @Test
    fun `Create and validate PaymentBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = paymentProcessor.create(paymentParameters)

        val protocolMessageCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(paymentBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(
                paymentProcessor.isValid(
                    protocolMessageCorrupted,
                    TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
                )
            )
        }

        assert(exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate PaymentBinary encrypted, without sender's public and private key`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            paymentProcessor.create(paymentParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate PaymentBinary encrypted, without recipient's public key`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender
        )

        val exception = assertThrows(EncryptionException::class.java) {
            paymentProcessor.create(paymentParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse PaymentBinary encrypted to Payment`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Payment.Output.OUTPUTS,
            memo = TestData.Payment.MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = paymentProcessor.create(paymentParameters)
        val payment = paymentProcessor.parse(paymentBinary, TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION)

        assert(payment.merchantData == paymentParameters.merchantData)
        assert(payment.transactions.size == paymentParameters.transactions.size)
        assert(payment.outputs == paymentParameters.outputs)
        assert(payment.memo == paymentParameters.memo)
        assert(payment.originators.size == originators.size)
        assert(payment.beneficiaries.size == beneficiaries.size)
        assert(!payment.protocolMessageMetadata?.identifier!!.isBlank())
        assert(payment.protocolMessageMetadata?.version == 1L)
        assert(payment.protocolMessageMetadata?.statusCode == StatusCode.OK)
        assert(payment.protocolMessageMetadata?.messageType == MessageType.PAYMENT)
        assertTrue(payment.protocolMessageMetadata?.encrypted!!)
        assert(!payment.protocolMessageMetadata?.encryptedMessage.isNullOrBlank())
        assert(!payment.protocolMessageMetadata?.recipientPublicKeyPem.isNullOrBlank())
        assert(!payment.protocolMessageMetadata?.senderPublicKeyPem.isNullOrBlank())
        assert(!payment.protocolMessageMetadata?.signature.isNullOrBlank())
        assert(payment.protocolMessageMetadata?.nonce!! > 0L)
    }

    @Test
    fun `Validate invalid PaymentBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            paymentProcessor.isValid("fakePaymentBinary".toByteArray())
        }
    }
}
