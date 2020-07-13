package com.netki

import com.netki.address.info.config.AddressInformationFactory
import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.model.AddressInformation

/**
 * Fetch the detailed information about an address.
 */
object TidAddressInfo {

    /**
     * Key to connect to an external address provider.
     */
    private var authorizationKey: String = ""

    /**
     * Instance to access the address information.
     */
    private val addressInformation by lazy {
        AddressInformationFactory.getInstance(authorizationKey)
    }

    /**
     * Method to initialize the address info provider.
     * Make sure to call this method before any other one in this class.
     *
     * @param authorizationKey to fetch the required data.
     */
    fun init(authorizationKey: String) {
        this.authorizationKey = authorizationKey
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
        addressInformation.getAddressInformation(currency, address)
}
