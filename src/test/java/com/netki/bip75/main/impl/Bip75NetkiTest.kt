package com.netki.bip75.main.impl

import com.netki.TransactId
import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.service.impl.Bip75ServiceNetki
import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.model.AddressInformation
import com.netki.model.InvoiceRequestParameters
import com.netki.model.PaymentRequestParameters
import com.netki.security.CertificateValidator
import com.netki.util.ErrorInformation.ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER
import com.netki.util.ErrorInformation.ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER
import com.netki.util.TestData
import com.netki.util.TestData.Address.ADDRESS_INFORMATION
import com.netki.util.TestData.Attestations.REQUESTED_ATTESTATIONS
import com.netki.util.TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_NONE
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Recipients.RECIPIENTS_PARAMETERS
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.sql.Timestamp

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class Bip75NetkiTest {

    private lateinit var bip75Netki: Bip75Netki
    private lateinit var mockAddressInformationService: AddressInformationService
    private val certificateValidator = CertificateValidator("src/main/resources/certificates")
    private lateinit var transactId: TransactId

    @BeforeAll
    fun setUp() {
        mockAddressInformationService = Mockito.mock(AddressInformationService::class.java)
        bip75Netki = Bip75Netki(Bip75ServiceNetki(certificateValidator, mockAddressInformationService))
        transactId = TransactId(bip75Netki)
    }

    @Test
    fun `Create and parse InvoiceRequest binary and fetch AddressInformation`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenReturn(ADDRESS_INFORMATION)

        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestData)

        val invoiceRequest = bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)

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
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenReturn(AddressInformation())

        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestData)

        val invoiceRequest = bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)

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
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenThrow(
            AddressProviderUnauthorizedException(
                String.format(
                    ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER,
                    "test_address"
                )
            )
        )

        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )
        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestData)

        val exception = assertThrows(AddressProviderUnauthorizedException::class.java) {
            bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider authorization error for address:"))
    }

    @Test
    fun `Create and parse InvoiceRequest binary and fetch AddressInformation throwing error from the address provider`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenThrow(
            AddressProviderErrorException(
                String.format(
                    ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER,
                    "test_address",
                    "Runtime error"
                )
            )
        )

        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS
        )
        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestData)

        val exception = assertThrows(AddressProviderErrorException::class.java) {
            bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }

    @Test
    fun `Create and parse InvoiceRequest binary with recipient information and fetch AddressInformation`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenReturn(ADDRESS_INFORMATION)

        val originators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_NONE
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestData = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl",
            originatorsAddresses = TestData.Payment.Output.OUTPUTS,
            originatorParameters = originators,
            beneficiaryParameters = beneficiaries,
            senderParameters = sender,
            attestationsRequested = REQUESTED_ATTESTATIONS,
            recipientParameters = RECIPIENTS_PARAMETERS
        )
        val invoiceRequestBinary = transactId.createInvoiceRequest(invoiceRequestData)

        val invoiceRequest = bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)

        assertEquals(invoiceRequest.originatorsAddresses.size, invoiceRequestData.originatorsAddresses.size)
        assertEquals(invoiceRequest.recipientChainAddress, RECIPIENTS_PARAMETERS.chainAddress)
        assertEquals(invoiceRequest.recipientVaspName, RECIPIENTS_PARAMETERS.vaspName)
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
    fun `Create and parse PaymentRequest binary and fetch AddressInformation`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenReturn(ADDRESS_INFORMATION)

        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
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
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = bip75Netki.createPaymentRequest(paymentRequestParameters)

        val paymentRequest = bip75Netki.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)

        assertEquals(paymentRequest.beneficiariesAddresses.size, paymentRequestParameters.beneficiariesAddresses.size)
        paymentRequest.beneficiariesAddresses.forEach { output ->
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
    fun `Create and parse PaymentRequest binary and fetch AddressInformation for no existing address`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenReturn(AddressInformation())

        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
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
            attestationsRequested = REQUESTED_ATTESTATIONS
        )
        val paymentRequestBinary = bip75Netki.createPaymentRequest(paymentRequestParameters)

        val paymentRequest = bip75Netki.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)

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
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenThrow(
            AddressProviderUnauthorizedException(
                String.format(
                    ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER,
                    "test_address"
                )
            )
        )

        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
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
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = bip75Netki.createPaymentRequest(paymentRequestParameters)

        val exception = assertThrows(AddressProviderUnauthorizedException::class.java) {
            bip75Netki.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider authorization error for address:"))
    }

    @Test
    fun `Create and parse PaymentRequest binary and fetch AddressInformation throwing error from the address provider`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenThrow(
            AddressProviderErrorException(
                String.format(
                    ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER,
                    "test_address",
                    "Runtime error"
                )
            )
        )

        val beneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
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
            attestationsRequested = REQUESTED_ATTESTATIONS
        )

        val paymentRequestBinary = bip75Netki.createPaymentRequest(paymentRequestParameters)

        val exception = assertThrows(AddressProviderErrorException::class.java) {
            bip75Netki.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}
