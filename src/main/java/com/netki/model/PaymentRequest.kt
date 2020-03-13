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
    val paymentParameters: PaymentParameters,

    /**
     * Account owners.
     */
    val owners: List<Owner> = emptyList(),

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
