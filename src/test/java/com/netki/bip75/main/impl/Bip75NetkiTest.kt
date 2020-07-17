package com.netki.bip75.main.impl

import com.netki.TransactId
import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.service.impl.Bip75ServiceNetki
import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.model.AddressInformation
import com.netki.util.ErrorInformation.ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER
import com.netki.util.ErrorInformation.ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER
import com.netki.util.TestData.Address.ADDRESS_INFORMATION
import com.netki.util.TestData.Attestations.REQUESTED_ATTESTATIONS
import com.netki.util.TestData.InvoiceRequest.INVOICE_REQUEST_DATA
import com.netki.util.TestData.Owners.NO_PRIMARY_OWNER_PKI_X509SHA256
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256
import com.netki.util.TestData.PaymentRequest.PAYMENT_DETAILS
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class Bip75NetkiTest {

    private lateinit var bip75Netki: Bip75Netki
    private lateinit var mockAddressInformationService: AddressInformationService

    @BeforeAll
    fun setUp() {
        mockAddressInformationService = Mockito.mock(AddressInformationService::class.java)
        bip75Netki = Bip75Netki(Bip75ServiceNetki(mockAddressInformationService))
    }

    @Test
    fun `Create and parse InvoiceRequest binary and fetch AddressInformation`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenReturn(ADDRESS_INFORMATION)

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(
                INVOICE_REQUEST_DATA, owners, sender,
                REQUESTED_ATTESTATIONS
            )

        val invoiceRequest = bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)

        assertEquals(invoiceRequest.outputs.size, INVOICE_REQUEST_DATA.outputs.size)
        invoiceRequest.outputs.forEach { output ->
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

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(
                INVOICE_REQUEST_DATA, owners, sender,
                REQUESTED_ATTESTATIONS
            )

        val invoiceRequest = bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)

        assertEquals(invoiceRequest.outputs.size, INVOICE_REQUEST_DATA.outputs.size)
        invoiceRequest.outputs.forEach { output ->
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

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(
                INVOICE_REQUEST_DATA, owners, sender,
                REQUESTED_ATTESTATIONS
            )

        val exception = Assertions.assertThrows(AddressProviderUnauthorizedException::class.java) {
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

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(
                INVOICE_REQUEST_DATA, owners, sender,
                REQUESTED_ATTESTATIONS
            )

        val exception = Assertions.assertThrows(AddressProviderErrorException::class.java) {
            bip75Netki.parseInvoiceRequestWithAddressesInfo(invoiceRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }

    @Test
    fun `Create and parse PaymentRequest binary and fetch AddressInformation`() {
        `when`(
            mockAddressInformationService.getAddressInformation(
                any(AddressCurrency::class.java),
                anyString()
            )
        ).thenReturn(ADDRESS_INFORMATION)

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            bip75Netki.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequest = bip75Netki.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)

        assertEquals(paymentRequest.paymentRequestParameters.outputs.size, PAYMENT_DETAILS.outputs.size)
        paymentRequest.paymentRequestParameters.outputs.forEach { output ->
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

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            bip75Netki.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val paymentRequest = bip75Netki.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)

        assertEquals(paymentRequest.paymentRequestParameters.outputs.size, PAYMENT_DETAILS.outputs.size)
        paymentRequest.paymentRequestParameters.outputs.forEach { output ->
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

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            bip75Netki.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = Assertions.assertThrows(AddressProviderUnauthorizedException::class.java) {
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

        val owners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val sender = SENDER_PKI_X509SHA256

        val paymentRequestBinary =
            bip75Netki.createPaymentRequest(PAYMENT_DETAILS, owners, sender, REQUESTED_ATTESTATIONS)

        val exception = Assertions.assertThrows(AddressProviderErrorException::class.java) {
            bip75Netki.parsePaymentRequestWithAddressesInfo(paymentRequestBinary)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}
