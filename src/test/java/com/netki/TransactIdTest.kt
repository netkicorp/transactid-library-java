package com.netki

import com.netki.bip75.protocol.Messages
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidSignatureException
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE
import com.netki.util.ErrorInformation.SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE
import com.netki.util.TestData.Attestations.INVALID_ATTESTATION
import com.netki.util.TestData.Attestations.REQUESTED_ATTESTATIONS
import com.netki.util.TestData.InvoiceRequest.INVOICE_REQUEST_DATA
import com.netki.util.TestData.Owners.NO_PRIMARY_OWNER_PKI_NONE
import com.netki.util.TestData.Owners.NO_PRIMARY_OWNER_PKI_X509SHA256
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_NONE
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.Payment.MEMO
import com.netki.util.TestData.Payment.Output.OUTPUTS
import com.netki.util.TestData.Payment.PAYMENT
import com.netki.util.TestData.Payment.PAYMENT_PARAMETERS
import com.netki.util.TestData.PaymentRequest.PAYMENT_DETAILS
import com.netki.util.TestData.Senders.SENDER_PKI_NONE
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.toAttestationType
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TransactIdTest {

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
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owner and Sender with PkiData and bundle certificate`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender with PkiData and bundle certificate`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_NONE

        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE

        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_INVALID_CERTIFICATE

        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
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
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
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
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val invoiceRequestCorrupted = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary)
            .setMemo("Memo changed!!")
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(TransactId.isInvoiceRequestValid(invoiceRequestCorrupted))
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
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val invoiceRequestCorrupted = Messages.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary)

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


        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(TransactId.isInvoiceRequestValid(invoiceRequestCorrupted.build().toByteArray()))
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
            TransactId.createInvoiceRequest(INVOICE_REQUEST_DATA, owners, sender, REQUESTED_ATTESTATIONS)

        val invoiceRequest = TransactId.parseInvoiceRequest(invoiceRequestBinary)

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
    }

    @Test
    fun `Validate invalid InvoiceRequestBinary`() {
        val exception = assertThrows(InvalidObjectException::class.java) {
            TransactId.isInvoiceRequestValid("fakeInvoiceRequest".toByteArray())
        }

        assert(exception.message?.contains("Invalid object for: invoiceRequest") ?: false)
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owner and Sender with PkiData bundle`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender with PkiData bundle`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256_BUNDLED_CERTIFICATE,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_NONE

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender without PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_NONE

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners without PkiData and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_NONE,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, one Owner with PkiData, one Owner without data and Sender with PkiData`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate PaymentRequestBinary, Owners with PkiData and Sender with PkiData but invalid certificate chain`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256_INVALID_CERTIFICATE

        val paymentRequestBinary =
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
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
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
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
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary)
            .setPaymentDetailsVersion(4)
            .build()
            .toByteArray()

        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(TransactId.isPaymentRequestValid(paymentRequestCorrupted))
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
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequestCorrupted = Messages.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary)

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


        val exception = assertThrows(InvalidSignatureException::class.java) {
            assert(TransactId.isPaymentRequestValid(paymentRequestCorrupted.build().toByteArray()))
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
            TransactId.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequest = TransactId.parsePaymentRequest(paymentRequestBinary)

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
    }

    @Test
    fun `Validate invalid PaymentRequestBinary`() {
        val exception = assertThrows(InvalidObjectException::class.java) {
            TransactId.isPaymentRequestValid("fakePaymentRequest".toByteArray())
        }

        assert(exception.message?.contains("Invalid object for: paymentRequest") ?: false)
    }

    @Test
    fun `Create and validate PaymentBinary`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val paymentBinary = TransactId.createPayment(PAYMENT_PARAMETERS, owners)

        assert(TransactId.isPaymentValid(paymentBinary))
    }

    @Test
    fun `Create and parse PaymentBinary to Payment`() {
        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val paymentBinary = TransactId.createPayment(PAYMENT_PARAMETERS, owners)
        val payment = TransactId.parsePayment(paymentBinary)

        assert(payment.merchantData == PAYMENT_PARAMETERS.merchantData)
        assert(payment.transactions.size == PAYMENT_PARAMETERS.transactions.size)
        assert(payment.outputs == PAYMENT_PARAMETERS.outputs)
        assert(payment.memo == PAYMENT_PARAMETERS.memo)
        assert(payment.owners.size == owners.size)
    }

    @Test
    fun `Validate invalid PaymentBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            TransactId.isPaymentValid("fakePaymentBinary".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary`() {
        val paymentAckBinary = TransactId.createPaymentAck(PAYMENT, MEMO)

        assert(TransactId.isPaymentAckValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck`() {
        val paymentBinary = TransactId.createPaymentAck(PAYMENT, MEMO)
        val paymentAck = TransactId.parsePaymentAck(paymentBinary)

        assert(paymentAck.payment.merchantData == PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == PAYMENT.outputs)
        assert(paymentAck.payment.owners.size == PAYMENT.owners.size)
        assert(paymentAck.payment.memo == PAYMENT.memo)
        assert(paymentAck.memo == MEMO)
    }

    @Test
    fun `Validate invalid PaymentAckBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            TransactId.isPaymentAckValid("fakePaymentAckBinary".toByteArray())
        }
    }
}
