package com.netki.model

/**
 * Data of the owner of the account to be used to create a message.
 */
data class OwnerParameters(
    /**
     * True if this is the primary account owner for this transaction, there can be only one primary owner per transaction.
     */
    val isPrimaryForTransaction: Boolean = true,

    /**
     * All the PkiData associated to the Owner.
     */
    val pkiDataParametersSets: List<PkiDataParameters>

)
