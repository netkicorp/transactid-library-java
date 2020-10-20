package com.netki.model

/**
 * Data to create InvoiceRequest message.
 */
data class InvoiceRequestParameters(

    /**
     * Integer-number-of-satoshis.
     */
    val amount: Long,

    /**
     * Human-readable description of invoice request for the receiver.
     */
    val memo: String,

    /**
     * Secure (usually TLS-protected HTTP) location where an EncryptedProtocolMessage SHOULD be sent when ready.
     */
    val notificationUrl: String? = "",

    /**
     * One or more outputs where Bitcoins are to be sent.
     */
    val outputs: List<Output> = emptyList()
)
