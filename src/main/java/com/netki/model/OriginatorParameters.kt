package com.netki.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class OriginatorParameters @JvmOverloads constructor(
    /**
     * True if this is the primary account owner for this transaction, there can be only one primary owner per transaction.
     */
    @JsonProperty("isPrimaryForTransaction")
    @SerializedName("isPrimaryForTransaction")
    override val isPrimaryForTransaction: Boolean = true,

    /**
     * All the PkiData associated to the originator.
     */
    override val pkiDataParametersSets: List<PkiDataParameters>
) : OwnerParameters()
