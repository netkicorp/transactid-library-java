package com.netki.address.info.service

import com.netki.model.AddressInformation

interface AddressInformationService {

    /**
     * Fetch the information of a given address.
     *
     * @param currency of the address.
     * @param address to fetch the information.
     * @return information of the wallet address.
     */
    fun getAddressInformation(currency: Int, address: String): AddressInformation
}
