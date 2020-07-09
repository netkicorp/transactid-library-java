package com.netki.address.info.main

import com.netki.model.AddressInformation

/**
 * Fetch the detailed information about an address.
 */
interface AddressInformation {

    /**
     * Fetch the information of a given address.
     *
     * @param currency of the address.
     * @param address to fetch the information.
     * @return information of the wallet address.
     */
    fun getAddressInformation(currency: Int, address: String): AddressInformation
}
