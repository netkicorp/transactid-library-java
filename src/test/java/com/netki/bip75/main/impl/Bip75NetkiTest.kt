package com.netki.bip75.main.impl

import com.netki.TransactId
import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.processor.impl.InvoiceRequestProcessor
import com.netki.bip75.processor.impl.PaymentAckProcessor
import com.netki.bip75.processor.impl.PaymentProcessor
import com.netki.bip75.processor.impl.PaymentRequestProcessor
import com.netki.bip75.service.impl.Bip75ServiceNetki
import com.netki.security.CertificateValidator
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class Bip75NetkiTest {

    private lateinit var bip75Netki: Bip75Netki
    private lateinit var mockAddressInformationService: AddressInformationService
    private val certificateValidator = CertificateValidator("src/test/resources/certificates")
    private lateinit var invoiceRequestProcessor: InvoiceRequestProcessor
    private lateinit var paymentRequestProcessor: PaymentRequestProcessor
    private lateinit var paymentProcessor: PaymentProcessor
    private lateinit var paymentAckProcessor: PaymentAckProcessor
    private lateinit var transactId: TransactId

    @BeforeAll
    fun setUp() {
        mockAddressInformationService = Mockito.mock(AddressInformationService::class.java)
        invoiceRequestProcessor = InvoiceRequestProcessor(mockAddressInformationService, certificateValidator)
        paymentRequestProcessor = PaymentRequestProcessor(mockAddressInformationService, certificateValidator)
        paymentProcessor = PaymentProcessor(mockAddressInformationService, certificateValidator)
        paymentAckProcessor = PaymentAckProcessor(mockAddressInformationService, certificateValidator)
        bip75Netki = Bip75Netki(
            Bip75ServiceNetki(
                invoiceRequestProcessor,
                paymentRequestProcessor,
                paymentProcessor,
                paymentAckProcessor
            )
        )
        transactId = TransactId(bip75Netki)
    }

}
