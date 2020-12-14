package com.netki.address.info.repo.data

import com.google.gson.annotations.SerializedName
import com.netki.model.AddressTagInformation

data class MerkleTagInformation(

    @SerializedName("tag_name_verbose")
    val tagNameVerbose: String? = null,

    @SerializedName("tag_subtype_verbose")
    val tagSubtypeVerbose: String? = null,

    @SerializedName("tag_type_verbose")
    val tagTypeVerbose: String? = null,

    @SerializedName("total_value_usd")
    val totalValueUsd: String? = null
)

internal fun MerkleTagInformation.toAddressTagInformation() = AddressTagInformation(
    tagNameVerbose = this.tagNameVerbose,
    tagSubtypeVerbose = this.tagSubtypeVerbose,
    tagTypeVerbose = this.tagTypeVerbose,
    totalValueUsd = this.totalValueUsd
)
