package com.netki.ca.certificates

import com.netki.security.CryptoModule
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CaCertificatesTest {

    @BeforeAll
    fun setUp() {
        // Nothing to do here
    }

    @Test
    fun `Validate that the CA production certificate is valid`() {
        val caCertificate = CryptoModule.certificatePemToObject(CaCertificates.TRANSACT_ID_PROD)

        assertNotNull(caCertificate)
        assertNotNull(caCertificate.publicKey)
    }

    @Test
    fun `Validate that the CA development certificate is valid`() {
        val devCertificate = CryptoModule.certificatePemToObject(CaCertificates.TRANSACT_ID_DEV)

        assertNotNull(devCertificate)
        assertNotNull(devCertificate.publicKey)
    }
}
