package com.netki.keymanagement.driver.impl

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.bettercloud.vault.api.Logical
import com.bettercloud.vault.api.Logical.logicalOperations
import com.bettercloud.vault.response.LogicalResponse
import com.bettercloud.vault.rest.RestResponse
import com.netki.exceptions.KeyManagementStoreException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import java.util.*

private const val CERTS_SCHEMA = "certs/user/attestations/"
private const val PRIVATE_KEY_SCHEMA = "keys/user/priv/"
private const val CERTIFICATE_KEY = "certificate_key"
private const val PRIVATE_KEY_KEY = "private_key_key"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VaultDriverTest {

    private lateinit var vaultDriver: VaultDriver
    private lateinit var mockVault: Vault
    private lateinit var mockVaultConfig: VaultConfig
    private lateinit var mockLogical: Logical
    private val certificatePem = "fake_cert"
    private val privateKeyPem = "fake_private_key"
    private val certificateId = "1234"
    private val privateKeyId = "1234"

    @BeforeAll
    fun setUp() {
        mockVault = Mockito.mock(Vault::class.java)
        mockVaultConfig = Mockito.mock(VaultConfig::class.java)
        mockLogical = Mockito.mock(Logical::class.java)
        vaultDriver = VaultDriver(mockVault)
    }

    @Test
    fun `Test store certificate PEM successfully`() {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[CERTIFICATE_KEY] = certificatePem

        Mockito.`when`(mockVault.logical()).thenReturn(mockLogical)
        Mockito.`when`(mockLogical.write("$CERTS_SCHEMA/$certificateId", secrets)).thenReturn(
            LogicalResponse(
                RestResponse(200, "type", "body".toByteArray()),
                1,
                logicalOperations.writeV1
            )
        )

        vaultDriver.storeCertificatePem(certificateId, certificatePem)
    }

    @Test
    fun `Test store certificate PEM with error`() {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[CERTIFICATE_KEY] = certificatePem

        Mockito.`when`(mockVault.logical()).thenReturn(mockLogical)
        Mockito.`when`(mockLogical.write("$CERTS_SCHEMA/$certificateId", secrets)).thenReturn(
            LogicalResponse(
                RestResponse(300, "type", "body".toByteArray()),
                1,
                logicalOperations.writeV1
            )
        )
        Assertions.assertThrows(KeyManagementStoreException::class.java) {
            vaultDriver.storeCertificatePem(certificateId, certificatePem)
        }
    }

    @Test
    fun `Test store privateKey PEM successfully`() {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[PRIVATE_KEY_KEY] = privateKeyPem

        Mockito.`when`(mockVault.logical()).thenReturn(mockLogical)
        Mockito.`when`(mockLogical.write("$PRIVATE_KEY_SCHEMA/$privateKeyId", secrets)).thenReturn(
            LogicalResponse(
                RestResponse(200, "type", "body".toByteArray()),
                1,
                logicalOperations.writeV1
            )
        )

        vaultDriver.storePrivateKeyPem(privateKeyId, privateKeyPem)
    }

    @Test
    fun `Test store privateKey PEM with error`() {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[PRIVATE_KEY_KEY] = privateKeyPem

        Mockito.`when`(mockVault.logical()).thenReturn(mockLogical)
        Mockito.`when`(mockLogical.write("$PRIVATE_KEY_SCHEMA/$privateKeyId", secrets)).thenReturn(
            LogicalResponse(
                RestResponse(300, "type", "body".toByteArray()),
                1,
                logicalOperations.writeV1
            )
        )
        Assertions.assertThrows(KeyManagementStoreException::class.java) {
            vaultDriver.storePrivateKeyPem(privateKeyId, privateKeyPem)
        }
    }
}
