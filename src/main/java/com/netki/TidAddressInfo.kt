package com.netki

import com.netki.address.info.config.AddressInformationFactory
import com.netki.address.info.main.AddressInformationProvider
import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.model.AddressInformation

/**
 * Fetch the detailed information about an address.
 */
class TidAddressInfo(private val addressInformationProvider: AddressInformationProvider) {

    companion object {
        /**
         * Method to get an instance of this class.
         *
         * @param authorizationKey Key to connect to an external address provider.
         * @return instance of TidAddressInfo.
         */
        @JvmStatic
        fun getInstance(authorizationKey: String): TidAddressInfo {
            val addressInformation = AddressInformationFactory.getInstance(authorizationKey)
            return TidAddressInfo(addressInformation)
        }
    }

    /**
     * Fetch the information of a given address.
     *
     * @param currency of the address.
     * @param address to fetch the information.
     * @throws AddressProviderErrorException if there is an error fetching the information from the provider.
     * @throws AddressProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
     * @return information of the address.
     */
    @Throws(AddressProviderErrorException::class, AddressProviderUnauthorizedException::class)
    fun getAddressInformation(currency: AddressCurrency, address: String): AddressInformation =
        addressInformationProvider.getAddressInformation(currency, address)
}
