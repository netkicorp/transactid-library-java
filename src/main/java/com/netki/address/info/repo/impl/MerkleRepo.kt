package com.netki.address.info.repo.impl

import com.netki.address.info.repo.AddressInformationRepo
import com.netki.model.AddressInformation

/**
 * Implementation to fetch the address information from Merkle provider.
 */
class MerkleRepo : AddressInformationRepo {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: Int, address: String): AddressInformation {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
