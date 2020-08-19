package com.netki.keymanagement.config

import com.netki.keymanagement.driver.KeyManagementDriver
import com.netki.keymanagement.driver.impl.VaultDriver
import com.netki.keymanagement.main.KeyManagement
import com.netki.keymanagement.main.impl.KeyManagementNetki
import com.netki.keymanagement.repo.CertificateProvider
import com.netki.keymanagement.repo.impl.NetkiCertificateProvider
import com.netki.keymanagement.service.KeyManagementService
import com.netki.keymanagement.service.impl.KeyManagementNetkiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature

/**
 * Factory to generate KeyManagement instance.
 */
object KeyManagementFactory {

    /**
     * Get an instance of KeyManagement.
     *
     * @return KeyManagement instance.
     */
    fun getInstance(
        authorizationCertificateProviderKey: String,
        authorizationSecureStorageKey: String,
        addressSecureStorage: String
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
            NetkiCertificateProvider(client, authorizationCertificateProviderKey)

        val keyManagementDriver: KeyManagementDriver = VaultDriver(authorizationSecureStorageKey, addressSecureStorage)

        val keyManagementService: KeyManagementService =
            KeyManagementNetkiService(certificateProvider, keyManagementDriver)

        return KeyManagementNetki(keyManagementService)
    }
}
