package com.netki.address.info.repo.data

data class MerkleWalletDetail(
    val merkleWalletAlerts: List<MerkleWalletAlert>,
    val balance: Double,
    val case_status: Any,
    val case_status_verbose: Any,
    val created_at: String,
    val currency: Int,
    val currency_verbose: String,
    val earliest_transaction_time: String,
    val identifier: String,
    val latest_transaction_time: String,
    val risk_level: Int,
    val risk_level_verbose: String,
    val total_incoming_value: String,
    val total_incoming_value_usd: String,
    val total_outgoing_value: String,
    val total_outgoing_value_usd: String,
    val updated_at: String
)
