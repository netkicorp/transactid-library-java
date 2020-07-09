package com.netki.model

data class AddressInformation(

    /**
     * Address.
     */
    val identifier: String,

    /**
     * Describes all alerts fired for this address.
     */
    val alerts: List<Alert>,

    /**
     * Total amount in cryptocurrency available with address.
     */
    val balance: Double,

    /**
     * The currency code for the blockchain this address was searched on.
     */
    val currency: Int,

    /**
     * The currency name for the blockchain this address was searched on.
     */
    val currency_verbose: String,

    /**
     * Date on which address has made its first transaction.
     */
    val earliest_transaction_time: String,

    /**
     * Date on which address has made its last transaction.
     */
    val latest_transaction_time: String,

    /**
     * An integer indicating if this address is Low Risk [1], Medium Risk [2] or High Risk [3] address or if no risks were detected [0].
     */
    val risk_level: Int,

    /**
     * Indicates if this address is Low Risk, Medium Risk , High Risk or if no risks were detected.
     */
    val risk_level_verbose: String,

    /**
     * Total amount received by the address in cryptocurrency.
     */
    val total_incoming_value: String,

    /**
     * 	Total amount received by the address in USD.
     */
    val total_incoming_value_usd: String,

    /**
     * Total amount sent by the address in cryptocurrency.
     */
    val total_outgoing_value: String,

    /**
     * Total amount sent by the address in USD.
     */
    val total_outgoing_value_usd: String,

    /**
     * UTC Timestamp for when this resource was created by you.
     */
    val created_at: String,

    /**
     * UTC Timestamp for most recent lookup of this resource.
     */
    val updated_at: String
)
