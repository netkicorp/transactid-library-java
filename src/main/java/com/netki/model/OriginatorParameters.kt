package com.netki.model

data class OriginatorParameters(
    /**
     * True if this is the primary account owner for this transaction, there can be only one primary owner per transaction.
     */
    override val isPrimaryForTransaction: Boolean = true,

    /**
     * All the PkiData associated to the originator.
     */
    override val pkiDataParametersSets: List<PkiDataParameters>
) : OwnerParameters()
