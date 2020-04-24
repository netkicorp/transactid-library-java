package com.netki.security

import com.netki.exceptions.InvalidCertificateException
import com.netki.model.CertificateChain
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND
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

    private val rootCertificateRandom = CryptoModule.certificatePemToObject(ROOT_CERTIFICATE_RANDOM) as X509Certificate
    private val intermediateCertificateRandom =
        CryptoModule.certificatePemToObject(INTERMEDIATE_CERTIFICATE_RANDOM) as X509Certificate

    @Test
    fun `Verify correct certificate chain for client certificate one`() {
        assert(CertificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_CHAIN_ONE))
    }

    @Test
    fun `Verify correct certificate chain for client certificate two`() {
        assert(CertificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_CHAIN_TWO))
    }

    @Test
    fun `Verify correct certificate chain for client certificate three bundle`() {
        assert(CertificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_CHAIN_THREE_BUNDLE))
    }

    @Test
    fun `Verify incorrect certificate chain for client certificate`() {
        assert(!CertificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_RANDOM))
    }

    @Test
    fun `Verify incorrect input root certificate for validateCertificateChain`() {
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            val certificateChains = listOf(
                CertificateChain(intermediateCertificateRandom, mutableListOf(intermediateCertificateRandom))
            )
            CertificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_RANDOM, certificateChains)
        }
        assert(exception.message?.contains("is not a valid") ?: false)
    }

    @Test
    fun `Verify incorrect input intermediate certificate for validateCertificateChain`() {
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            val certificateChains = listOf(
                CertificateChain(rootCertificateRandom, mutableListOf(rootCertificateRandom))
            )
            CertificateValidator.validateCertificateChain(CLIENT_CERTIFICATE_RANDOM, certificateChains)
        }
        assert(exception.message?.contains("is not a valid") ?: false)
    }

    @Test
    fun `Verify incorrect input client certificate for validateCertificateChain`() {
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            val certificateChains = listOf(
                CertificateChain(rootCertificateRandom, mutableListOf(intermediateCertificateRandom))
            )
            CertificateValidator.validateCertificateChain(ROOT_CERTIFICATE_RANDOM, certificateChains)
        }
        assert(exception.message?.contains(CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND) ?: false)
    }

    @Test
    fun `Verify root certificate is self signed`() {
        assert(rootCertificateRandom.isSelfSigned())
    }

    @Test
    fun `Verify intermediate certificate is not self signed`() {
        assert(!intermediateCertificateRandom.isSelfSigned())
    }

    @Test
    fun `Verify client certificate is not self signed`() {
        assert(!intermediateCertificateRandom.isSelfSigned())
    }
}
