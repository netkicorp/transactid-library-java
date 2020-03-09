package com.netki.model

/**
 * Data of the sender of the message to be used to create a message.
 */
data class SenderParameters(
    /**
     * PkiData associated to the sender
     */
    val pkiDataParameters: PkiDataParameters
)
