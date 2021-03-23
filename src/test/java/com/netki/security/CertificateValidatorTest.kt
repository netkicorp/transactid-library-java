package com.netki.security

import com.netki.exceptions.ExceptionInformation.CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND
import com.netki.exceptions.InvalidCertificateException
import com.netki.model.CertificateChain
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_CHAIN_ONE
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_CHAIN_THREE_BUNDLE
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_CHAIN_TWO
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM
import com.netki.util.TestData.KeyPairs.INTERMEDIATE_CERTIFICATE_RANDOM
import com.netki.util.TestData.KeyPairs.ROOT_CERTIFICATE_RANDOM
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.security.cert.X509Certificate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CertificateValidatorTest {

    private val rootCertificateRandom = ROOT_CERTIFICATE_RANDOM.toCertificate() as X509Certificate
    private val intermediateCertificateRandom =
        INTERMEDIATE_CERTIFICATE_RANDOM.toCertificate() as X509Certificate
    private val certificateValidator = CertificateValidator("src/test/resources/certificates")

    @Test
    fun `Verify correct certificate chain for client certificate one`() {
        assert(certificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_CHAIN_ONE))
    }

    @Test
    fun `Verify correct certificate chain for client certificate two`() {
        assert(certificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_CHAIN_TWO))
    }

    @Test
    fun `Verify correct certificate chain for client certificate three bundle`() {
        assert(certificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_CHAIN_THREE_BUNDLE))
    }

    @Test
    fun `Verify incorrect certificate chain for client certificate`() {
        assert(!certificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_RANDOM))
    }

    @Test
    fun `Verify incorrect input root certificate for validateCertificateChain`() {
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            val certificateChains = listOf(
                CertificateChain(intermediateCertificateRandom, mutableListOf(intermediateCertificateRandom))
            )
            certificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_RANDOM, certificateChains)
        }
        assert(exception.message?.contains("is not a valid") ?: false)
    }

    @Test
    fun `Verify incorrect input intermediate certificate for validateCertificateChain`() {
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            val certificateChains = listOf(
                CertificateChain(rootCertificateRandom, mutableListOf(rootCertificateRandom))
            )
            certificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_RANDOM, certificateChains)
        }
        assert(exception.message?.contains("is not a valid") ?: false)
    }

    @Test
    fun `Verify incorrect input client certificate for validateCertificateChain`() {
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            val certificateChains = listOf(
                CertificateChain(rootCertificateRandom, mutableListOf(intermediateCertificateRandom))
            )
            certificateValidator.validateCertificateChain(ROOT_CERTIFICATE_RANDOM, certificateChains)
        }
        assert(exception.message?.contains(CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND) ?: false)
    }
}
