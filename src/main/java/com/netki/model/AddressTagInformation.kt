package com.netki.model

data class AddressTagInformation(

    /**
     * Entity type.
     */
    val tagNameVerbose: String? = null,

    /**
     * Entity subtype.
     */
    val tagSubtypeVerbose: String? = null,

    /**
     * Entity name.
     */
    val tagTypeVerbose: String? = null,

    /**
     * Value sent by entity in this transaction.
     */
    val totalValueUsd: String? = null
)
