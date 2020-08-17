package com.netki.address.info.config

import com.google.gson.Gson
import com.netki.address.info.main.AddressInformationProvider
import com.netki.address.info.main.impl.AddressInformationProviderNetki
import com.netki.address.info.repo.impl.MerkleRepo
import com.netki.address.info.service.impl.AddressInformationNetkiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature

/**
 * Factory to generate AddressInformation instance.
 */
object AddressInformationFactory {

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

        val gson = Gson()

        val addressInformationRepo = MerkleRepo(client, authorizationKey, gson)

        val addressInformationService = AddressInformationNetkiService(addressInformationRepo)

        return AddressInformationProviderNetki(addressInformationService)
    }
}
