package com.netki

import com.netki.address.info.config.AddressInformationFactory

/**
 * Fetch the detailed information about an address.
 */
object TidAddressInfo {

    /**
     * Instance to access the address information.
     */
    private val addressInformation = AddressInformationFactory.getInstance()

    /**
     * Fetch the information of a given address.
     *
     * @param currency of the address.
     * @param address to fetch the information.
     * @return information of the wallet address.
     */
    fun getAddressInformation(currency: Int, address: String) =
        addressInformation.getAddressInformation(currency, address)
}
