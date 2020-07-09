package com.netki.address.info.service.impl

import com.netki.address.info.repo.AddressInformationRepo
import com.netki.address.info.service.AddressInformationService

class AddressInformationNetkiService(
    private val addressInformationRepo: AddressInformationRepo
) : AddressInformationService {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: Int, address: String) =
        addressInformationRepo.getAddressInformation(currency, address)
}
