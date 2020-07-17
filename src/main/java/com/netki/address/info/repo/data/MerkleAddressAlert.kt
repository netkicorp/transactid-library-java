package com.netki.address.info.repo.data

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.netki.model.Alert

data class MerkleAddressAlert(

    @SerializedName("rule_name")
    val ruleName: String?,

    @SerializedName("risk_level")
    val riskLevel: Int?,

    @SerializedName("risk_level_verbose")
    val riskLevelVerbose: String?,

    @SerializedName("risk_types")
    val merkleAddressRiskTypes: List<MerkleAddressRiskType>?,

    @SerializedName("context")
    val context: JsonObject?,

    @SerializedName("created_at")
    val createdAt: String?
)

fun MerkleAddressAlert.toAlert() = Alert(
    this.ruleName,
    this.riskLevel,
    this.riskLevelVerbose,
    this.merkleAddressRiskTypes?.map { it.toRiskType() },
    this.context,
    this.createdAt
)
