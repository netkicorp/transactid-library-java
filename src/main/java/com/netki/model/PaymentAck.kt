package com.netki.model

/**
 * Representation of PaymentAck message.
 */
data class PaymentAck @JvmOverloads constructor(

    /**
     * Copy of the Payment message that triggered this PaymentACK.
     */
    val payment: Payment,

    /**
     * 	UTF-8 encoded note that should be displayed to the customer giving the status of the transaction.
     * 	(e.g. "Payment of 1 BTC for eleven tribbles accepted for processing.").
     */
    val memo: String? = null,

    /**
     * Metadata for the protocol message.
     */
    val protocolMessageMetadata: ProtocolMessageMetadata
) : ProtocolMessage
