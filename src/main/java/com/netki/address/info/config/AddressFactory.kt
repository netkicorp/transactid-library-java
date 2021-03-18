package com.netki.address.info.config

import com.netki.address.info.main.Address
import com.netki.address.info.main.impl.AddressNetki
import com.netki.address.info.service.impl.AddressNetkiService

/**
 * Factory to generate Address instance.
 */
internal object AddressFactory {

    /**
     * Get an instance of AddressInformation.
     *
     * @return AddressInformation instance.
     */
    fun getInstance(authorizationKey: String): Address {
        val addressInformationProvider = AddressInformationFactory.getInstance(authorizationKey)

        val addressInformationService = AddressNetkiService(addressInformationProvider)

        return AddressNetki(addressInformationService)
    }
}
