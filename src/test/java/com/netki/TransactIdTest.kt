package com.netki

import com.netki.bip75.protocol.Messages
import com.netki.exceptions.EncryptionException
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR
import com.netki.util.ErrorInformation.ENCRYPTION_MISSING_SENDER_KEYS_ERROR
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE
import com.netki.util.TestData
import com.netki.util.TestData.Attestations.INVALID_ATTESTATION
import com.netki.util.TestData.Attestations.REQUESTED_ATTESTATIONS
import com.netki.util.TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_NONE
import com.netki.util.TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL
import com.netki.util.TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
import com.netki.util.TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_NONE
import com.netki.util.TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_NONE
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256_BUNDLED_CERTIFICATE
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.Payment.MEMO
import com.netki.util.TestData.Payment.MEMO_PAYMENT_ACK
import com.netki.util.TestData.Payment.Output.OUTPUTS
import com.netki.util.TestData.Payment.PAYMENT
import com.netki.util.TestData.Recipients.RECIPIENTS_PARAMETERS
import com.netki.util.TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
import com.netki.util.TestData.Senders.SENDER_PKI_NONE
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
import com.netki.util.getSerializedMessage
import com.netki.util.toAttestationType
import com.netki.util.toByteString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Timestamp

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TransactIdTest {

    private val transactId = TransactId.getInstance("src/main/resources/certificates")
    private val pairKey = TestData.Keys.generateKeyPairECDSA()

    @BeforeAll
    fun setUp() {
        // Nothing to do here
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,

            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )

        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owner and Sender with PkiData and bundle certificate`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and bundle certificate`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender without PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_NONE,
            NO_PRIMARY_ORIGINATOR_PKI_NONE
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_NONE,
            NO_PRIMARY_ORIGINATOR_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_INVALID_CERTIFICATE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData but invalid certificate chain and Sender with PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256_INVALID_CERTIFICATE,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and invalid Sender signature`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        val invoiceRequestCorrupted = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary.getSerializedMessage(false))
            .setMemo("Memo changed!!")
            .build()
            .toByteArray()

        val messageProtocolCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.INVOICE_REQUEST)
            .setSerializedMessage(invoiceRequestCorrupted.toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isInvoiceRequestValid(messageProtocolCorrupted))
        }

        assert(
            exception.message == SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE
        )
    }

    @Test
    fun `Create and validate InvoiceRequestBinary Encrypted, Owners and Sender with PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assertTrue(transactId.isInvoiceRequestValid(invoiceRequestBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        assertThrows(EncryptionException::class.java) {
            transactId.isInvoiceRequestValid(invoiceRequestBinary)
        }
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        val messageProtocolCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(invoiceRequestBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isInvoiceRequestValid(messageProtocolCorrupted, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
        }

        assert(exception.message == SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary encrypted, without sender's public and private key`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            transactId.createInvoiceRequest(invoiceRequestParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary encrypted, without recipient's public key`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            recipientParameters = RECIPIENTS_PARAMETERS,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            transactId.createInvoiceRequest(invoiceRequestParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse InvoiceRequestBinary encrypted to InvoiceRequest`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        val invoiceRequest = transactId.parseInvoiceRequest(invoiceRequestBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION)

        assert(invoiceRequestParameters.amount == invoiceRequest.amount)
        assert(invoiceRequestParameters.memo == invoiceRequest.memo)
        assert(invoiceRequestParameters.notificationUrl == invoiceRequest.notificationUrl)
        assert(REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(OUTPUTS.size == invoiceRequest.originatorsAddresses.size)

        assert(invoiceRequest.originators.size == 2)
        assert(invoiceRequest.beneficiaries.size == 1)
        invoiceRequest.originators.forEachIndexed { index, originator ->
            assert(originator.isPrimaryForTransaction == originators[index].isPrimaryForTransaction)
            originator.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_ORIGINATOR_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
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
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

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
                        .setAttestation(INVALID_ATTESTATION.toAttestationType())
                )
                    .build()
            }
            ownersWithInvalidSignature.add(index, ownerWithoutSignaturesBuilder.build())
        }

        invoiceRequestCorrupted.clearOriginators()
        invoiceRequestCorrupted.addAllOriginators(ownersWithInvalidSignature)

        val messageProtocolCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.INVOICE_REQUEST)
            .setSerializedMessage(invoiceRequestCorrupted.build().toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isInvoiceRequestValid(messageProtocolCorrupted))
        }

        assert(exception.message == SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and parse InvoiceRequestBinary to InvoiceRequest`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        val invoiceRequest = transactId.parseInvoiceRequest(invoiceRequestBinary)

        assert(invoiceRequestParameters.amount == invoiceRequest.amount)
        assert(invoiceRequestParameters.memo == invoiceRequest.memo)
        assert(invoiceRequestParameters.notificationUrl == invoiceRequest.notificationUrl)
        assert(REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(OUTPUTS.size == invoiceRequest.originatorsAddresses.size)

        assert(invoiceRequest.originators.size == 2)
        assert(invoiceRequest.beneficiaries.size == 1)
        invoiceRequest.originators.forEachIndexed() { index, originator ->
            assert(originator.isPrimaryForTransaction == originators[index].isPrimaryForTransaction)
            originator.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_ORIGINATOR_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
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
    }


    @Test
    fun `Create and parse InvoiceRequestBinary to InvoiceRequest with message information`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_CANCEL
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestParameters)

        val invoiceRequest = transactId.parseInvoiceRequest(invoiceRequestBinary)

        assert(invoiceRequestParameters.amount == invoiceRequest.amount)
        assert(invoiceRequestParameters.memo == invoiceRequest.memo)
        assert(invoiceRequestParameters.notificationUrl == invoiceRequest.notificationUrl)
        assert(REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(OUTPUTS.size == invoiceRequest.originatorsAddresses.size)

        assert(invoiceRequest.originators.size == 2)
        assert(invoiceRequest.beneficiaries.isEmpty())
        invoiceRequest.originators.forEachIndexed() { index, originator ->
            assert(originator.isPrimaryForTransaction == originators[index].isPrimaryForTransaction)
            originator.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_ORIGINATOR_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
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
        assert(invoiceRequest.protocolMessageMetadata.statusMessage == MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(invoiceRequest.protocolMessageMetadata.messageType == MessageType.INVOICE_REQUEST)
        assertFalse(invoiceRequest.protocolMessageMetadata.encrypted)
    }

    @Test
    fun `Validate invalid InvoiceRequestBinary`() {
        val exception = assertThrows(InvalidObjectException::class.java) {
            transactId.isInvoiceRequestValid("fakeInvoiceRequest".toByteArray())
        }

        assert(exception.message?.contains("Invalid object for") ?: false)
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owner and Sender with PkiData bundle`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData bundle`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender without PkiData`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE,
            NO_PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_NONE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE,
            NO_PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_INVALID_CERTIFICATE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isPaymentRequestValid(paymentRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData but invalid certificate chain and Sender with PkiData`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256_INVALID_CERTIFICATE,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isPaymentRequestValid(paymentRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData and invalid Sender signature`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary.getSerializedMessage(false))
            .setPaymentDetailsVersion(4)
            .build()
            .toByteArray()

        val messageProtocolCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.PAYMENT_REQUEST)
            .setSerializedMessage(paymentRequestCorrupted.toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isPaymentRequestValid(messageProtocolCorrupted))
        }

        assert(
            exception.message == SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE
        )
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData and invalid Owner signature`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary.getSerializedMessage(false))

        val ownersWithInvalidSignature = mutableListOf<Messages.Beneficiary>()
        paymentRequestCorrupted.beneficiariesList.forEachIndexed { index, beneficiary ->
            val ownerWithoutSignaturesBuilder = Messages.Beneficiary.newBuilder()
                .mergeFrom(beneficiary)
            beneficiary.attestationsList.forEachIndexed() { attestationIndex, attestation ->
                ownerWithoutSignaturesBuilder.removeAttestations(attestationIndex)
                ownerWithoutSignaturesBuilder.addAttestations(
                    attestationIndex, Messages.Attestation.newBuilder()
                        .mergeFrom(attestation)
                        .setAttestation(INVALID_ATTESTATION.toAttestationType())
                )
                    .build()
            }
            ownersWithInvalidSignature.add(index, ownerWithoutSignaturesBuilder.build())
        }

        paymentRequestCorrupted.clearBeneficiaries()
        paymentRequestCorrupted.addAllBeneficiaries(ownersWithInvalidSignature)


        val messageProtocolCorrupted = Messages.ProtocolMessage.newBuilder()
            .setVersion(1)
            .setStatusCode(StatusCode.OK.code)
            .setMessageType(Messages.ProtocolMessageType.PAYMENT_REQUEST)
            .setSerializedMessage(paymentRequestCorrupted.build().toByteString())
            .setStatusMessage("test")
            .setIdentifier("random".toByteString())
            .build()
            .toByteArray()


        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isPaymentRequestValid(messageProtocolCorrupted))
        }

        assert(exception.message == SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE.format(INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and parse PaymentRequestBinary to PaymentRequest`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val paymentRequest = transactId.parsePaymentRequest(paymentRequestBinary)

        assert(paymentRequest.network == paymentRequestParameters.network)
        assert(paymentRequest.time == paymentRequestParameters.time)
        assert(paymentRequest.expires == paymentRequestParameters.expires)
        assert(paymentRequest.memo == paymentRequestParameters.memo)
        assert(paymentRequest.paymentUrl == paymentRequestParameters.paymentUrl)
        assert(paymentRequest.merchantData == paymentRequestParameters.merchantData)
        assert(paymentRequest.beneficiariesAddresses.size == paymentRequestParameters.beneficiariesAddresses.size)

        assert(paymentRequest.beneficiaries.size == 2)
        paymentRequest.beneficiaries.forEachIndexed() { index, beneficiary ->
            assert(beneficiary.isPrimaryForTransaction == beneficiaries[index].isPrimaryForTransaction)
            beneficiary.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_BENEFICIARY_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
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
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_CANCEL
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val paymentRequest = transactId.parsePaymentRequest(paymentRequestBinary)

        assert(paymentRequest.network == paymentRequestParameters.network)
        assert(paymentRequest.beneficiariesAddresses == paymentRequestParameters.beneficiariesAddresses)
        assert(paymentRequest.time == paymentRequestParameters.time)
        assert(paymentRequest.expires == paymentRequestParameters.expires)
        assert(paymentRequest.memo == paymentRequestParameters.memo)
        assert(paymentRequest.paymentUrl == paymentRequestParameters.paymentUrl)
        assert(paymentRequest.merchantData == paymentRequestParameters.merchantData)
        assert(paymentRequest.beneficiariesAddresses.size == paymentRequestParameters.beneficiariesAddresses.size)

        assert(paymentRequest.beneficiaries.size == 2)
        paymentRequest.beneficiaries.forEachIndexed() { index, beneficiary ->
            assert(beneficiary.isPrimaryForTransaction == beneficiaries[index].isPrimaryForTransaction)
            beneficiary.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_BENEFICIARY_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
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
        assert(paymentRequest.protocolMessageMetadata.statusMessage == MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(paymentRequest.protocolMessageMetadata.messageType == MessageType.PAYMENT_REQUEST)

    }

    @Test
    fun `Validate invalid PaymentRequestBinary`() {
        val exception = assertThrows(InvalidObjectException::class.java) {
            transactId.isPaymentRequestValid("fakePaymentRequest".toByteArray())
        }

        assert(exception.message?.contains("Invalid object for") ?: false)
    }

    @Test
    fun `Create and validate PaymentRequestBinary Encrypted, Owners and Sender with PkiData`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_CANCEL,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assertTrue(transactId.isPaymentRequestValid(paymentRequestBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
    }

    @Test
    fun `Create and validate PaymentRequestBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        assertThrows(EncryptionException::class.java) {
            transactId.isPaymentRequestValid(paymentRequestBinary)
        }
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val messageProtocolCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(paymentRequestBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isPaymentRequestValid(messageProtocolCorrupted, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
        }

        assert(exception.message == SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate PaymentRequestBinary encrypted, without sender's public and private key`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            transactId.createPaymentRequest(paymentRequestParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate PaymentRequestBinary encrypted, without recipient's public key`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            transactId.createPaymentRequest(paymentRequestParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse PaymentRequestBinary encrypted to PaymentRequest`() {
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentRequestBinary = transactId.createPaymentRequest(paymentRequestParameters)

        val paymentRequest = transactId.parsePaymentRequest(paymentRequestBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION)

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
                val ownerPkiData = PRIMARY_BENEFICIARY_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
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
    fun `Create and validate PaymentBinary`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries
        )

        val paymentBinary = transactId.createPayment(paymentParameters)

        assert(transactId.isPaymentValid(paymentBinary))
    }

    @Test
    fun `Create and parse PaymentBinary to Payment`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries
        )

        val paymentBinary = transactId.createPayment(paymentParameters)
        val payment = transactId.parsePayment(paymentBinary)

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
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            messageInformation = MESSAGE_INFORMATION_CANCEL
        )
        val paymentBinary = transactId.createPayment(paymentParameters)
        val payment = transactId.parsePayment(paymentBinary)

        assert(payment.merchantData == paymentParameters.merchantData)
        assert(payment.transactions.size == paymentParameters.transactions.size)
        assert(payment.outputs == paymentParameters.outputs)
        assert(payment.memo == paymentParameters.memo)
        assert(payment.originators.size == originators.size)
        assert(payment.beneficiaries.isEmpty())
        assert(!payment.protocolMessageMetadata!!.identifier.isBlank())
        assert(payment.protocolMessageMetadata?.version == 1L)
        assert(payment.protocolMessageMetadata?.statusCode == StatusCode.CANCEL)
        assert(payment.protocolMessageMetadata?.statusMessage == MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(payment.protocolMessageMetadata?.messageType == MessageType.PAYMENT)
    }

    @Test
    fun `Create and validate PaymentBinary Encrypted, Owners and Sender with PkiData`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = transactId.createPayment(paymentParameters)

        assertTrue(transactId.isPaymentValid(paymentBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
    }

    @Test
    fun `Create and validate PaymentBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = transactId.createPayment(paymentParameters)

        assertThrows(EncryptionException::class.java) {
            transactId.isPaymentValid(paymentBinary)
        }
    }

    @Test
    fun `Create and validate PaymentBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = transactId.createPayment(paymentParameters)

        val messageProtocolCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(paymentBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isPaymentValid(messageProtocolCorrupted, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
        }

        assert(exception.message == SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate PaymentBinary encrypted, without sender's public and private key`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = emptyList(),
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val exception = assertThrows(EncryptionException::class.java) {
            transactId.createPayment(paymentParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate PaymentBinary encrypted, without recipient's public key`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender
        )

        val exception = assertThrows(EncryptionException::class.java) {
            transactId.createPayment(paymentParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse PaymentBinary encrypted to Payment`() {
        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256_WITH_ENCRYPTION
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = sender,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = transactId.createPayment(paymentParameters)
        val payment = transactId.parsePayment(paymentBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION)

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
            transactId.isPaymentValid("fakePaymentBinary".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO
        )
        val paymentAckBinary = transactId.createPaymentAck(paymentAckParameters)

        assert(transactId.isPaymentAckValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO_PAYMENT_ACK
        )
        val paymentBinary = transactId.createPaymentAck(paymentAckParameters)
        val paymentAck = transactId.parsePaymentAck(paymentBinary)

        assert(paymentAck.payment.merchantData == PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == PAYMENT.outputs)
        assert(paymentAck.payment.originators.size == PAYMENT.originators.size)
        assert(paymentAck.payment.memo == PAYMENT.memo)
        assert(paymentAck.payment.protocolMessageMetadata == null)
        assert(paymentAck.memo == MEMO_PAYMENT_ACK)
        assert(!paymentAck.protocolMessageMetadata.identifier.isBlank())
        assert(paymentAck.protocolMessageMetadata.version == 1L)
        assert(paymentAck.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(paymentAck.protocolMessageMetadata.statusMessage.isEmpty())
        assert(paymentAck.protocolMessageMetadata.messageType == MessageType.PAYMENT_ACK)
    }

    @Test
    fun `Create and validate PaymentAckBinary with message information`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO
        )
        val paymentAckBinary = transactId.createPaymentAck(paymentAckParameters)

        assert(transactId.isPaymentAckValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck with message information`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO_PAYMENT_ACK,
            messageInformation = MESSAGE_INFORMATION_CANCEL
        )
        val paymentBinary = transactId.createPaymentAck(paymentAckParameters)
        val paymentAck = transactId.parsePaymentAck(paymentBinary)

        assert(paymentAck.payment.merchantData == PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == PAYMENT.outputs)
        assert(paymentAck.payment.originators.size == PAYMENT.originators.size)
        assert(paymentAck.payment.memo == PAYMENT.memo)
        assert(paymentAck.payment.protocolMessageMetadata == null)
        assert(paymentAck.memo == MEMO_PAYMENT_ACK)
        assert(!paymentAck.protocolMessageMetadata.identifier.isBlank())
        assert(paymentAck.protocolMessageMetadata.version == 1L)
        assert(paymentAck.protocolMessageMetadata.statusCode == StatusCode.CANCEL)
        assert(paymentAck.protocolMessageMetadata.statusMessage == MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(paymentAck.protocolMessageMetadata.messageType == MessageType.PAYMENT_ACK)
    }

    @Test
    fun `Validate invalid PaymentAckBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            transactId.isPaymentAckValid("fakePaymentAckBinary".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary Encrypted, Owners and Sender with PkiData`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )
        val paymentAckBinary = transactId.createPaymentAck(paymentAckParameters)

        assert(transactId.isPaymentAckValid(paymentAckBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
    }

    @Test
    fun `Create and validate PaymentAckBinary Encrypted, Owners and Sender with PkiData without RecipientParametersEncryptionParameters`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentAckBinary = transactId.createPaymentAck(paymentAckParameters)

        assertThrows(EncryptionException::class.java) {
            transactId.isPaymentAckValid(paymentAckBinary)
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary, Owners and Sender with PkiData and invalid Encryption signature`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentAckBinary = transactId.createPaymentAck(paymentAckParameters)

        val messageProtocolCorrupted = Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(paymentAckBinary)
            .setIdentifier("bad identifier".toByteString())
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(transactId.isPaymentAckValid(messageProtocolCorrupted, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION))
        }

        assert(exception.message == SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE)
    }

    @Test
    fun `Create and validate PaymentAckBinary encrypted, without sender's public and private key`() {
        val exception = assertThrows(EncryptionException::class.java) {
            val paymentAckParameters = PaymentAckParameters(
                payment = PAYMENT,
                memo = MEMO,
                messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
                senderParameters = SENDER_PKI_X509SHA256,
                recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
            )

            transactId.createPaymentAck(paymentAckParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    @Test
    fun `Create and validate PaymentAckBinary encrypted, without recipient's public key`() {
        val exception = assertThrows(EncryptionException::class.java) {
            val paymentAckParameters = PaymentAckParameters(
                payment = PAYMENT,
                memo = MEMO,
                messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
                senderParameters = SENDER_PKI_X509SHA256,
                recipientParameters = RECIPIENTS_PARAMETERS
            )

            transactId.createPaymentAck(paymentAckParameters)
        }

        assert(exception.message == ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    @Test
    fun `Create and parse PaymentAckBinary encrypted to PaymentAck`() {
        val paymentAckParameters = PaymentAckParameters(
            payment = PAYMENT,
            memo = MEMO_PAYMENT_ACK,
            messageInformation = MESSAGE_INFORMATION_ENCRYPTION,
            senderParameters = SENDER_PKI_X509SHA256_WITH_ENCRYPTION,
            recipientParameters = RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
        )

        val paymentBinary = transactId.createPaymentAck(paymentAckParameters)
        val paymentAck = transactId.parsePaymentAck(paymentBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION)

        assert(paymentAck.payment.merchantData == PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == PAYMENT.outputs)
        assert(paymentAck.payment.originators.size == PAYMENT.originators.size)
        assert(paymentAck.payment.memo == PAYMENT.memo)
        assert(paymentAck.payment.protocolMessageMetadata == null)
        assert(paymentAck.memo == MEMO_PAYMENT_ACK)
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
    }
}
