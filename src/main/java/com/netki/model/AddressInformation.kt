package com.netki.model

/**
 * Detailed information about an address.
 */
data class AddressInformation @JvmOverloads constructor(

    /**
     * Address.
     * If blank or empty, not information was found for this address.
     */
    val identifier: String? = "",

    /**
     * Describes all alerts fired for this address.
     */
    val alerts: List<Alert>? = emptyList(),

    /**
     * Total amount in cryptocurrency available with address.
     */
    val balance: Double? = 0.0,

    /**
     * The currency code for the blockchain this address was searched on, [-1] if could not get the currency of the address.
     */
    val currency: Int? = -1,

    /**
     * The currency name for the blockchain this address was searched on.
     */
    val currencyVerbose: String? = "",

    /**
     * Date on which address has made its first transaction.
     */
    val earliestTransactionTime: String? = "",

    /**
     * Date on which address has made its last transaction.
     */
    val latestTransactionTime: String? = "",

    /**
     * An integer indicating if this address is Low Risk [1], Medium Risk [2] or High Risk [3] address or if no risks were detected [0], [-1] if could not fetch the risk level.
     */
    val riskLevel: Int? = -1,

    /**
     * Indicates if this address is Low Risk, Medium Risk , High Risk or if no risks were detected.
     */
    val riskLevelVerbose: String? = "",

    /**
     * Total amount received by the address in cryptocurrency.
     */
    val totalIncomingValue: String? = "",

    /**
     * Total amount received by the address in USD.
     */
    val totalIncomingValueUsd: String? = "",

    /**
     * Total amount sent by the address in cryptocurrency.
     */
    val totalOutgoingValue: String? = "",

    /**
     * Total amount sent by the address in USD.
     */
    val totalOutgoingValueUsd: String? = "",

    /**
     * UTC Timestamp for when this resource was created by you.
     */
    val createdAt: String? = "",

    /**
     * UTC Timestamp for most recent lookup of this resource.
     */
    val updatedAt: String? = ""
)
