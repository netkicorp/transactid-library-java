package com.netki.keymanagement.driver.impl

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.bettercloud.vault.response.LogicalResponse
import com.netki.exceptions.KeyManagementStoreException
import com.netki.keymanagement.driver.KeyManagementDriver
import java.util.*

/**
 * Implementation of KeyManagementDriver using Vault.
 */
private const val CERTS_SCHEMA = "certs/user/attestations/"
private const val PRIVATE_KEY_SCHEMA = "keys/user/priv/"
private const val CERTIFICATE_KEY = "certificate_key"
private const val PRIVATE_KEY_KEY = "private_key_key"

internal class VaultDriver(
    private val vault: Vault
) : KeyManagementDriver {

    /**
     * {@inheritDoc}
     */
    override fun storeCertificatePem(certificateId: String, certificatePem: String) {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[CERTIFICATE_KEY] = certificatePem
        val logicalResponse = vault.logical().write("$CERTS_SCHEMA/$certificateId", secrets)
        validateLogicalResponse(logicalResponse)
    }

    /**
     * {@inheritDoc}
     */
    override fun storePrivateKeyPem(privateKeyId: String, privateKeyPem: String) {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[PRIVATE_KEY_KEY] = privateKeyPem
        val logicalResponse = vault.logical().write("$PRIVATE_KEY_SCHEMA/$privateKeyId", secrets)
        validateLogicalResponse(logicalResponse)
    }

    private fun validateLogicalResponse(logicalResponse: LogicalResponse) {
        if (logicalResponse.restResponse.status > 299) {
            throw KeyManagementStoreException(logicalResponse.restResponse.body.toString(Charsets.UTF_8))
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun fetchCertificatePem(certificateId: String): String? = vault.logical()
        .read("$CERTS_SCHEMA/$certificateId")
        .data[CERTIFICATE_KEY]

    /**
     * {@inheritDoc}
     */
    override fun fetchPrivateKeyPem(privateKeyId: String): String? = vault.logical()
        .read("$PRIVATE_KEY_SCHEMA/$privateKeyId")
        .data[PRIVATE_KEY_KEY]
}
