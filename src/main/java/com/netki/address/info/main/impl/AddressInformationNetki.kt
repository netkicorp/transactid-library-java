package com.netki.address.info.main.impl

import com.netki.address.info.main.AddressInformation
import com.netki.address.info.service.AddressInformationService

class AddressInformationNetki(
    private val addressInformationService: AddressInformationService
) : AddressInformation {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: Int, address: String) =
        addressInformationService.getAddressInformation(currency, address)
}
