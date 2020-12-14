package com.netki.address.info.repo.data

import com.google.gson.annotations.SerializedName
import com.netki.model.AddressInformation

data class MerkleAddress(

    @SerializedName("identifier")
    val identifier: String? = "",

    @SerializedName("balance")
    val balance: Double? = 0.0,

    @SerializedName("beneficiary")
    val beneficiary: List<MerkleTagInformation>? = null,

    @SerializedName("case_status")
    val caseStatus: Int? = null,

    @SerializedName("case_status_verbose")
    val caseStatusVerbose: String? = null,

    @SerializedName("currency")
    val currency: Int? = null,

    @SerializedName("currency_verbose")
    val currencyVerbose: String? = null,

    @SerializedName("earliest_transaction_time")
    val earliestTransactionTime: String? = null,

    @SerializedName("latest_transaction_time")
    val latestTransactionTime: String? = null,

    @SerializedName("originator")
    val originator: List<MerkleTagInformation>? = null,

    @SerializedName("risk_level")
    val riskLevel: Int? = null,

    @SerializedName("risk_level_verbose")
    val riskLevelVerbose: String? = null,

    @SerializedName("tags")
    val merkleTags: MerkleTags? = null,

    @SerializedName("total_incoming_value")
    val totalIncomingValue: String? = null,

    @SerializedName("total_incoming_value_usd")
    val totalIncomingValueUsd: String? = null,

    @SerializedName("total_outgoing_value")
    val totalOutgoingValue: String? = null,

    @SerializedName("total_outgoing_value_usd")
    val totalOutgoingValueUsd: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)

internal fun MerkleAddress.toAddressInformation() = AddressInformation(
    identifier = this.identifier,
    balance = this.balance,
    beneficiary = this.beneficiary?.map { it.toAddressTagInformation() },
    caseStatus = this.caseStatus,
    caseStatusVerbose = this.caseStatusVerbose,
    currency = this.currency,
    currencyVerbose = this.currencyVerbose,
    earliestTransactionTime = this.earliestTransactionTime,
    latestTransactionTime = this.latestTransactionTime,
    originator = this.originator?.map { it.toAddressTagInformation() },
    riskLevel = this.riskLevel,
    riskLevelVerbose = this.riskLevelVerbose,
    tags = this.merkleTags?.toAddressTags(),
    totalIncomingValue = this.totalIncomingValue,
    totalIncomingValueUsd = this.totalIncomingValueUsd,
    totalOutgoingValue = this.totalOutgoingValue,
    totalOutgoingValueUsd = this.totalOutgoingValueUsd,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt

)
