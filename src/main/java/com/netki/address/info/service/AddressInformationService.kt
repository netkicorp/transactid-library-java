package com.netki.address.info.service

import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.model.AddressInformation

interface AddressInformationService {

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
    fun getAddressInformation(currency: AddressCurrency, address: String): AddressInformation
}
