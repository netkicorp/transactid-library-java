package com.netki.model

/**
 * Type of owners for accounts.
 */
enum class OwnerType(val description: String) {
    BENEFICIARY("beneficiary"),
    ORIGINATOR("originator")
}
