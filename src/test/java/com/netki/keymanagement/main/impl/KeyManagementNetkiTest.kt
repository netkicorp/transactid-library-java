package com.netki.keymanagement.main.impl

import com.netki.exceptions.*
import com.netki.keymanagement.driver.impl.VaultDriver
import com.netki.keymanagement.repo.data.CertificateAttestationResponse
import com.netki.keymanagement.repo.impl.NetkiCertificateProvider
import com.netki.keymanagement.service.impl.KeyManagementNetkiService
import com.netki.security.CryptoModule
import com.netki.util.TestData
import com.netki.util.TestData.CertificateGeneration.ATTESTATIONS_INFORMATION
import com.netki.util.TestData.CertificateGeneration.ATTESTATIONS_REQUESTED
import com.netki.util.TestData.CertificateGeneration.CERTIFICATE_ATTESTATION_RESPONSE
import com.netki.util.TestData.CertificateGeneration.CSRS_ATTESTATIONS
import com.netki.util.TestData.CertificateGeneration.TRANSACTION_ID
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import java.security.cert.X509Certificate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KeyManagementNetkiTest {

    private lateinit var keyManagement: KeyManagementNetki
    private lateinit var mockDriver: VaultDriver
    private lateinit var mockCertificateProvider: NetkiCertificateProvider

    @BeforeAll
    fun setUp() {
        mockDriver = Mockito.mock(VaultDriver::class.java)
        mockCertificateProvider = Mockito.mock(NetkiCertificateProvider::class.java)
        val keyManagementService = KeyManagementNetkiService(mockCertificateProvider, mockDriver)
        keyManagement = KeyManagementNetki(keyManagementService)
    }

    @BeforeEach
    fun resetMock() {
        Mockito.reset(mockDriver)
        Mockito.reset(mockCertificateProvider)
    }

    @Test
    fun `Generate certificate for attestations successfully`() {
        `when`(mockCertificateProvider.requestTransactionId(ATTESTATIONS_REQUESTED)).thenReturn(TRANSACTION_ID)
        doNothing().`when`(mockCertificateProvider).submitCsrsAttestations(TRANSACTION_ID, CSRS_ATTESTATIONS)
        `when`(mockCertificateProvider.getCertificates(TRANSACTION_ID)).thenReturn(CERTIFICATE_ATTESTATION_RESPONSE)

        val attestationCertificate = keyManagement.generateCertificates(ATTESTATIONS_INFORMATION)

        assertEquals(attestationCertificate.size, CERTIFICATE_ATTESTATION_RESPONSE.count)
    }

    @Test
    fun `Generate certificate for attestations returning empty list of certificates`() {
        `when`(mockCertificateProvider.requestTransactionId(ATTESTATIONS_REQUESTED)).thenReturn(TRANSACTION_ID)
        doNothing().`when`(mockCertificateProvider).submitCsrsAttestations(TRANSACTION_ID, CSRS_ATTESTATIONS)
        `when`(mockCertificateProvider.getCertificates(TRANSACTION_ID)).thenReturn(
            CertificateAttestationResponse(
                0,
                emptyList()
            )
        )

        val attestationCertificate = keyManagement.generateCertificates(ATTESTATIONS_INFORMATION)

        assertTrue(attestationCertificate.isEmpty())
    }

    @Test
    fun `Store certificate on PEM format successfully`() {
        Mockito.doNothing().`when`(mockDriver).storeCertificatePem(Mockito.anyString(), Mockito.anyString())

        val idResult = keyManagement.storeCertificatePem(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)

        assert(!idResult.isBlank())
    }

    @Test
    fun `Store certificate on PEM format invalid certificate`() {
        Mockito.doNothing().`when`(mockDriver).storeCertificatePem(Mockito.anyString(), Mockito.anyString())

        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            keyManagement.storeCertificatePem("this is not a valid certificate")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid x509 certificate"))
    }

    @Test
    fun `Store certificate on PEM format driver error`() {
        Mockito.`when`(mockDriver.storeCertificatePem(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val exception = Assertions.assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storeCertificatePem(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the certificate"))
    }

    @Test
    fun `Store certificate object successfully`() {
        Mockito.doNothing().`when`(mockDriver).storeCertificatePem(Mockito.anyString(), Mockito.anyString())

        val certificateObject =
            CryptoModule.certificatePemToObject(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM) as X509Certificate
        val idResult = keyManagement.storeCertificate(certificateObject)

        assert(!idResult.isBlank())
    }

    @Test
    fun `Store certificate object driver error`() {
        Mockito.`when`(mockDriver.storeCertificatePem(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val certificateObject =
            CryptoModule.certificatePemToObject(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM) as X509Certificate
        val exception = Assertions.assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storeCertificate(certificateObject)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the certificate"))
    }

    @Test
    fun `Store private key on PEM format successfully`() {
        Mockito.doNothing().`when`(mockDriver).storePrivateKeyPem(Mockito.anyString(), Mockito.anyString())

        val idResult = keyManagement.storePrivateKeyPem(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)

        assert(!idResult.isBlank())
    }

    @Test
    fun `Store private key on PEM format invalid private key`() {
        Mockito.doNothing().`when`(mockDriver).storePrivateKeyPem(Mockito.anyString(), Mockito.anyString())

        val exception = Assertions.assertThrows(InvalidPrivateKeyException::class.java) {
            keyManagement.storePrivateKeyPem("this is not a valid private key")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid private key"))
    }

    @Test
    fun `Store private key on PEM format driver error`() {
        Mockito.`when`(mockDriver.storePrivateKeyPem(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val exception = Assertions.assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storePrivateKeyPem(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the private key"))
    }

    @Test
    fun `Store private key object successfully`() {
        Mockito.doNothing().`when`(mockDriver).storePrivateKeyPem(Mockito.anyString(), Mockito.anyString())

        val privateKeyObject = CryptoModule.privateKeyPemToObject(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)
        val idResult = keyManagement.storePrivateKey(privateKeyObject)

        assert(!idResult.isBlank())
    }

    @Test
    fun `Store private key object driver error`() {
        Mockito.`when`(mockDriver.storePrivateKeyPem(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException("Random exception"))

        val privateKeyObject = CryptoModule.privateKeyPemToObject(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)
        val exception = Assertions.assertThrows(KeyManagementStoreException::class.java) {
            keyManagement.storePrivateKey(privateKeyObject)
        }

        assert(exception.message != null && exception.message!!.contains("There was an error storing the private key"))
    }

    @Test
    fun `Fetch certificate on PEM format successfully`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)

        val certificate = keyManagement.fetchCertificatePem("12345")

        assert(!certificate.isBlank())
    }

    @Test
    fun `Fetch certificate on PEM format driver error`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = Assertions.assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchCertificatePem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the certificate"))
    }

    @Test
    fun `Fetch certificate on PEM format invalid certificate`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn("invalid certificate")
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            keyManagement.fetchCertificatePem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid x509 certificate"))
    }

    @Test
    fun `Fetch certificate on PEM format not found`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn(null)
        val exception = Assertions.assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchCertificatePem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Certificate not found for id"))
    }

    @Test
    fun `Fetch certificate object successfully`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM)

        val certificate = keyManagement.fetchCertificate("12345")

        assert(certificate != null)
    }

    @Test
    fun `Fetch certificate object driver error`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = Assertions.assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchCertificate("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the certificate"))
    }

    @Test
    fun `Fetch certificate oobject invalid certificate`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn("invalid certificate")
        val exception = Assertions.assertThrows(InvalidCertificateException::class.java) {
            keyManagement.fetchCertificate("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid x509 certificate"))
    }

    @Test
    fun `Fetch certificate object not found`() {
        Mockito.`when`(mockDriver.fetchCertificatePem(Mockito.anyString())).thenReturn(null)
        val exception = Assertions.assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchCertificate("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Certificate not found for id"))
    }

    @Test
    fun `Fetch private key on PEM format successfully`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)

        val privateKey = keyManagement.fetchPrivateKeyPem("12345")

        assert(!privateKey.isBlank())
    }

    @Test
    fun `Fetch private key on PEM format driver error`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = Assertions.assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchPrivateKeyPem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the private key"))
    }

    @Test
    fun `Fetch private key on PEM format invalid certificate`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn("invalid private key")
        val exception = Assertions.assertThrows(InvalidPrivateKeyException::class.java) {
            keyManagement.fetchPrivateKeyPem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid private key"))
    }

    @Test
    fun `Fetch private key on PEM format not found`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn(null)
        val exception = Assertions.assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchPrivateKeyPem("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Private key not found for id"))
    }

    @Test
    fun `Fetch private key object successfully`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString()))
            .thenReturn(TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE)

        val certificate = keyManagement.fetchPrivateKey("12345")

        assert(certificate != null)
    }

    @Test
    fun `Fetch private key object driver error`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenThrow(RuntimeException("random error"))
        val exception = Assertions.assertThrows(KeyManagementFetchException::class.java) {
            keyManagement.fetchPrivateKey("12345")
        }

        assert(exception.message != null && exception.message!!.contains("There was an error fetching the private key"))
    }

    @Test
    fun `Fetch private key object invalid private key`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn("invalid private key")
        val exception = Assertions.assertThrows(InvalidPrivateKeyException::class.java) {
            keyManagement.fetchPrivateKey("12345")
        }

        assert(exception.message != null && exception.message!!.contains("This is not a valid private key"))
    }

    @Test
    fun `Fetch private key object not found`() {
        Mockito.`when`(mockDriver.fetchPrivateKeyPem(Mockito.anyString())).thenReturn(null)
        val exception = Assertions.assertThrows(ObjectNotFoundException::class.java) {
            keyManagement.fetchPrivateKey("12345")
        }

        assert(exception.message != null && exception.message!!.contains("Private key not found for id"))
    }
}

