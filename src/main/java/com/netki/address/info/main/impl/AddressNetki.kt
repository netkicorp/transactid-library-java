package com.netki.address.info.main.impl

import com.netki.address.info.main.Address
import com.netki.address.info.service.AddressService
import com.netki.model.AddressCurrency

internal class AddressNetki(
    private val addressService: AddressService
) : Address {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: AddressCurrency, address: String) =
        addressService.getAddressInformation(currency, address)
}
