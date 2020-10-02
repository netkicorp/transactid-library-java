package com.netki.model

/**
 * Metadata for the protocol message.
 */
data class ProtocolMessageMetadata(
    /**
     * Protocol version number.
     */
    val version: Long,

    /**
     * Message Protocol Status Code.
     */
    val statusCode: StatusCode,

    /**
     * Message Type of serialized_message.
     */
    val messageType: MessageType,

    /**
     * Human-readable Payment Protocol status message
     */
    val statusMessage: String,

    /**
     * Unique key to identify this entire exchange on the server. Default value SHOULD be SHA256(Serialized Initial InvoiceRequest + Current Epoch Time in Seconds as a String)
     */
    val identifier: String
)
