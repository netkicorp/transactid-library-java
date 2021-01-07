package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.*
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.sql.Timestamp

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PaymentRequestProcessorTest {

    private lateinit var mockAddressInformationService: AddressInformationService
    private val certificateValidator = CertificateValidator("src/test/resources/certificates")
    private lateinit var paymentRequestProcessor: PaymentRequestProcessor

    @BeforeAll
    fun setUp() {
        mockAddressInformationService = Mockito.mock(AddressInformationService::class.java)
        paymentRequestProcessor = PaymentRequestProcessor(mockAddressInformationService, certificateValidator)
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assert(paymentRequestProcessor.isValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owner and Sender with PkiData bundle`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assert(paymentRequestProcessor.isValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData bundle`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assert(paymentRequestProcessor.isValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender without PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_NONE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assert(paymentRequestProcessor.isValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_NONE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assert(paymentRequestProcessor.isValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assert(paymentRequestProcessor.isValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assert(paymentRequestProcessor.isValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_INVALID_CERTIFICATE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(paymentRequestProcessor.isValid(paymentRequestBinary))
        }

        assert(exception.message == ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData but invalid certificate chain and Sender with PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_INVALID_CERTIFICATE,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(paymentRequestProcessor.isValid(paymentRequestBinary))
        }

        assert(exception.message == ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(TestData.Attestations.INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData and invalid Sender signature`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary.getSerializedMessage(false))
            .setPaymentDetailsVersion(4)
            .build()
            .toByteArray()

        val protocolMessageCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.PAYMENT_REQUEST)
            .setSerializedMessage(paymentRequestCorrupted.toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(paymentRequestProcessor.isValid(protocolMessageCorrupted))
        }

        assert(
            exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE
        )
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData and invalid Owner signature`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_NONE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary.getSerializedMessage(false))

        val ownersWithInvalidSignature = mutableListOf<Messages.Beneficiary>()
        paymentRequestCorrupted.beneficiariesList.forEachIndexed { index, beneficiary ->
            val ownerWithoutSignaturesBuilder = Messages.Beneficiary.newBuilder()
                .mergeFrom(beneficiary)
            beneficiary.attestationsList.forEachIndexed { attestationIndex, attestation ->
                ownerWithoutSignaturesBuilder.removeAttestations(attestationIndex)
                ownerWithoutSignaturesBuilder.addAttestations(
                    attestationIndex, Messages.Attestation.newBuilder()
                        .mergeFrom(attestation)
                        .setAttestation(TestData.Attestations.INVALID_ATTESTATION.toAttestationType())
                )
                    .build()
            }
            ownersWithInvalidSignature.add(index, ownerWithoutSignaturesBuilder.build())
        }

        paymentRequestCorrupted.clearBeneficiaries()
        paymentRequestCorrupted.addAllBeneficiaries(ownersWithInvalidSignature)


        val protocolMessageCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.PAYMENT_REQUEST)
            .setSerializedMessage(paymentRequestCorrupted.build().toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()


        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(paymentRequestProcessor.isValid(protocolMessageCorrupted))
        }

        assert(exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(TestData.Attestations.INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and parse PaymentRequestBinary to PaymentRequest`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val paymentRequest = paymentRequestProcessor.parse(paymentRequestBinary)

        assert(paymentRequest.network == paymentRequestParameters.network)
        assert(paymentRequest.time == paymentRequestParameters.time)
        assert(paymentRequest.expires == paymentRequestParameters.expires)
        assert(paymentRequest.memo == paymentRequestParameters.memo)
        assert(paymentRequest.paymentUrl == paymentRequestParameters.paymentUrl)
        assert(paymentRequest.merchantData == paymentRequestParameters.merchantData)
        assert(paymentRequest.beneficiariesAddresses.size == paymentRequestParameters.beneficiariesAddresses.size)

        assert(paymentRequest.beneficiaries.size == 2)
        paymentRequest.beneficiaries.forEachIndexed { index, beneficiary ->
            assert(beneficiary.isPrimaryForTransaction == beneficiaries[index].isPrimaryForTransaction)
            beneficiary.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData =
                    TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (beneficiary.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters?.type == paymentRequest.senderPkiType)
        assert(sender.pkiDataParameters?.certificatePem == paymentRequest.senderPkiData)
        assert(!paymentRequest.senderSignature.isNullOrBlank())
        assert(!paymentRequest.protocolMessageMetadata.identifier.isBlank())
        assert(paymentRequest.protocolMessageMetadata.version == 1L)
        assert(paymentRequest.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(paymentRequest.protocolMessageMetadata.statusMessage.isEmpty())
        assert(paymentRequest.protocolMessageMetadata.messageType == MessageType.PAYMENT_REQUEST)
    }

    @Test
    fun `Create and parse PaymentRequestBinary to PaymentRequest with message information`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val paymentRequest = paymentRequestProcessor.parse(paymentRequestBinary)

        assert(paymentRequest.network == paymentRequestParameters.network)
        assert(paymentRequest.beneficiariesAddresses == paymentRequestParameters.beneficiariesAddresses)
        assert(paymentRequest.time == paymentRequestParameters.time)
        assert(paymentRequest.expires == paymentRequestParameters.expires)
        assert(paymentRequest.memo == paymentRequestParameters.memo)
        assert(paymentRequest.paymentUrl == paymentRequestParameters.paymentUrl)
        assert(paymentRequest.merchantData == paymentRequestParameters.merchantData)
        assert(paymentRequest.beneficiariesAddresses.size == paymentRequestParameters.beneficiariesAddresses.size)

        assert(paymentRequest.beneficiaries.size == 2)
        paymentRequest.beneficiaries.forEachIndexed { index, beneficiary ->
            assert(beneficiary.isPrimaryForTransaction == beneficiaries[index].isPrimaryForTransaction)
            beneficiary.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData =
                    TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (beneficiary.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters?.type == paymentRequest.senderPkiType)
        assert(sender.pkiDataParameters?.certificatePem == paymentRequest.senderPkiData)
        assert(!paymentRequest.senderSignature.isNullOrBlank())
        assert(!paymentRequest.protocolMessageMetadata.identifier.isBlank())
        assert(paymentRequest.protocolMessageMetadata.version == 1L)
        assert(paymentRequest.protocolMessageMetadata.statusCode == StatusCode.CANCEL)
        assert(paymentRequest.protocolMessageMetadata.statusMessage == TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(paymentRequest.protocolMessageMetadata.messageType == MessageType.PAYMENT_REQUEST)

    }

    @Test
    fun `Validate invalid PaymentRequestBinary`() {
        val exception = assertThrows(InvalidObjectException::class.java) {
            paymentRequestProcessor.isValid("fakePaymentRequest".toByteArray())
        }

        assert(exception.message?.contains("Invalid object for") ?: false)
    }

    @Test
    fun `Create and validate PaymentRequestBinary Encrypted, Owners and Sender with PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assertTrue(
            paymentRequestProcessor.isValid(
                paymentRequestBinary,
                TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
            )
        )
    }

    @Test
    fun `Create and validate PaymentRequestBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        assertThrows(EncryptionException::class.java) {
            paymentRequestProcessor.isValid(paymentRequestBinary)
        }
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val protocolMessageCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(paymentRequestBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(
                paymentRequestProcessor.isValid(
                    protocolMessageCorrupted,
                    TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
                )
            )
        }

        assert(exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate PaymentRequestBinary encrypted, without sender's public and private key`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            paymentRequestProcessor.create(paymentRequestParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate PaymentRequestBinary encrypted, without recipient's public key`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            paymentRequestProcessor.create(paymentRequestParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse PaymentRequestBinary encrypted to PaymentRequest`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val paymentRequest = paymentRequestProcessor.parse(
            paymentRequestBinary,
            TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        assert(paymentRequest.network == paymentRequestParameters.network)
        assert(paymentRequest.beneficiariesAddresses == paymentRequestParameters.beneficiariesAddresses)
        assert(paymentRequest.time == paymentRequestParameters.time)
        assert(paymentRequest.expires == paymentRequestParameters.expires)
        assert(paymentRequest.memo == paymentRequestParameters.memo)
        assert(paymentRequest.paymentUrl == paymentRequestParameters.paymentUrl)
        assert(paymentRequest.merchantData == paymentRequestParameters.merchantData)
        assert(paymentRequest.beneficiariesAddresses.size == paymentRequestParameters.beneficiariesAddresses.size)

        assert(paymentRequest.beneficiaries.size == 2)
        paymentRequest.beneficiaries.forEachIndexed { index, beneficiary ->
            assert(beneficiary.isPrimaryForTransaction == beneficiaries[index].isPrimaryForTransaction)
            beneficiary.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData =
                    TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (beneficiary.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters?.type == paymentRequest.senderPkiType)
        assert(sender.pkiDataParameters?.certificatePem == paymentRequest.senderPkiData)
        assert(!paymentRequest.senderSignature.isNullOrBlank())
        assert(!paymentRequest.protocolMessageMetadata.identifier.isBlank())
        assert(paymentRequest.protocolMessageMetadata.version == 1L)
        assert(paymentRequest.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(paymentRequest.protocolMessageMetadata.statusMessage.isEmpty())
        assert(paymentRequest.protocolMessageMetadata.messageType == MessageType.PAYMENT_REQUEST)
        assertTrue(paymentRequest.protocolMessageMetadata.encrypted)
        assert(!paymentRequest.protocolMessageMetadata.encryptedMessage.isNullOrBlank())
        assert(!paymentRequest.protocolMessageMetadata.recipientPublicKeyPem.isNullOrBlank())
        assert(!paymentRequest.protocolMessageMetadata.senderPublicKeyPem.isNullOrBlank())
        assert(!paymentRequest.protocolMessageMetadata.signature.isNullOrBlank())
        assert(paymentRequest.protocolMessageMetadata.nonce!! > 0L)
    }


    @Test
    fun `Create and parse PaymentRequest binary and fetch AddressInformation`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                TestData.any(AddressCurrency::class.java),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(TestData.Address.ADDRESS_INFORMATION)

        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val paymentRequest = paymentRequestProcessor.parseWithAddressesInfo(paymentRequestBinary)

        assertEquals(paymentRequest.beneficiariesAddresses.size, paymentRequestParameters.beneficiariesAddresses.size)
        paymentRequest.beneficiariesAddresses.forEach { output ->
            run {
                assert(!output.addressInformation?.identifier.isNullOrBlank())
                assert(!output.addressInformation?.currencyVerbose.isNullOrBlank())
                assert(!output.addressInformation?.earliestTransactionTime.isNullOrBlank())
                assert(!output.addressInformation?.latestTransactionTime.isNullOrBlank())
            }
        }
    }

    @Test
    fun `Create and parse PaymentRequest binary and fetch AddressInformation for no existing address`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                TestData.any(AddressCurrency::class.java),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(AddressInformation())

        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )
        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val paymentRequest = paymentRequestProcessor.parseWithAddressesInfo(paymentRequestBinary)

        assertEquals(paymentRequest.beneficiariesAddresses.size, paymentRequestParameters.beneficiariesAddresses.size)
        paymentRequest.beneficiariesAddresses.forEach { output ->
            run {
                assert(output.addressInformation?.identifier.isNullOrBlank())
                assert(output.addressInformation?.currencyVerbose.isNullOrBlank())
                assert(output.addressInformation?.earliestTransactionTime.isNullOrBlank())
                assert(output.addressInformation?.latestTransactionTime.isNullOrBlank())
            }
        }
    }

    @Test
    fun `Create and parse PaymentRequest binary and fetch AddressInformation throwing Unauthorized error`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                TestData.any(AddressCurrency::class.java),
                ArgumentMatchers.anyString()
            )
        ).thenThrow(
            AddressProviderUnauthorizedException(
                String.format(
                    ErrorInformation.ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER,
                    "test_address"
                )
            )
        )

        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val exception = assertThrows(AddressProviderUnauthorizedException::class.java) {
            paymentRequestProcessor.parseWithAddressesInfo(paymentRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider authorization error for address:"))
    }

    @Test
    fun `Create and parse PaymentRequest binary and fetch AddressInformation throwing error from the address provider`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                TestData.any(AddressCurrency::class.java),
                ArgumentMatchers.anyString()
            )
        ).thenThrow(
            AddressProviderErrorException(
                String.format(
                    ErrorInformation.ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER,
                    "test_address",
                    "Runtime error"
                )
            )
        )

        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Payment.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = paymentRequestProcessor.create(paymentRequestParameters)

        val exception = assertThrows(AddressProviderErrorException::class.java) {
            paymentRequestProcessor.parseWithAddressesInfo(paymentRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }
}
