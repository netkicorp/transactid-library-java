package com.netki.address.info.repo.data

import com.google.gson.annotations.SerializedName
import com.netki.model.RiskType

internal data class MerkleAddressRiskType(

    @SerializedName("risk_type")
    val riskType: Int?,

    @SerializedName("risk_type_verbose")
    val riskTypeVerbose: String?
)

internal fun MerkleAddressRiskType.toRiskType() = RiskType(
    this.riskType,
    this.riskTypeVerbose
)
