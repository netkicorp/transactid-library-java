package com.netki.address.info.config

import com.netki.address.info.main.AddressInformation
import com.netki.address.info.main.impl.AddressInformationNetki
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
    fun getInstance(authorizationKey: String): AddressInformation {
        val client: HttpClient by lazy {
            HttpClient(OkHttp) {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
            }
        }

        val addressInformationRepo = MerkleRepo(client, authorizationKey)

        val addressInformationService = AddressInformationNetkiService(addressInformationRepo)

        return AddressInformationNetki(addressInformationService)
    }
}
