package com.netki.keymanagement.config

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.netki.keymanagement.driver.KeyManagementDriver
import com.netki.keymanagement.driver.impl.VaultDriver
import com.netki.keymanagement.main.KeyManagement
import com.netki.keymanagement.main.impl.KeyManagementNetki
import com.netki.keymanagement.repo.CertificateProvider
import com.netki.keymanagement.repo.impl.NetkiCertificateProvider
import com.netki.keymanagement.service.KeyManagementService
import com.netki.keymanagement.service.impl.KeyManagementNetkiService
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*

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

        val client: HttpClient by lazy {
            HttpClient(OkHttp) {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
            }
        }

        val certificateProvider: CertificateProvider =
            NetkiCertificateProvider(client, authorizationCertificateProviderKey, authorizationCertificateProviderUrl)

        val config: VaultConfig = VaultConfig()
            .address(addressSecureStorage)
            .token(authorizationSecureStorageKey)
            .build()

        val vault = Vault(config)

        val keyManagementDriver: KeyManagementDriver = VaultDriver(vault)

        val keyManagementService: KeyManagementService =
            KeyManagementNetkiService(certificateProvider, keyManagementDriver)

        return KeyManagementNetki(keyManagementService)
    }
}
