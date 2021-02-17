package com.netki.model

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Data to create InvoiceRequest message.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
data class InvoiceRequestParameters @JvmOverloads constructor(

    /**
     * List of originators for this transaction.
     */
    val originatorParameters: List<OriginatorParameters>,

    /**
     * The sender of the protocol message.
     */
    val senderParameters: SenderParameters,

    /**
     * List of attestations requested for the transaction.
     */
    val attestationsRequested: List<Attestation>,

    /**
     * Integer-number-of-satoshis.
     */
    val amount: Long? = null,

    /**
     * Human-readable description of invoice request for the receiver.
     */
    val memo: String? = "",

    /**
     * Where the payment comes from.
     */
    val originatorsAddresses: List<Output> = emptyList(),

    /**
     * List of beneficiaries for this transaction.
     */
    val beneficiaryParameters: List<BeneficiaryParameters>? = emptyList(),

    /**
     * Information of the recipient of the message.
     */
    val recipientParameters: RecipientParameters? = RecipientParameters(),

    /**
     * Secure (usually TLS-protected HTTP) location where an EncryptedProtocolMessage SHOULD be sent when ready.
     */
    val notificationUrl: String? = "",

    /**
     * Status and information of the protocol message status, by default "OK".
     */
    val messageInformation: MessageInformation = MessageInformation()
) : ProtocolMessageParameters
