package com.netki.model

/**
 * Metadata for the protocol message.
 */
data class ProtocolMessageMetadata @JvmOverloads constructor(
    /**
     * Protocol version number.
     */
    val version: Long,

    /**
     * Protocol Message Status Code.
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
    val identifier: String,

    /**
     * True if the message is encrypted, false otherwise.
     */
    val encrypted: Boolean = false,

    /**
     * AES-256-GCM Encrypted (as defined in BIP75) Payment Protocol Message.
     */
    val encryptedMessage: String? = null,

    /**
     * Recipient's SEC-encoded EC Public Key.
     */
    val recipientPublicKeyPem: String? = null,

    /**
     * Sender's SEC-encoded EC Public Key.
     */
    val senderPublicKeyPem: String? = null,

    /**
     * 	Microseconds since epoch.
     */
    val nonce: Long? = null,

    /**
     * 	DER-encoded Signature over the full EncryptedProtocolMessage with EC Key Belonging to Sender / Recipient, respectively.
     */
    val signature: String? = null
)
