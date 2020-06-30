package com.netki.model

/**
 * Representation of PaymentRequest message.
 */
data class PaymentRequest(

    /**
     * Version of the protocol buffer object.
     */
    val paymentDetailsVersion: Int? = 1,

    /**
     * Bulk of the information that contains PaymentDetails message.
     */
    val paymentRequestParameters: PaymentRequestParameters,

    /**
     * Account owners.
     */
    val owners: List<Owner> = emptyList(),

    /**
     * List of attestations requested
     */
    val attestationsRequested: List<Attestation> = emptyList(),

    /**
     * Type of sender's pki data.
     */
    val senderPkiType: PkiType? = PkiType.NONE,

    /**
     * Sender's pki data, depends on senderPkiType.
     */
    val senderPkiData: String? = null,

    /**
     * Sender's Signature of the whole message.
     */
    val senderSignature: String? = null


)
