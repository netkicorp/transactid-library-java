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
    @Deprecated("Not used anymore")
    val alerts: List<Alert>? = emptyList(),

    /**
     * Total amount in cryptocurrency available with address.
     */
    val balance: Double? = 0.0,

    /**
     * A list of entities who were beneficiaries in a transaction.
     */
    val beneficiary: List<AddressTagInformation>? = null,

    /**
     * Case status for transaction.
     */
    val caseStatus: Int? = null,

    /**
     * Case status for transaction.
     */
    val caseStatusVerbose: String? = null,

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
     * A list of entities who were originators in a transaction.
     */
    val originator: List<AddressTagInformation>? = null,

    /**
     * An integer indicating if this address is Low Risk [1], Medium Risk [2] or High Risk [3] address or if no risks were detected [0], [-1] if could not fetch the risk level.
     */
    val riskLevel: Int? = -1,

    /**
     * Indicates if this address is Low Risk, Medium Risk , High Risk or if no risks were detected.
     */
    val riskLevelVerbose: String? = "",

    /**
     * Information about the owner and user.
     */
    val tags: AddressTags? = null,

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
