package com.netki.address.info.config

import com.netki.address.info.main.AddressInformationProvider
import com.netki.address.info.main.impl.AddressInformationProviderNetki
import com.netki.address.info.repo.impl.MerkleRepo
import com.netki.address.info.service.impl.AddressInformationNetkiService
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*

/**
 * Factory to generate AddressInformation instance.
 */
internal object AddressInformationFactory {

    /**
     * Get an instance of AddressInformation.
     *
     * @return AddressInformation instance.
     */
    fun getInstance(authorizationKey: String): AddressInformationProvider {
        val client: HttpClient by lazy {
            HttpClient(OkHttp) {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
            }
        }

        val addressInformationRepo = MerkleRepo(client, authorizationKey)

        val addressInformationService = AddressInformationNetkiService(addressInformationRepo)

        return AddressInformationProviderNetki(addressInformationService)
    }
}
