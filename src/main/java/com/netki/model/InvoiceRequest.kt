package com.netki.model

/**
 * Representation of InvoiceRequest message.
 */
data class InvoiceRequest(

    /**
     * Sender's SEC-encoded EC public key.
     */
    val senderPublicKey: String,

    /**
     * Integer-number-of-satoshis.
     */
    val amount: Long? = 0,

    /**
     * One of none / x509+sha256 / pgp+sha256 / ecdsa+sha256.
     */
    val pkiType: String? = "none",

    /**
     * Data of the pki depending on pkiType.
     */
    val pkiData: String? = null,

    /**
     * Human-readable description of invoice request for the receiver.
     */
    val memo: String? = null,

    /**
     * Secure (usually TLS-protected HTTP) location where an EncryptedProtocolMessage SHOULD be sent when ready.
     */
    val notificationUrl: String? = null,

    /**
     * PKI-dependent signature.
     */
    val signature: String? = null
)
