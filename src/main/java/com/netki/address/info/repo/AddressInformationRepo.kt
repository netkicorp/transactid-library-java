package com.netki.address.info.repo

import com.netki.model.AddressInformation

interface AddressInformationRepo {

    /**
     * Fetch the information of a given address.
     *
     * @param currency of the address.
     * @param address to fetch the information.
     * @return information of the wallet address.
     */
    fun getAddressInformation(currency: Int, address: String): AddressInformation
}
