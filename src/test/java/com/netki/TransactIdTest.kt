package com.netki

import com.netki.bip75.protocol.Messages
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.MessageType
import com.netki.model.StatusCode
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE
import com.netki.util.TestData.Attestations.INVALID_ATTESTATION
import com.netki.util.TestData.Attestations.REQUESTED_ATTESTATIONS
import com.netki.util.TestData.InvoiceRequest.INVOICE_REQUEST_DATA
import com.netki.util.TestData.MessageInformationData.MESSAGE_INFORMATION_CANCEL
import com.netki.util.TestData.Owners.NO_PRIMARY_OWNER_PKI_NONE
import com.netki.util.TestData.Owners.NO_PRIMARY_OWNER_PKI_X509SHA256
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_NONE
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.Payment.MEMO
import com.netki.util.TestData.Payment.MEMO_PAYMENT_ACK
import com.netki.util.TestData.Payment.Output.OUTPUTS
import com.netki.util.TestData.Payment.PAYMENT
import com.netki.util.TestData.Payment.PAYMENT_PARAMETERS
import com.netki.util.TestData.PaymentRequest.PAYMENT_DETAILS
import com.netki.util.TestData.Senders.SENDER_PKI_NONE
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.getSerializedMessage
import com.netki.util.toAttestationType
import com.netki.util.toByteString
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TransactIdTest {

    private val transactId = TransactId.getInstance("src/main/resources/certificates")

    @BeforeAll
    fun setUp() {
        // Nothing to do here
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owner and Sender with PkiData and bundle certificate`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and bundle certificate`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_NONE

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_INVALID_CERTIFICATE

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }


    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData but invalid certificate chain and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_INVALID_CERTIFICATE,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isInvoiceRequestValid(invoiceRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and invalid Sender signature`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val invoiceRequestCorrupted = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary.getSerializedMessage())
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
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and invalid Owner signature`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val invoiceRequestCorrupted = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary.getSerializedMessage())

        val ownersWithInvalidSignature = mutableListOf<Messages.Owner>()
        invoiceRequestCorrupted.ownersList.forEachIndexed { index, owner ->
            val ownerWithoutSignaturesBuilder = Messages.Owner.newBuilder()
                .mergeFrom(owner)
            owner.attestationsList.forEachIndexed { attestationIndex, attestation ->
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

        invoiceRequestCorrupted.clearOwners()
        invoiceRequestCorrupted.addAllOwners(ownersWithInvalidSignature)

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
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val invoiceRequest = transactId.parseInvoiceRequest(invoiceRequestBinary)

        assert(INVOICE_REQUEST_DATA.amount == invoiceRequest.amount)
        assert(INVOICE_REQUEST_DATA.memo == invoiceRequest.memo)
        assert(INVOICE_REQUEST_DATA.notificationUrl == invoiceRequest.notificationUrl)
        assert(REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(OUTPUTS.size == invoiceRequest.outputs.size)

        assert(invoiceRequest.owners.size == 2)
        invoiceRequest.owners.forEachIndexed() { index, owner ->
            assert(owner.isPrimaryForTransaction == owners[index].isPrimaryForTransaction)
            owner.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_OWNER_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (owner.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters.type == invoiceRequest.senderPkiType)
        assert(sender.pkiDataParameters.certificatePem == invoiceRequest.senderPkiData)
        assert(!invoiceRequest.senderSignature.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.identifier.isBlank())
        assert(invoiceRequest.protocolMessageMetadata.version == 1L)
        assert(invoiceRequest.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(invoiceRequest.protocolMessageMetadata.statusMessage.isEmpty())
        assert(invoiceRequest.protocolMessageMetadata.messageType == MessageType.INVOICE_REQUEST)
    }


    @Test
    fun `Create and parse InvoiceRequestBinary to InvoiceRequest with message information`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            transactId.createInvoiceRequest(
                INVOICE_REQUEST_DATA,
                owners,
                sender,
                REQUESTED_ATTESTATIONS,
                null,
                MESSAGE_INFORMATION_CANCEL
            )

        val invoiceRequest = transactId.parseInvoiceRequest(invoiceRequestBinary)

        assert(INVOICE_REQUEST_DATA.amount == invoiceRequest.amount)
        assert(INVOICE_REQUEST_DATA.memo == invoiceRequest.memo)
        assert(INVOICE_REQUEST_DATA.notificationUrl == invoiceRequest.notificationUrl)
        assert(REQUESTED_ATTESTATIONS.size == invoiceRequest.attestationsRequested.size)
        assert(OUTPUTS.size == invoiceRequest.outputs.size)

        assert(invoiceRequest.owners.size == 2)
        invoiceRequest.owners.forEachIndexed() { index, owner ->
            assert(owner.isPrimaryForTransaction == owners[index].isPrimaryForTransaction)
            owner.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_OWNER_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (owner.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters.type == invoiceRequest.senderPkiType)
        assert(sender.pkiDataParameters.certificatePem == invoiceRequest.senderPkiData)
        assert(!invoiceRequest.senderSignature.isNullOrBlank())
        assert(!invoiceRequest.protocolMessageMetadata.identifier.isBlank())
        assert(invoiceRequest.protocolMessageMetadata.version == 1L)
        assert(invoiceRequest.protocolMessageMetadata.statusCode == StatusCode.CANCEL)
        assert(invoiceRequest.protocolMessageMetadata.statusMessage == MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(invoiceRequest.protocolMessageMetadata.messageType == MessageType.INVOICE_REQUEST)
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
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owner and Sender with PkiData bundle`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData bundle`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_NONE

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(transactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_INVALID_CERTIFICATE

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isPaymentRequestValid(paymentRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData but invalid certificate chain and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_INVALID_CERTIFICATE,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(transactId.isPaymentRequestValid(paymentRequestBinary))
        }

        assert(exception.message == CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA.format(INVALID_ATTESTATION.name))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData and invalid Sender signature`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary.getSerializedMessage())
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
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary.getSerializedMessage())

        val ownersWithInvalidSignature = mutableListOf<Messages.Owner>()
        paymentRequestCorrupted.ownersList.forEachIndexed { index, owner ->
            val ownerWithoutSignaturesBuilder = Messages.Owner.newBuilder()
                .mergeFrom(owner)
            owner.attestationsList.forEachIndexed() { attestationIndex, attestation ->
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

        paymentRequestCorrupted.clearOwners()
        paymentRequestCorrupted.addAllOwners(ownersWithInvalidSignature)


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
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequest = transactId.parsePaymentRequest(paymentRequestBinary)

        val paymentDetails = paymentRequest.paymentRequestParameters

        assert(paymentDetails.network == PAYMENT_DETAILS.network)
        assert(paymentDetails.outputs == PAYMENT_DETAILS.outputs)
        assert(paymentDetails.time == PAYMENT_DETAILS.time)
        assert(paymentDetails.expires == PAYMENT_DETAILS.expires)
        assert(paymentDetails.memo == PAYMENT_DETAILS.memo)
        assert(paymentDetails.paymentUrl == PAYMENT_DETAILS.paymentUrl)
        assert(paymentDetails.merchantData == PAYMENT_DETAILS.merchantData)
        assert(paymentDetails.outputs.size == PAYMENT_DETAILS.outputs.size)

        assert(paymentRequest.owners.size == 2)
        paymentRequest.owners.forEachIndexed() { index, owner ->
            assert(owner.isPrimaryForTransaction == owners[index].isPrimaryForTransaction)
            owner.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_OWNER_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (owner.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters.type == paymentRequest.senderPkiType)
        assert(sender.pkiDataParameters.certificatePem == paymentRequest.senderPkiData)
        assert(!paymentRequest.senderSignature.isNullOrBlank())
        assert(!paymentRequest.protocolMessageMetadata.identifier.isBlank())
        assert(paymentRequest.protocolMessageMetadata.version == 1L)
        assert(paymentRequest.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(paymentRequest.protocolMessageMetadata.statusMessage.isEmpty())
        assert(paymentRequest.protocolMessageMetadata.messageType == MessageType.PAYMENT_REQUEST)
    }

    @Test
    fun `Create and parse PaymentRequestBinary to PaymentRequest with message information`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            transactId.createPaymentRequest(
                PAYMENT_DETAILS,
                owners,
                sender,
                REQUESTED_ATTESTATIONS,
                1,
                MESSAGE_INFORMATION_CANCEL
            )

        val paymentRequest = transactId.parsePaymentRequest(paymentRequestBinary)

        val paymentDetails = paymentRequest.paymentRequestParameters

        assert(paymentDetails.network == PAYMENT_DETAILS.network)
        assert(paymentDetails.outputs == PAYMENT_DETAILS.outputs)
        assert(paymentDetails.time == PAYMENT_DETAILS.time)
        assert(paymentDetails.expires == PAYMENT_DETAILS.expires)
        assert(paymentDetails.memo == PAYMENT_DETAILS.memo)
        assert(paymentDetails.paymentUrl == PAYMENT_DETAILS.paymentUrl)
        assert(paymentDetails.merchantData == PAYMENT_DETAILS.merchantData)
        assert(paymentDetails.outputs.size == PAYMENT_DETAILS.outputs.size)

        assert(paymentRequest.owners.size == 2)
        paymentRequest.owners.forEachIndexed() { index, owner ->
            assert(owner.isPrimaryForTransaction == owners[index].isPrimaryForTransaction)
            owner.pkiDataSet.forEachIndexed { pkiDataIndex, pkiData ->
                val ownerPkiData = PRIMARY_OWNER_PKI_X509SHA256.pkiDataParametersSets[pkiDataIndex]
                assert(pkiData.type == ownerPkiData.type)
                assert(pkiData.attestation == ownerPkiData.attestation)
                assert(pkiData.certificatePem == ownerPkiData.certificatePem)
                when (owner.isPrimaryForTransaction) {
                    true -> assert(!pkiData.signature.isNullOrBlank())
                    false -> assert(pkiData.signature.isNullOrBlank())
                }
            }
        }

        assert(sender.pkiDataParameters.type == paymentRequest.senderPkiType)
        assert(sender.pkiDataParameters.certificatePem == paymentRequest.senderPkiData)
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
    fun `Create and validate PaymentBinary`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val paymentBinary = transactId.createPayment(PAYMENT_PARAMETERS, owners)

        assert(transactId.isPaymentValid(paymentBinary))
    }

    @Test
    fun `Create and parse PaymentBinary to Payment`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val paymentBinary = transactId.createPayment(PAYMENT_PARAMETERS, owners)
        val payment = transactId.parsePayment(paymentBinary)

        assert(payment.merchantData == PAYMENT_PARAMETERS.merchantData)
        assert(payment.transactions.size == PAYMENT_PARAMETERS.transactions.size)
        assert(payment.outputs == PAYMENT_PARAMETERS.outputs)
        assert(payment.memo == PAYMENT_PARAMETERS.memo)
        assert(payment.owners.size == owners.size)
        assert(!payment.protocolMessageMetadata!!.identifier.isBlank())
        assert(payment.protocolMessageMetadata?.version == 1L)
        assert(payment.protocolMessageMetadata?.statusCode == StatusCode.OK)
        assert(payment.protocolMessageMetadata?.statusMessage.isNullOrBlank())
        assert(payment.protocolMessageMetadata?.messageType == MessageType.PAYMENT)
    }

    @Test
    fun `Create and parse PaymentBinary to Payment with message information`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val paymentBinary = transactId.createPayment(PAYMENT_PARAMETERS, owners, MESSAGE_INFORMATION_CANCEL)
        val payment = transactId.parsePayment(paymentBinary)

        assert(payment.merchantData == PAYMENT_PARAMETERS.merchantData)
        assert(payment.transactions.size == PAYMENT_PARAMETERS.transactions.size)
        assert(payment.outputs == PAYMENT_PARAMETERS.outputs)
        assert(payment.memo == PAYMENT_PARAMETERS.memo)
        assert(payment.owners.size == owners.size)
        assert(!payment.protocolMessageMetadata!!.identifier.isBlank())
        assert(payment.protocolMessageMetadata?.version == 1L)
        assert(payment.protocolMessageMetadata?.statusCode == StatusCode.CANCEL)
        assert(payment.protocolMessageMetadata?.statusMessage == MESSAGE_INFORMATION_CANCEL.statusMessage)
        assert(payment.protocolMessageMetadata?.messageType == MessageType.PAYMENT)
    }

    @Test
    fun `Validate invalid PaymentBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            transactId.isPaymentValid("fakePaymentBinary".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary`() {
        val paymentAckBinary = transactId.createPaymentAck(PAYMENT, MEMO)

        assert(transactId.isPaymentAckValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck`() {
        val paymentBinary = transactId.createPaymentAck(PAYMENT, MEMO_PAYMENT_ACK)
        val paymentAck = transactId.parsePaymentAck(paymentBinary)

        assert(paymentAck.payment.merchantData == PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == PAYMENT.outputs)
        assert(paymentAck.payment.owners.size == PAYMENT.owners.size)
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
        val paymentAckBinary = transactId.createPaymentAck(PAYMENT, MEMO)

        assert(transactId.isPaymentAckValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck with message information`() {
        val paymentBinary = transactId.createPaymentAck(PAYMENT, MEMO_PAYMENT_ACK, MESSAGE_INFORMATION_CANCEL)
        val paymentAck = transactId.parsePaymentAck(paymentBinary)

        assert(paymentAck.payment.merchantData == PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == PAYMENT.outputs)
        assert(paymentAck.payment.owners.size == PAYMENT.owners.size)
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
}
