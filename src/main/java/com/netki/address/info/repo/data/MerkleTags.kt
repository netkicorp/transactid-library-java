package com.netki.address.info.repo.data

import com.google.gson.annotations.SerializedName
import com.netki.model.AddressTags

data class MerkleTags(

    @SerializedName("owner")
    val owner: MerkleTagInformation? = null,

    @SerializedName("user")
    val user: MerkleTagInformation? = null
)

internal fun MerkleTags.toAddressTags() = AddressTags(
    owner = this.owner?.toAddressTagInformation(),
    user = this.user?.toAddressTagInformation()
)
