package com.netki.bip75.config

import com.netki.address.info.repo.impl.MerkleRepo
import com.netki.address.info.service.impl.AddressInformationNetkiService
import com.netki.bip75.main.Bip75
import com.netki.bip75.main.impl.Bip75Netki
import com.netki.bip75.service.Bip75Service
import com.netki.bip75.service.impl.Bip75ServiceNetki
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature

/**
 * Factory to generate Bip75 instance.
 */
internal object Bip75Factory {

    /**
     * Get an instance of Bip75.
     * @param authorizationKey pass this parameter if address information will be required.
     * @return Bip75 instance.
     */
    @JvmOverloads
    fun getInstance(authorizationKey: String? = null): Bip75 {
        val client: HttpClient by lazy {
            HttpClient(OkHttp) {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
            }
        }

        val addressInformationRepo = MerkleRepo(client, authorizationKey ?: "")

        val addressInformationService = AddressInformationNetkiService(addressInformationRepo)

        val bip75Service: Bip75Service = Bip75ServiceNetki(addressInformationService)

        return Bip75Netki(bip75Service)
    }
}
