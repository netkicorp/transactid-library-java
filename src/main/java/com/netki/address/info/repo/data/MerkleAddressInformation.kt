package com.netki.address.info.repo.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.netki.model.AddressInformation

internal data class MerkleAddressInformation(

    @SerializedName("alerts")
    val merkleAddressAlerts: List<MerkleAddressAlert>?,

    @SerializedName("balance")
    val balance: Double?,

    @SerializedName("case_status")
    val caseStatus: Any?,

    @SerializedName("case_status_verbose")
    val caseStatusVerbose: Any?,

    @SerializedName("currency")
    val currency: Int?,

    @SerializedName("currency_verbose")
    val currencyVerbose: String?,

    @SerializedName("earliest_transaction_time")
    val earliestTransactionTime: String?,

    @SerializedName("identifier")
    val identifier: String?,

    @SerializedName("latest_transaction_time")
    val latestTransactionTime: String?,

    @SerializedName("risk_level")
    val riskLevel: Int?,

    @SerializedName("risk_level_verbose")
    val riskLevelVerbose: String?,

    @SerializedName("total_incoming_value")
    val totalIncomingValue: String?,

    @SerializedName("total_incoming_value_usd")
    val totalIncomingValueUsd: String?,

    @SerializedName("total_outgoing_value")
    val totalOutgoingValue: String?,

    @SerializedName("total_outgoing_value_usd")
    val totalOutgoingValueUsd: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("created_at")
    val createdAt: String?
)

internal fun MerkleAddressInformation.toAddressInformation(gson: Gson) = AddressInformation(
    this.identifier,
    this.merkleAddressAlerts?.map { it.toAlert(gson) },
    this.balance,
    this.currency,
    this.currencyVerbose,
    this.earliestTransactionTime,
    this.latestTransactionTime,
    this.riskLevel,
    this.riskLevelVerbose,
    this.totalIncomingValue,
    this.totalIncomingValueUsd,
    this.totalOutgoingValue,
    this.totalOutgoingValueUsd,
    this.createdAt,
    this.updatedAt
)
