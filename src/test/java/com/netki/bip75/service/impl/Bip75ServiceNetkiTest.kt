package com.netki.bip75.service.impl

import com.netki.exceptions.ExceptionInformation
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.message.config.MessageFactory
import com.netki.model.InvoiceRequestParameters
import com.netki.model.PaymentParameters
import com.netki.model.PaymentRequestParameters
import com.netki.security.CertificateValidator
import com.netki.util.TestData
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Timestamp

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class Bip75ServiceNetkiTest {

    private lateinit var bip75ServiceNetki: Bip75ServiceNetki

    @BeforeAll
    fun setUp() {
        val message = MessageFactory.getInstance("fake-key")
        val certificateValidator = CertificateValidator("src/test/resources/certificates")
        bip75ServiceNetki = Bip75ServiceNetki(message, certificateValidator)
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
            originatorsAddresses = TestData.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = bip75ServiceNetki.createInvoiceRequest(invoiceRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(bip75ServiceNetki.isInvoiceRequestValid(invoiceRequestBinary))
        }

        assert(exception.message == ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }

    @Test
    fun `Create and validate InvoiceRequestBinary, Beneficiaries with PkiData but invalid certificate chain and Sender with PkiData`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_INVALID_CERTIFICATE
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val invoiceRequestParameters = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = bip75ServiceNetki.createInvoiceRequest(invoiceRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(bip75ServiceNetki.isInvoiceRequestValid(invoiceRequestBinary))
        }

        assert(
            exception.message == ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA.format(
                TestData.Attestations.INVALID_ATTESTATION.name
            )
        )
    }


    @Test
    fun `Create and validate InvoiceRequestBinary, Originators with PkiData but invalid certificate chain and Sender with PkiData`() {
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
            originatorsAddresses = TestData.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = bip75ServiceNetki.createInvoiceRequest(invoiceRequestParameters)

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(bip75ServiceNetki.isInvoiceRequestValid(invoiceRequestBinary))
        }

        assert(
            exception.message == ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_ORIGINATOR_CERTIFICATE_CA.format(
                TestData.Attestations.INVALID_ATTESTATION.name
            )
        )
    }


    @Test
    fun `Create and validate PaymentRequestBinary, Beneficiaries with PkiData but invalid certificate chain and Sender with PkiData`() {
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_INVALID_CERTIFICATE,
            TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = TestData.Senders.SENDER_PKI_X509SHA256
        val paymentRequestParameters = PaymentRequestParameters(
            network = "main",
            beneficiariesAddresses = TestData.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = bip75ServiceNetki.createPaymentRequest(paymentRequestParameters, "12345")

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(bip75ServiceNetki.isPaymentRequestValid(paymentRequestBinary))
        }

        assert(
            exception.message == ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA.format(
                TestData.Attestations.INVALID_ATTESTATION.name
            )
        )
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
            beneficiariesAddresses = TestData.Output.OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data",
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = TestData.Attestations.REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = bip75ServiceNetki.createPaymentRequest(paymentRequestParameters, "12345")

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(bip75ServiceNetki.isPaymentRequestValid(paymentRequestBinary))
        }

        assert(exception.message == ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA)
    }


    @Test
    fun `Create and validate PaymentBinary with originator certificate not valid`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256_INVALID_CERTIFICATE,
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
            outputs = TestData.Output.OUTPUTS,
            memo = "memo",
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries
        )

        val paymentBinary = bip75ServiceNetki.createPayment(paymentParameters, "12345")

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(bip75ServiceNetki.isPaymentValid(paymentBinary))
        }

        assert(
            exception.message == ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_ORIGINATOR_CERTIFICATE_CA.format(
                TestData.Attestations.INVALID_ATTESTATION.name
            )
        )
    }

    @Test
    fun `Create and validate PaymentBinary with beneficiaries certificate not valid`() {
        val originators = listOf(
            TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256,
            TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256_INVALID_CERTIFICATE
        )
        val paymentParameters = PaymentParameters(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = TestData.Output.OUTPUTS,
            memo = "memo",
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries
        )

        val paymentBinary = bip75ServiceNetki.createPayment(paymentParameters, "12345")

        val exception = assertThrows(InvalidCertificateChainException::class.java) {
            assert(bip75ServiceNetki.isPaymentValid(paymentBinary))
        }

        assert(
            exception.message == ExceptionInformation.CERTIFICATE_VALIDATION_INVALID_BENEFICIARY_CERTIFICATE_CA.format(
                TestData.Attestations.INVALID_ATTESTATION.name
            )
        )
    }

}
