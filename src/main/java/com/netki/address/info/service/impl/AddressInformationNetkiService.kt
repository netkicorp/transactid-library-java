package com.netki.address.info.service.impl

import com.netki.address.info.repo.AddressInformationRepo
import com.netki.address.info.service.AddressInformationService
import com.netki.model.AddressCurrency

internal class AddressInformationNetkiService(
    private val addressInformationRepo: AddressInformationRepo
) : AddressInformationService {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: AddressCurrency, address: String) =
        addressInformationRepo.getAddressInformation(currency, address)
}
