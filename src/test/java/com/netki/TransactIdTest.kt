package com.netki

import com.netki.bip75.protocol.Protos
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.KeyPairPem
import com.netki.model.PkiType
import com.netki.security.CryptoModule
import com.netki.util.TestData
import com.netki.util.toByteString
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TransactIdTest {

    private lateinit var keyPairPemX509: KeyPairPem
    private lateinit var keyPairPemNone: KeyPairPem
    private lateinit var invalidCertKeyPairPemX509: KeyPairPem

    @BeforeAll
    fun setUp() {
        val privateKey = CryptoModule.privateKeyPemToObject(TestData.KeyPairs.CLIENT_PRIVATE_KEY_PEM)
        val certificate = CryptoModule.certificatePemToObject(TestData.KeyPairs.CLIENT_CERTIFICATE_PEM)
        val randomCertificate = CryptoModule.certificatePemToObject(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM_PEM)
        keyPairPemX509 = KeyPairPem(
            CryptoModule.objectToPrivateKeyPem(privateKey),
            CryptoModule.objectToCertificatePem(certificate),
            PkiType.X509SHA256
        )
        keyPairPemNone = KeyPairPem(
            CryptoModule.objectToPrivateKeyPem(privateKey),
            CryptoModule.objectToCertificatePem(certificate),
            PkiType.NONE
        )
        invalidCertKeyPairPemX509 = KeyPairPem(
            CryptoModule.objectToPrivateKeyPem(privateKey),
            CryptoModule.objectToCertificatePem(randomCertificate),
            PkiType.X509SHA256
        )
    }

    @Test
    fun `Create and validate InvoiceRequestBinary with PkiData`() {
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(TestData.InvoiceRequest.INVOICE_REQUEST_DATA, keyPairPemX509)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create and validate InvoiceRequestBinary without PkiData`() {
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(TestData.InvoiceRequest.INVOICE_REQUEST_DATA, keyPairPemNone)

        assert(TransactId.isInvoiceRequestValid(invoiceRequestBinary))
    }

    @Test
    fun `Create an invalid InvoiceRequestBinary with PkiData and invalid certificate chain`() {
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(TestData.InvoiceRequest.INVOICE_REQUEST_DATA, invalidCertKeyPairPemX509)

        assertThrows(InvalidCertificateChainException::class.java) {
            TransactId.isInvoiceRequestValid(invoiceRequestBinary)
        }
    }

    @Test
    fun `Create and validate invalid signature for InvoiceRequestBinary with PkiData`() {
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(TestData.InvoiceRequest.INVOICE_REQUEST_DATA, keyPairPemX509)
        val invoiceRequestInvalidSignature = Protos.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary)
            .setSignature(
                ("OJ5cmg/1HHvmnAIlEYhu7GLGHISeZTYehmMA5uvmakxB/qZDduhw7ZKgxs8hFf/SiY0m/Nw/aICAbxtighLG3Bn+jRTy" +
                        "x5lGECceuLeCgqhrXGDK9p6q853gKFibe1uw+dQWWCF/SuJ1wvs4p2uHTzUCxnginAcSdLiRqukUPSlZVN+Md" +
                        "BXLEhMCOvkkrY4yDIcWDDKBJH7BRtI4hJ7fDEypC1e65QT5pYHkYySWrNku65zGfS2w6VccUYyXy2hqulJYKg" +
                        "QzEOoAj7CyULIDMnab/OYKJYcOcg98VbKBhh91GrCIXtQBsba5TD93lJNjIaznhJlatvB+QkWYbXfhNA==").toByteString()
            )
            .build()

        assertThrows(InvalidSignatureException::class.java) {
            TransactId.isInvoiceRequestValid(invoiceRequestInvalidSignature.toByteArray())
        }
    }

    @Test
    fun `Create and validate missing signature for InvoiceRequestBinary with PkiData`() {
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(TestData.InvoiceRequest.INVOICE_REQUEST_DATA, keyPairPemX509)
        val invoiceRequestInvalidSignature = Protos.InvoiceRequest.newBuilder()
            .mergeFrom(invoiceRequestBinary)
            .setSignature(
                ("OJ5cmg/1HHvmnAIlEYhu7GLGHISeZTYehmMA5uvmakxB/qZDduhw7ZKgxs8hFf/SiY0m/Nw/aICAbxtighLG3Bn+jRTy" +
                        "x5lGECceuLeCgqhrXGDK9p6q853gKFibe1uw+dQWWCF/SuJ1wvs4p2uHTzUCxnginAcSdLiRqukUPSlZVN+Md" +
                        "BXLEhMCOvkkrY4yDIcWDDKBJH7BRtI4hJ7fDEypC1e65QT5pYHkYySWrNku65zGfS2w6VccUYyXy2hqulJYKg" +
                        "QzEOoAj7CyULIDMnab/OYKJYcOcg98VbKBhh91GrCIXtQBsba5TD93lJNjIaznhJlatvB+QkWYbXfhNA==").toByteString()
            )
            .build()

        assertThrows(InvalidSignatureException::class.java) {
            TransactId.isInvoiceRequestValid(invoiceRequestInvalidSignature.toByteArray())
        }
    }

    @Test
    fun `Create and parse InvoiceRequestBinary to InvoiceRequest with PkiData`() {
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(TestData.InvoiceRequest.INVOICE_REQUEST_DATA, keyPairPemX509)
        val invoiceRequest = TransactId.parseInvoiceRequest(invoiceRequestBinary)
        val publicKey = CryptoModule.certificatePemToObject(keyPairPemX509.certificatePem).publicKey

        assert(invoiceRequest.amount == TestData.InvoiceRequest.INVOICE_REQUEST_DATA.amount)
        assert(invoiceRequest.memo == TestData.InvoiceRequest.INVOICE_REQUEST_DATA.memo)
        assert(invoiceRequest.notificationUrl == TestData.InvoiceRequest.INVOICE_REQUEST_DATA.notificationUrl)
        assert(invoiceRequest.pkiData == keyPairPemX509.certificatePem)
        assert(invoiceRequest.pkiType == keyPairPemX509.type.value)
        assert(invoiceRequest.senderPublicKey == CryptoModule.objectToPublicKeyPem(publicKey))
        assert(!invoiceRequest.signature.isNullOrBlank())
    }

    @Test
    fun `Create and parse InvoiceRequestBinary to InvoiceRequest without PkiData`() {
        val invoiceRequestBinary =
            TransactId.createInvoiceRequest(TestData.InvoiceRequest.INVOICE_REQUEST_DATA, keyPairPemNone)
        val invoiceRequest = TransactId.parseInvoiceRequest(invoiceRequestBinary)
        val publicKey = CryptoModule.certificatePemToObject(keyPairPemNone.certificatePem).publicKey

        assert(invoiceRequest.amount == TestData.InvoiceRequest.INVOICE_REQUEST_DATA.amount)
        assert(invoiceRequest.memo == TestData.InvoiceRequest.INVOICE_REQUEST_DATA.memo)
        assert(invoiceRequest.notificationUrl == TestData.InvoiceRequest.INVOICE_REQUEST_DATA.notificationUrl)
        assert(invoiceRequest.pkiData == keyPairPemNone.certificatePem)
        assert(invoiceRequest.pkiType == keyPairPemNone.type.value)
        assert(invoiceRequest.senderPublicKey == CryptoModule.objectToPublicKeyPem(publicKey))
        assert(invoiceRequest.signature.isNullOrBlank())
    }

    @Test
    fun `Validate invalid InvoiceRequestBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            TransactId.isInvoiceRequestValid("fakeInvoiceRequest".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentRequestBinary with PkiData`() {
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, keyPairPemX509)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and invalid PaymentRequestBinary with PkiData and invalid certificate chain`() {
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, invalidCertKeyPairPemX509)

        assertThrows(InvalidCertificateChainException::class.java) {
            TransactId.isPaymentRequestValid(paymentRequestBinary)
        }
    }

    @Test
    fun `Create and validate PaymentRequestBinary without PkiData`() {
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, keyPairPemNone)

        assert(TransactId.isPaymentRequestValid(paymentRequestBinary))
    }

    @Test
    fun `Create and validate invalid signature for PaymentRequestBinary with PkiData`() {
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, keyPairPemX509)
        val paymentRequestInvalidSignature = Protos.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary)
            .setSignature(
                ("OJ5cmg/1HHvmnAIlEYhu7GLGHISeZTYehmMA5uvmakxB/qZDduhw7ZKgxs8hFf/SiY0m/Nw/aICAbxtighLG3Bn+jRTy" +
                        "x5lGECceuLeCgqhrXGDK9p6q853gKFibe1uw+dQWWCF/SuJ1wvs4p2uHTzUCxnginAcSdLiRqukUPSlZVN+Md" +
                        "BXLEhMCOvkkrY4yDIcWDDKBJH7BRtI4hJ7fDEypC1e65QT5pYHkYySWrNku65zGfS2w6VccUYyXy2hqulJYKg" +
                        "QzEOoAj7CyULIDMnab/OYKJYcOcg98VbKBhh91GrCIXtQBsba5TD93lJNjIaznhJlatvB+QkWYbXfhNA==").toByteString()
            )
            .build()

        assertThrows(InvalidSignatureException::class.java) {
            TransactId.isPaymentRequestValid(paymentRequestInvalidSignature.toByteArray())
        }
    }

    @Test
    fun `Create and validate missing signature for PaymentRequestBinary with PkiData`() {
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, keyPairPemX509)
        val paymentRequestInvalidSignature = Protos.PaymentRequest.newBuilder()
            .mergeFrom(paymentRequestBinary)
            .setSignature(
                ("OJ5cmg/1HHvmnAIlEYhu7GLGHISeZTYehmMA5uvmakxB/qZDduhw7ZKgxs8hFf/SiY0m/Nw/aICAbxtighLG3Bn+jRTy" +
                        "x5lGECceuLeCgqhrXGDK9p6q853gKFibe1uw+dQWWCF/SuJ1wvs4p2uHTzUCxnginAcSdLiRqukUPSlZVN+Md" +
                        "BXLEhMCOvkkrY4yDIcWDDKBJH7BRtI4hJ7fDEypC1e65QT5pYHkYySWrNku65zGfS2w6VccUYyXy2hqulJYKg" +
                        "QzEOoAj7CyULIDMnab/OYKJYcOcg98VbKBhh91GrCIXtQBsba5TD93lJNjIaznhJlatvB+QkWYbXfhNA==").toByteString()
            )
            .build()

        assertThrows(InvalidSignatureException::class.java) {
            TransactId.isPaymentRequestValid(paymentRequestInvalidSignature.toByteArray())
        }
    }

    @Test
    fun `Create and parse PaymentRequestBinary to PaymentRequest with PkiData`() {
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, keyPairPemX509)
        val paymentRequest = TransactId.parsePaymentRequest(paymentRequestBinary)

        assert(paymentRequest.paymentDetailsVersion == 1)
        assert(paymentRequest.pkiData == keyPairPemX509.certificatePem)
        assert(paymentRequest.pkiType == keyPairPemX509.type.value)
        assert(paymentRequest.paymentDetails.network == TestData.PaymentRequest.PAYMENT_DETAILS.network)
        assert(paymentRequest.paymentDetails.outputs == TestData.PaymentRequest.PAYMENT_DETAILS.outputs)
        assert(paymentRequest.paymentDetails.time == TestData.PaymentRequest.PAYMENT_DETAILS.time)
        assert(paymentRequest.paymentDetails.expires == TestData.PaymentRequest.PAYMENT_DETAILS.expires)
        assert(paymentRequest.paymentDetails.memo == TestData.PaymentRequest.PAYMENT_DETAILS.memo)
        assert(paymentRequest.paymentDetails.paymentUrl == TestData.PaymentRequest.PAYMENT_DETAILS.paymentUrl)
        assert(paymentRequest.paymentDetails.merchantData == TestData.PaymentRequest.PAYMENT_DETAILS.merchantData)
        assert(!paymentRequest.signature.isNullOrBlank())
    }


    @Test
    fun `Create and parse PaymentRequestBinary to PaymentRequest without PkiData`() {
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, keyPairPemNone)
        val paymentRequest = TransactId.parsePaymentRequest(paymentRequestBinary)

        assert(paymentRequest.paymentDetailsVersion == 1)
        assert(paymentRequest.pkiData == keyPairPemNone.certificatePem)
        assert(paymentRequest.pkiType == keyPairPemNone.type.value)
        assert(paymentRequest.paymentDetails.network == TestData.PaymentRequest.PAYMENT_DETAILS.network)
        assert(paymentRequest.paymentDetails.outputs == TestData.PaymentRequest.PAYMENT_DETAILS.outputs)
        assert(paymentRequest.paymentDetails.time == TestData.PaymentRequest.PAYMENT_DETAILS.time)
        assert(paymentRequest.paymentDetails.expires == TestData.PaymentRequest.PAYMENT_DETAILS.expires)
        assert(paymentRequest.paymentDetails.memo == TestData.PaymentRequest.PAYMENT_DETAILS.memo)
        assert(paymentRequest.paymentDetails.paymentUrl == TestData.PaymentRequest.PAYMENT_DETAILS.paymentUrl)
        assert(paymentRequest.paymentDetails.merchantData == TestData.PaymentRequest.PAYMENT_DETAILS.merchantData)
        assert(paymentRequest.signature.isNullOrBlank())
    }

    @Test
    fun `Create and parse PaymentRequestBinary to PaymentRequest with version and PkiData`() {
        val paymentVersion = 10
        val paymentRequestBinary =
            TransactId.createPaymentRequest(TestData.PaymentRequest.PAYMENT_DETAILS, keyPairPemX509, paymentVersion)
        val paymentRequest = TransactId.parsePaymentRequest(paymentRequestBinary)

        assert(paymentRequest.paymentDetailsVersion == paymentVersion)
        assert(paymentRequest.pkiData == keyPairPemX509.certificatePem)
        assert(paymentRequest.pkiType == keyPairPemX509.type.value)
        assert(paymentRequest.paymentDetails.network == TestData.PaymentRequest.PAYMENT_DETAILS.network)
        assert(paymentRequest.paymentDetails.outputs == TestData.PaymentRequest.PAYMENT_DETAILS.outputs)
        assert(paymentRequest.paymentDetails.time == TestData.PaymentRequest.PAYMENT_DETAILS.time)
        assert(paymentRequest.paymentDetails.expires == TestData.PaymentRequest.PAYMENT_DETAILS.expires)
        assert(paymentRequest.paymentDetails.memo == TestData.PaymentRequest.PAYMENT_DETAILS.memo)
        assert(paymentRequest.paymentDetails.paymentUrl == TestData.PaymentRequest.PAYMENT_DETAILS.paymentUrl)
        assert(paymentRequest.paymentDetails.merchantData == TestData.PaymentRequest.PAYMENT_DETAILS.merchantData)
        assert(!paymentRequest.signature.isNullOrBlank())
    }

    @Test
    fun `Validate invalid PaymentRequestBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            TransactId.isPaymentRequestValid("fakePaymentRequest".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentBinary`() {
        val paymentBinary = TransactId.createPayment(TestData.Payment.PAYMENT)

        assert(TransactId.isPaymentValid(paymentBinary))
    }

    @Test
    fun `Create and parse PaymentBinary to Payment`() {
        val paymentBinary = TransactId.createPayment(TestData.Payment.PAYMENT)
        val payment = TransactId.parsePayment(paymentBinary)

        assert(payment.merchantData == TestData.Payment.PAYMENT.merchantData)
        assert(payment.transactions.size == TestData.Payment.PAYMENT.transactions.size)
        assert(payment.outputs == TestData.Payment.PAYMENT.outputs)
        assert(payment.memo == TestData.Payment.PAYMENT.memo)
    }

    @Test
    fun `Validate invalid PaymentBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            TransactId.isPaymentValid("fakePaymentBinary".toByteArray())
        }
    }

    @Test
    fun `Create and validate PaymentAckBinary`() {
        val paymentAckBinary = TransactId.createPaymentAck(TestData.Payment.PAYMENT, TestData.Payment.MEMO)

        assert(TransactId.isPaymentAckValid(paymentAckBinary))
    }

    @Test
    fun `Create and parse PaymentAckBinary to PaymentAck`() {
        val paymentBinary = TransactId.createPaymentAck(TestData.Payment.PAYMENT, TestData.Payment.MEMO)
        val paymentAck = TransactId.parsePaymentAck(paymentBinary)

        assert(paymentAck.payment.merchantData == TestData.Payment.PAYMENT.merchantData)
        assert(paymentAck.payment.transactions.size == TestData.Payment.PAYMENT.transactions.size)
        assert(paymentAck.payment.outputs == TestData.Payment.PAYMENT.outputs)
        assert(paymentAck.payment.memo == TestData.Payment.PAYMENT.memo)
        assert(paymentAck.memo == TestData.Payment.MEMO)
    }

    @Test
    fun `Validate invalid PaymentAckBinary`() {
        assertThrows(InvalidObjectException::class.java) {
            TransactId.isPaymentAckValid("fakePaymentAckBinary".toByteArray())
        }
    }
}
