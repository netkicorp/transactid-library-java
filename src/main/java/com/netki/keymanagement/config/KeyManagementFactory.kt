package com.netki.keymanagement.config

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.netki.keygeneration.config.KeyGenerationFactory
import com.netki.keygeneration.main.KeyGeneration
import com.netki.keymanagement.driver.KeyManagementDriver
import com.netki.keymanagement.driver.impl.VaultDriver
import com.netki.keymanagement.main.KeyManagement
import com.netki.keymanagement.main.impl.KeyManagementNetki
import com.netki.keymanagement.service.KeyManagementService
import com.netki.keymanagement.service.impl.KeyManagementNetkiService

/**
 * Factory to generate KeyManagement instance.
 */
internal object KeyManagementFactory {

    /**
     * Get an instance of KeyManagement.
     *
     * @return KeyManagement instance.
     */
    fun getInstance(
        authorizationCertificateProviderKey: String,
        authorizationSecureStorageKey: String,
        addressSecureStorage: String,
        authorizationCertificateProviderUrl: String
    ): KeyManagement {

        val keyGeneration: KeyGeneration =
            KeyGenerationFactory.getInstance(authorizationCertificateProviderKey, authorizationCertificateProviderUrl)

        val config: VaultConfig = VaultConfig()
            .address(addressSecureStorage)
            .token(authorizationSecureStorageKey)
            .build()

        val vault = Vault(config)

        val keyManagementDriver: KeyManagementDriver = VaultDriver(vault)

        val keyManagementService: KeyManagementService =
            KeyManagementNetkiService(keyGeneration, keyManagementDriver)

        return KeyManagementNetki(keyManagementService)
    }
}
