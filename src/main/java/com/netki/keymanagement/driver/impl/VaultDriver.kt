package com.netki.keymanagement.driver.impl

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.netki.keymanagement.driver.KeyManagementDriver
import java.util.*

/**
 * Implementation of KeyManagementDriver using Vault.
 */
const val CERTS_SCHEMA = "certs/user/attestations/"
const val PRIVATE_KEY_SCHEMA = "keys/user/priv/"
const val CERTIFICATE_KEY = "certificate_key"
const val PRIVATE_KEY_KEY = "private_key_key"

class VaultDriver : KeyManagementDriver {

    // TODO: replace with the correct configuration
    private val config: VaultConfig = VaultConfig()
        .address("http://127.0.0.1:8200")
        .token("00000000-0000-0000-0000-000000000000")
        .build()

    private val vault = Vault(config)

    /**
     * {@inheritDoc}
     */
    override fun storeCertificatePem(certificateId: String, certificatePem: String) {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[CERTIFICATE_KEY] = certificatePem
        vault.logical().write("$CERTS_SCHEMA/$certificateId", secrets)
    }

    /**
     * {@inheritDoc}
     */
    override fun storePrivateKeyPem(privateKeyId: String, privateKeyPem: String) {
        val secrets: MutableMap<String, Any> = HashMap()
        secrets[PRIVATE_KEY_KEY] = privateKeyPem
        vault.logical().write("$PRIVATE_KEY_SCHEMA/$privateKeyId", secrets)
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
