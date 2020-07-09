package com.netki.address.info.repo.data

data class MerkleWalletAlert(
    val merkleWalletContext: String,
    val created_at: String,
    val risk_level: Int,
    val risk_level_verbose: String,
    val merkleWalletRisk_types: List<MerkleWalletRiskType>,
    val rule_name: String
)
