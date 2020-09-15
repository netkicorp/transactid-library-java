package com.netki.model

/**
 * Data of the recipient of the message to be used to create a message.
 */
data class RecipientParameters(
    /**
     * Recipient's vasp name
     */
    val vaspName: String,

    /**
     * Recipient's vasp name
     */
    val chainAddress: String
)
