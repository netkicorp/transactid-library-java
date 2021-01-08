package com.netki.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Timestamp

/**
 * Representation of PaymentDetails message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaymentRequestParameters @JvmOverloads constructor(

    /**
     * Either "main" for payments on the production Bitcoin network, or "test" for payments on test network.
     */
    val network: String? = "main",

    /**
     * Where payment should be sent.
     */
    val beneficiariesAddresses: List<Output> = emptyList(),

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
    val merchantData: String? = null,

    /**
     * List of beneficiaries for this transaction.
     */
    val beneficiaryParameters: List<BeneficiaryParameters>,

    /**
     * The sender of the protocol message.
     */
    val senderParameters: SenderParameters,

    /**
     * List of attestations requested for the transaction.
     */
    val attestationsRequested: List<Attestation>,

    /**
     * Information of the recipient of the message.
     */
    val recipientParameters: RecipientParameters? = null,

    /**
     * Status and information of the protocol message status, by default "OK".
     */
    val messageInformation: MessageInformation = MessageInformation(),

    /**
     * Version of the PaymentDetails message.
     */
    val paymentParametersVersion: Int = 1
) : ProtocolMessageParameters
