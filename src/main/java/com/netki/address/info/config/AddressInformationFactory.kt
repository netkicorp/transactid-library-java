package com.netki.address.info.config

import com.netki.address.info.main.AddressInformation
import com.netki.address.info.main.impl.AddressInformationNetki
import com.netki.address.info.repo.impl.MerkleRepo
import com.netki.address.info.service.impl.AddressInformationNetkiService

/**
 * Factory to generate AddressInformation instance.
 */
object AddressInformationFactory {

    /**
     * Get an instance of AddressInformation.
     *
     * @return AddressInformation instance.
     */
    fun getInstance(): AddressInformation {

        val addressInformationRepo = MerkleRepo()

        val addressInformationService = AddressInformationNetkiService(addressInformationRepo)

        return AddressInformationNetki(addressInformationService)
    }
}
