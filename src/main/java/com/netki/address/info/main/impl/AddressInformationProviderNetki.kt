package com.netki.address.info.main.impl

import com.netki.address.info.main.AddressInformationProvider
import com.netki.address.info.service.AddressInformationService
import com.netki.model.AddressCurrency

internal class AddressInformationProviderNetki(
    private val addressInformationService: AddressInformationService
) : AddressInformationProvider {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: AddressCurrency, address: String) =
        addressInformationService.getAddressInformation(currency, address)
}
