package com.netki.security

import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_PEM
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM_PEM
import com.netki.util.TestData.KeyPairs.INTERMEDIATE_CERTIFICATE_PEM
import com.netki.util.TestData.KeyPairs.ROOT_CERTIFICATE_PEM
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.security.cert.X509Certificate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CertificateValidatorTest {

    private val rootCertificate = CryptoModule.certificatePemToObject(ROOT_CERTIFICATE_PEM) as X509Certificate
    private val intermediateCertificate =
        CryptoModule.certificatePemToObject(INTERMEDIATE_CERTIFICATE_PEM) as X509Certificate
    private val clientCertificate = CryptoModule.certificatePemToObject(CLIENT_CERTIFICATE_PEM) as X509Certificate
    private val clientCertificateRandom =
        CryptoModule.certificatePemToObject(CLIENT_CERTIFICATE_RANDOM_PEM) as X509Certificate

    @Test
    fun `Verify correct certificate chain for client certificate`() {
        CertificateValidator.validateCertificateChain(clientCertificate)
    }

    @Test
    fun `Verify incorrect certificate chain for client certificate`() {
        Assertions.assertThrows(InvalidCertificateChainException::class.java) {
            CertificateValidator.validateCertificateChain(clientCertificateRandom)
        }
    }

    @Test
    fun `Verify correct certificate chain for validateCertificateChain`() {
        CertificateValidator.validateCertificateChain(
            clientCertificate,
            listOf(intermediateCertificate),
            listOf(rootCertificate)
        )
    }

    @Test
    fun `Verify incorrect certificate chain for validateCertificateChain`() {
        Assertions.assertThrows(InvalidCertificateChainException::class.java) {
            CertificateValidator.validateCertificateChain(
                clientCertificateRandom,
                listOf(intermediateCertificate),
                listOf(rootCertificate)
            )
        }
    }

    @Test
    fun `Verify incorrect input client certificate for validateCertificateChain`() {
        Assertions.assertThrows(InvalidCertificateException::class.java) {
            CertificateValidator.validateCertificateChain(
                rootCertificate,
                listOf(intermediateCertificate),
                listOf(rootCertificate)
            )
        }
    }

    @Test
    fun `Verify incorrect input intermediate certificate for validateCertificateChain`() {
        Assertions.assertThrows(InvalidCertificateException::class.java) {
            CertificateValidator.validateCertificateChain(
                clientCertificate,
                listOf(rootCertificate),
                listOf(rootCertificate)
            )
        }
    }

    @Test
    fun `Verify incorrect input root certificate for validateCertificateChain`() {
        Assertions.assertThrows(InvalidCertificateException::class.java) {
            CertificateValidator.validateCertificateChain(
                clientCertificate,
                listOf(intermediateCertificate),
                listOf(clientCertificate)
            )
        }
    }

    @Test
    fun `Verify root certificate is self signed`() {
        assert(rootCertificate.isSelfSigned())
    }

    @Test
    fun `Verify intermediate certificate is not self signed`() {
        assert(!intermediateCertificate.isSelfSigned())
    }

    @Test
    fun `Verify client certificate is not self signed`() {
        assert(!intermediateCertificate.isSelfSigned())
    }
}
