package com.netki.model

/**
 *  Status code and Status message that is used for error communication
 *  such that the protocol does not rely on transport-layer error handling.
 */
data class MessageInformation @JvmOverloads constructor(
    /**
     * Protocol Message Status Code.
     */
    val statusCode: StatusCode = StatusCode.OK,

    /**
     * Human-readable Payment Protocol status message.
     */
    val statusMessage: String = "",

    /**
     * Set to true if you want to encrypt message.
     */
    val encryptMessage: Boolean = false
)
