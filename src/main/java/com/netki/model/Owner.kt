package com.netki.model

/**
 * Data of the owner of the account.
 */
data class Owner(
    /**
     * True if this is the primary account owner for this transaction, there can be only one primary owner per transaction.
     */
    val isPrimaryForTransaction: Boolean = true,

    /**
     * All the PkiData associated to the Owner.
     */
    val pkiDataSet: List<PkiData>
)
