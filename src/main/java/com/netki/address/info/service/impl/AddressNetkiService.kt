package com.netki.address.info.service.impl

import com.netki.address.info.main.AddressInformationProvider
import com.netki.address.info.service.AddressService
import com.netki.model.AddressCurrency

internal class AddressNetkiService(
    private val addressInformationProvider: AddressInformationProvider
) : AddressService {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: AddressCurrency, address: String) =
        addressInformationProvider.getAddressInformation(currency, address)
}
