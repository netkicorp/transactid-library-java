package com.netki.address.info.main.impl

import com.netki.address.info.main.AddressInformation
import com.netki.address.info.service.AddressInformationService
import com.netki.model.AddressCurrency

class AddressInformationNetki(
    private val addressInformationService: AddressInformationService
) : AddressInformation {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: AddressCurrency, address: String) =
        addressInformationService.getAddressInformation(currency, address)
}
