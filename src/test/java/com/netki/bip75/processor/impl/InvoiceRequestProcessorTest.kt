package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.*
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.util.*
import com.netki.util.TestData.any
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InvoiceRequestProcessorTest {

    private lateinit var mockAddressInformationService: AddressInformationService
    private val certificateValidator = CertificateValidator("src/test/resources/certificates")
    private lateinit var invoiceRequestProcessor: InvoiceRequestProcessor

    @BeforeAll
    fun setUp() {
        mockAddressInformationService = Mockito.mock(AddressInformationService::class.java)
        invoiceRequestProcessor = InvoiceRequestProcessor(mockAddressInformationService, certificateValidator)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,

            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )

        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owner and Sender with PkiData and bundle certificate`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and bundle certificate`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender without PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_NONE,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_NONE
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_NONE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_NONE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_NONE,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_INVALID_CERTIFICATE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
        }

        assert(exception.message == ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData but invalid certificate chain and Sender with PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256_INVALID_CERTIFICATE,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(invoiceRequestProcessor.isValid(invoiceRequestBinary))
        }

        assert(exception.message == ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(TestData.Attestations.INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and invalid Sender signature`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val invoiceRequestCorrupted = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary.getSerializedMessage(false))
            .setMemo("Memo changed!!")
            .build()
            .toByteArray()

        val protocolMessageCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.INVOICE_REQUEST)
            .setSerializedMessage(invoiceRequestCorrupted.toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(invoiceRequestProcessor.isValid(protocolMessageCorrupted))
        }

        assert(
            exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE
        )
    }

    @Test
    fun `Create and validate InvoiceRequestBinary Encrypted, Owners and Sender with PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assertTrue(
            invoiceRequestProcessor.isValid(
                invoiceRequestBinary,
                TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
            )
        )
    }

    @Test
    fun `Create and validate InvoiceRequestBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        assertThrows(EncryptionException::class.java) {
            invoiceRequestProcessor.isValid(invoiceRequestBinary)
        }
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val protocolMessageCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(invoiceRequestBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(
                invoiceRequestProcessor.isValid(
                    protocolMessageCorrupted,
                    TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
                )
            )
        }

        assert(exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary encrypted, without sender's public and private key`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            invoiceRequestProcessor.create(invoiceRequestParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary encrypted, without recipient's public key`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            invoiceRequestProcessor.create(invoiceRequestParameters)
        }

        assert(exception.message == ErrorInformation.ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse InvoiceRequestBinary encrypted to InvoiceRequest`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val invoiceRequest = invoiceRequestProcessor.parse(
            invoiceRequestBinary,
            TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        assert(invoiceRequestParameters.amount == invoiceRequest.amount)
        assert(invoiceRequestParameters.memo == invoiceRequest.memo)
        assert(invoiceRequestParameters.notificationUrl == invoiceRequest.notificationUrl)
        assert(TestData.Attestations.REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(TestData.Payment.Output.OUTPUTS.size == invoiceRequest.originatorsAddresses.size)

        assert(invoiceRequest.originators.size == 2)
        assert(invoiceRequest.beneficiaries.size == 1)
        invoiceRequest.originators.forEachIndexed { index, originator ->
            assert(originator.isPrimaryForTransaction == originators[index].isPrimaryForTransaction)
            originator.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData =
                    TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (originator.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters?.type == invoiceRequest.senderPkiType)
        assert(sender.pkiDataParameters?.certificatePem == invoiceRequest.senderPkiData)
        assert(!invoiceRequest.senderSignature.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.identifier.isBlank())
        assert(invoiceRequest.protocolMessageMetadata.version == 1L)
        assert(invoiceRequest.protocolMessageMetadata.messageType == MessageType.INVOICE_REQUEST)
        assertTrue(invoiceRequest.protocolMessageMetadata.encrypted)
        assert(!invoiceRequest.protocolMessageMetadata.encryptedMessage.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.recipientPublicKeyPem.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.senderPublicKeyPem.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.signature.isNullOrBlank())
        assert(invoiceRequest.protocolMessageMetadata.nonce!! > 0L)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and invalid Owner signature`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_NONE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val invoiceRequestCorrupted = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary.getSerializedMessage(false))

        val ownersWithInvalidSignature = mutableListOf<Messages.Originator>()
        invoiceRequestCorrupted.originatorsList.forEachIndexed { index, originator ->
            val ownerWithoutSignaturesBuilder = Messages.Originator.newBuilder()
                .mergeFrom(originator)
            originator.attestationsList.forEachIndexed { attestationIndex, attestation ->
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

        invoiceRequestCorrupted.clearOriginators()
        invoiceRequestCorrupted.addAllOriginators(ownersWithInvalidSignature)

        val protocolMessageCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.INVOICE_REQUEST)
            .setSerializedMessage(invoiceRequestCorrupted.build().toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(invoiceRequestProcessor.isValid(protocolMessageCorrupted))
        }

        assert(exception.message == ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(TestData.Attestations.INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and parse InvoiceRequestBinary to InvoiceRequest`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val invoiceRequest = invoiceRequestProcessor.parse(invoiceRequestBinary)

        assert(invoiceRequestParameters.amount == invoiceRequest.amount)
        assert(invoiceRequestParameters.memo == invoiceRequest.memo)
        assert(invoiceRequestParameters.notificationUrl == invoiceRequest.notificationUrl)
        assert(TestData.Attestations.REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(TestData.Payment.Output.OUTPUTS.size == invoiceRequest.originatorsAddresses.size)

        assert(invoiceRequest.originators.size == 2)
        assert(invoiceRequest.beneficiaries.size == 1)
        invoiceRequest.originators.forEachIndexed { index, originator ->
            assert(originator.isPrimaryForTransaction == originators[index].isPrimaryForTransaction)
            originator.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData =
                    TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (originator.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters?.type == invoiceRequest.senderPkiType)
        assert(sender.pkiDataParameters?.certificatePem == invoiceRequest.senderPkiData)
        assert(!invoiceRequest.senderSignature.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.identifier.isBlank())
        assert(invoiceRequest.protocolMessageMetadata.version == 1L)
        assert(invoiceRequest.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(invoiceRequest.protocolMessageMetadata.statusMessage.isEmpty())
        assert(invoiceRequest.protocolMessageMetadata.messageType == MessageType.INVOICE_REQUEST)
        assert(!invoiceRequest.senderEvCert.isNullOrBlank())
    }

    @Test
    fun `Create and parse InvoiceRequestBinary to InvoiceRequest with message information`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256

        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            messageInformation = TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestParameters)

        val invoiceRequest = invoiceRequestProcessor.parse(invoiceRequestBinary)

        assert(invoiceRequestParameters.amount == invoiceRequest.amount)
        assert(invoiceRequestParameters.memo == invoiceRequest.memo)
        assert(invoiceRequestParameters.notificationUrl == invoiceRequest.notificationUrl)
        assert(TestData.Attestations.REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(TestData.Payment.Output.OUTPUTS.size == invoiceRequest.originatorsAddresses.size)

        assert(invoiceRequest.originators.size == 2)
        assert(invoiceRequest.beneficiaries.isEmpty())
        invoiceRequest.originators.forEachIndexed { index, originator ->
            assert(originator.isPrimaryForTransaction == originators[index].isPrimaryForTransaction)
            originator.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData =
                    TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (originator.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters?.type == invoiceRequest.senderPkiType)
        assert(sender.pkiDataParameters?.certificatePem == invoiceRequest.senderPkiData)
        assert(!invoiceRequest.senderSignature.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.identifier.isBlank())
        assert(invoiceRequest.protocolMessageMetadata.version == 1L)
        assert(invoiceRequest.protocolMessageMetadata.statusCode == StatusCode.CANCEL)
        assert(invoiceRequest.protocolMessageMetadata.statusMessage == TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(invoiceRequest.protocolMessageMetadata.messageType == MessageType.INVOICE_REQUEST)
        assertFalse(invoiceRequest.protocolMessageMetadata.encrypted)
    }

    @Test
    fun `Validate invalid InvoiceRequestBinary`() {
        val exception = assertThrows(InvalidObjectException::class.java) {
            invoiceRequestProcessor.isValid("fakeInvoiceRequest".toByteArray())
        }

        assert(exception.message?.contains("Invalid object for") ?: false)
    }

    @Test
    fun `Create and parse InvoiceRequest binary and fetch AddressInformation`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(TestData.Address.ADDRESS_INFORMATION)

        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestData)

        val invoiceRequest = invoiceRequestProcessor.parseWithAddressesInfo(invoiceRequestBinary)

        assertEquals(invoiceRequest.originatorsAddresses.size, invoiceRequestData.originatorsAddresses.size)
        assertTrue(invoiceRequest.recipientChainAddress.isNullOrBlank())
        assertTrue(invoiceRequest.recipientVaspName.isNullOrBlank())
        invoiceRequest.originatorsAddresses.forEach { output ->
            run {
                assert(!output.addressInformation?.identifier.isNullOrBlank())
                assertNotNull(output.addressInformation?.alerts)
                assert(!output.addressInformation?.currencyVerbose.isNullOrBlank())
                assert(!output.addressInformation?.earliestTransactionTime.isNullOrBlank())
                assert(!output.addressInformation?.latestTransactionTime.isNullOrBlank())
            }
        }
    }

    @Test
    fun `Create and parse InvoiceRequest binary and fetch AddressInformation for no existing address`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(AddressInformation())

        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestData)

        val invoiceRequest = invoiceRequestProcessor.parseWithAddressesInfo(invoiceRequestBinary)

        assertEquals(invoiceRequest.originatorsAddresses.size, invoiceRequestData.originatorsAddresses.size)
        invoiceRequest.originatorsAddresses.forEach { output ->
            run {
                assert(output.addressInformation?.identifier.isNullOrBlank())
                assert(output.addressInformation?.currencyVerbose.isNullOrBlank())
                assert(output.addressInformation?.earliestTransactionTime.isNullOrBlank())
                assert(output.addressInformation?.latestTransactionTime.isNullOrBlank())
            }
        }
    }

    @Test
    fun `Create and parse InvoiceRequest binary and fetch AddressInformation throwing Unauthorized error`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
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

        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )
        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestData)

        val exception = assertThrows(AddressProviderUnauthorizedException::class.java) {
            invoiceRequestProcessor.parseWithAddressesInfo(invoiceRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider authorization error for address:"))
    }

    @Test
    fun `Create and parse InvoiceRequest binary and fetch AddressInformation throwing error from the address provider`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
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

        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )
        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestData)

        val exception = assertThrows(AddressProviderErrorException::class.java) {
            invoiceRequestProcessor.parseWithAddressesInfo(invoiceRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }

    @Test
    fun `Create and parse InvoiceRequest binary with recipient information and fetch AddressInformation`() {
        Mockito.`when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(TestData.Address.ADDRESS_INFORMATION)

        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS,
            recipientParameters = TestData.Recipients.RECIPIENTS_PARAMETERS
        )
        val invoiceRequestBinary = invoiceRequestProcessor.create(invoiceRequestData)

        val invoiceRequest = invoiceRequestProcessor.parseWithAddressesInfo(invoiceRequestBinary)

        assertEquals(invoiceRequest.originatorsAddresses.size, invoiceRequestData.originatorsAddresses.size)
        assertEquals(invoiceRequest.recipientChainAddress, TestData.Recipients.RECIPIENTS_PARAMETERS.chainAddress)
        assertEquals(invoiceRequest.recipientVaspName, TestData.Recipients.RECIPIENTS_PARAMETERS.vaspName)
        invoiceRequest.originatorsAddresses.forEach { output ->
            run {
                assert(!output.addressInformation?.identifier.isNullOrBlank())
                assertNotNull(output.addressInformation?.alerts)
                assert(!output.addressInformation?.currencyVerbose.isNullOrBlank())
                assert(!output.addressInformation?.earliestTransactionTime.isNullOrBlank())
                assert(!output.addressInformation?.latestTransactionTime.isNullOrBlank())
            }
        }
    }
}
