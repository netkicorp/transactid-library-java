package com.netki.keymanagement.main.impl

import com.netki.exceptions.*
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_INFORMATION_STRING_NOT_CORRECT_ERROR_PROVIDER
import com.netki.keygeneration.main.KeyGeneration
import com.netki.keymanagement.driver.impl.VaultDriver
import com.netki.keymanagement.service.impl.KeyManagementNetkiService
import com.netki.model.Attestation
import com.netki.model.AttestationInformation
import com.netki.model.IvmsConstraint
import com.netki.security.toCertificate
import com.netki.security.toPrivateKey
import com.netki.util.TestData
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import java.security.cert.X509Certificate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KeyManagementNetkiTest {

    private lateinit var keyManagement: KeyManagementNetki
    private lateinit var mockDriver: VaultDriver
    private lateinit var mockKeyGeneration: KeyGeneration

    @BeforeAll
    fun setUp() {
        mockDriver = Mockito.mock(VaultDriver::class.java)
        mockKeyGeneration = Mockito.mock(KeyGeneration::class.java)
        val keyManagementService = KeyManagementNetkiService(mockKeyGeneration, mockDriver)
        keyManagement = KeyManagementNetki(keyManagementService)
    }

    @BeforeEach
    fun resetMock() {
        Mockito.reset(mockDriver)
        Mockito.reset(mockKeyGeneration)
    }

    @Test
    fun `Store certificate on PEM format successfully`() {
        doNothing().`when`(mockDriver).storeCertificatePem(Mockito.anyString(), Mockito.anyString())

        val idResult = keyManagement.storeCertificatePem(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)

        assert(idResult.isNotBlank())
    }

    @Test
    fun `Store certificate on PEM format invalid certificate`() {
        doNothing().`when`(mockDriver).storeCertificatePem(Mockito.anyString(), Mockito.anyString())

        val exception = assertThrows(InvalidCertificateException::class.java) {
            keyManagement.storeCertificatePem("this is not a valid certificate")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid x509 certificate"))
    }

    @Test
    fun `Store certificate on PEM format driver error`() {
        `when`(mockDriver.storeCertificatePem(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val exception = assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storeCertificatePem(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the certificate"))
    }

    @Test
    fun `Store certificate object successfully`() {
        doNothing().`when`(mockDriver).storeCertificatePem(anyString(), anyString())

        val certificateObject =
            TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM.toCertificate() as X509Certificate
        val idResult = keyManagement.storeCertificate(certificateObject)

        assert(!idResult.isBlank())
    }

    @Test
    fun `Store certificate object driver error`() {
        `when`(mockDriver.storeCertificatePem(anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val certificateObject =
            TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM.toCertificate() as X509Certificate
        val exception = assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storeCertificate(certificateObject)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the certificate"))
    }

    @Test
    fun `Store private key on PEM format successfully`() {
        doNothing().`when`(mockDriver).storePrivateKeyPem(Mockito.anyString(), Mockito.anyString())

        val idResult = keyManagement.storePrivateKeyPem(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)

        assert(!idResult.isBlank())
    }

    @Test
    fun `Store private key on PEM format invalid private key`() {
        doNothing().`when`(mockDriver).storePrivateKeyPem(Mockito.anyString(), Mockito.anyString())

        val exception = assertThrows(InvalidPrivateKeyException::class.java) {
            keyManagement.storePrivateKeyPem("this is not a valid private key")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid private key"))
    }

    @Test
    fun `Store private key on PEM format driver error`() {
        `when`(mockDriver.storePrivateKeyPem(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val exception = assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storePrivateKeyPem(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the private key"))
    }

    @Test
    fun `Store private key object successfully`() {
        doNothing().`when`(mockDriver).storePrivateKeyPem(Mockito.anyString(), Mockito.anyString())

        val privateKeyObject = TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE.toPrivateKey()
        val idResult = keyManagement.storePrivateKey(privateKeyObject)

        assert(!idResult.isBlank())
    }

    @Test
    fun `Store private key object driver error`() {
        `when`(mockDriver.storePrivateKeyPem(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val privateKeyObject = TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE.toPrivateKey()
        val exception = assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storePrivateKey(privateKeyObject)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the private key"))
    }

    @Test
    fun `Fetch certificate on PEM format successfully`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)

        val certificate = keyManagement.fetchCertificatePem("12345")

        assert(!certificate.isBlank())
    }

    @Test
    fun `Fetch certificate on PEM format driver error`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchCertificatePem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the certificate"))
    }

    @Test
    fun `Fetch certificate on PEM format invalid certificate`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn("invalid certificate")
        val exception = assertThrows(InvalidCertificateException::class.java) {
            keyManagement.fetchCertificatePem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid x509 certificate"))
    }

    @Test
    fun `Fetch certificate on PEM format not found`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn(null)
        val exception = assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchCertificatePem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Certificate not found for id"))
    }

    @Test
    fun `Fetch certificate object successfully`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)

        val certificate = keyManagement.fetchCertificate("12345")

        assertNotNull(certificate)
    }

    @Test
    fun `Fetch certificate object driver error`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchCertificate("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the certificate"))
    }

    @Test
    fun `Fetch certificate oobject invalid certificate`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn("invalid certificate")
        val exception = assertThrows(InvalidCertificateException::class.java) {
            keyManagement.fetchCertificate("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid x509 certificate"))
    }

    @Test
    fun `Fetch certificate object not found`() {
        `when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn(null)
        val exception = assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchCertificate("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Certificate not found for id"))
    }

    @Test
    fun `Fetch private key on PEM format successfully`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)

        val privateKey = keyManagement.fetchPrivateKeyPem("12345")

        assert(!privateKey.isBlank())
    }

    @Test
    fun `Fetch private key on PEM format driver error`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchPrivateKeyPem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the private key"))
    }

    @Test
    fun `Fetch private key on PEM format invalid certificate`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn("invalid private key")
        val exception = assertThrows(InvalidPrivateKeyException::class.java) {
            keyManagement.fetchPrivateKeyPem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid private key"))
    }

    @Test
    fun `Fetch private key on PEM format not found`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn(null)
        val exception = assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchPrivateKeyPem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Private key not found for id"))
    }

    @Test
    fun `Fetch private key object successfully`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)

        val certificate = keyManagement.fetchPrivateKey("12345")

        assertNotNull(certificate)
    }

    @Test
    fun `Fetch private key object driver error`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchPrivateKey("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the private key"))
    }

    @Test
    fun `Fetch private key object invalid private key`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn("invalid private key")
        val exception = assertThrows(InvalidPrivateKeyException::class.java) {
            keyManagement.fetchPrivateKey("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid private key"))
    }

    @Test
    fun `Fetch private key object not found`() {
        `when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn(null)
        val exception = assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchPrivateKey("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Private key not found for id"))
    }

    @Test
    fun `Generate certificate for attestations with invalid data`() {
        val attestationInformation = AttestationInformation(
            Attestation.LEGAL_PERSON_NAME,
            IvmsConstraint.LEGL,
            "This is invalid data #$#$#$"
        )
        val attestationInformationInvalid = listOf(attestationInformation)

        val exception = assertThrows(CertificateProviderException::class.java) {
            keyManagement.generateCertificates(attestationInformationInvalid)
        }

        assert(
            exception.message != null && exception.message!!.contains(
                String.format(
                    CERTIFICATE_INFORMATION_STRING_NOT_CORRECT_ERROR_PROVIDER,
                    attestationInformation.data,
                    attestationInformation.attestation
                )
            )
        )
    }
}

