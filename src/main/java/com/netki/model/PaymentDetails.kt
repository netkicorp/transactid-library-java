package com.netki.model

import java.sql.Timestamp

/**
 * Representation of PaymentDetails message.
 */
data class PaymentDetails(

    /**
     * Either "main" for payments on the production Bitcoin network, or "test" for payments on test network.
     */
    val network: String? = "main",

    /**
     * One or more outputs where Bitcoins are to be sent.
     */
    val outputs: List<Output> = emptyList(),

    /**
     * Unix timestamp (seconds since 1-Jan-1970 UTC) when the PaymentRequest was created.
     */
    val time: Timestamp,

    /**
     * Unix timestamp (UTC) after which the PaymentRequest should be considered invalid.
     */
    val expires: Timestamp? = null,

    /**
     * 	UTF-8 encoded, plain-text (no formatting) note that should be displayed to the customer,
     * 	explaining what this PaymentRequest is for.
     */
    val memo: String? = null,

    /**
     * Secure (usually https) location where a Payment message (see below) may be sent to obtain a PaymentACK.
     */
    val paymentUrl: String? = null,

    /**
     * Arbitrary data that may be used by the merchant to identify the PaymentRequest.
     */
    val merchantData: String? = null
)
