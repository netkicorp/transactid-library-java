package com.netki

import com.netki.model.InvoiceRequestParameters
import com.netki.model.MessageType
import com.netki.model.PaymentRequestParameters
import com.netki.model.StatusCode
import com.netki.util.TestData.Attestations.REQUESTED_ATTESTATIONS
import com.netki.util.TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
import com.netki.util.TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Payment.Output.OUTPUTS
import com.netki.util.TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Timestamp

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TransactIdTest {

    private val transactId = TransactId.getInstance("src/test/resources/certificates")

    @BeforeAll
    fun setUp() {
        // Nothing to do here
    }

    @Test
    fun `Change status from OK to CANCEL to InvoiceRequest`() {
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
        val identifier = invoiceRequest.protocolMessageMetadata.identifier

        assert(invoiceRequest.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(invoiceRequest.protocolMessageMetadata.statusMessage.isEmpty())

        val newStatusCode = StatusCode.CANCEL
        val newStatusMessage = "Random cancel"
        val updatedInvoiceRequestBinary =
            transactId.changeStatusProtocolMessage(invoiceRequestBinary, newStatusCode, newStatusMessage)
        val updatedInvoiceRequest = transactId.parseInvoiceRequest(updatedInvoiceRequestBinary)

        assert(updatedInvoiceRequest.protocolMessageMetadata.statusCode == newStatusCode)
        assert(updatedInvoiceRequest.protocolMessageMetadata.statusMessage == newStatusMessage)
        assert(updatedInvoiceRequest.protocolMessageMetadata.identifier == invoiceRequest.protocolMessageMetadata.identifier)
        assert(updatedInvoiceRequest.protocolMessageMetadata.nonce == invoiceRequest.protocolMessageMetadata.nonce)
    }

    @Test
    fun `Change status from OK to CERTIFICATE_EXPIRED to PaymentRequest Encrypted`() {
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
        val identifier = paymentRequest.protocolMessageMetadata.identifier
        val encryptedMessage = paymentRequest.protocolMessageMetadata.encryptedMessage

        assert(paymentRequest.protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(paymentRequest.protocolMessageMetadata.statusMessage.isEmpty())

        val newStatusCode = StatusCode.CERTIFICATE_EXPIRED
        val newStatusMessage = "Random cancel"
        val updatedPaymentRequestBinary =
            transactId.changeStatusProtocolMessage(paymentRequestBinary, newStatusCode, newStatusMessage)
        val updatedPaymentRequest =
            transactId.parsePaymentRequest(updatedPaymentRequestBinary, RECIPIENTS_PARAMETERS_WITH_ENCRYPTION)

        assert(updatedPaymentRequest.protocolMessageMetadata.statusCode == newStatusCode)
        assert(updatedPaymentRequest.protocolMessageMetadata.statusMessage == newStatusMessage)
        assert(updatedPaymentRequest.protocolMessageMetadata.identifier == identifier)
        assert(updatedPaymentRequest.protocolMessageMetadata.encryptedMessage == encryptedMessage)
        assert(updatedPaymentRequest.protocolMessageMetadata.identifier == updatedPaymentRequest.protocolMessageMetadata.identifier)
        assert(updatedPaymentRequest.protocolMessageMetadata.nonce == updatedPaymentRequest.protocolMessageMetadata.nonce)
    }

    @Test
    fun `Create InvoiceRequestBinary and extract protocolMessageMetadata`() {
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
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val protocolMessageBinary = transactId.createInvoiceRequest(invoiceRequestParameters)
        val protocolMessageMetadata = transactId.getProtocolMessageMetadata(protocolMessageBinary)

        assert(protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(protocolMessageMetadata.statusMessage.isEmpty())
        assert(protocolMessageMetadata.messageType == MessageType.INVOICE_REQUEST)
    }

    @Test
    fun `Create PaymentRequestBinary and extract protocolMessageMetadata`() {
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

        val protocolMessageBinary = transactId.createPaymentRequest(paymentRequestParameters)
        val protocolMessageMetadata = transactId.getProtocolMessageMetadata(protocolMessageBinary)

        assert(protocolMessageMetadata.statusCode == StatusCode.OK)
        assert(protocolMessageMetadata.statusMessage.isEmpty())
        assert(protocolMessageMetadata.messageType == MessageType.PAYMENT_REQUEST)
    }
}
