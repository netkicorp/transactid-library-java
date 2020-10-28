package com.netki.model

/**
 * Data of the owner of the account.
 */
abstract class Owner {
    /**
     * True if this is the primary account owner for this transaction, there can be only one primary owner per transaction.
     */
    abstract val isPrimaryForTransaction: Boolean

    /**
     * All the PkiData associated to the Owner.
     */
    abstract val pkiDataSet: List<PkiData>
}
