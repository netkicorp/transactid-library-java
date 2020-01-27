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
     * Public-key infrastructure (PKI) system being used to identify the merchant.
     * One of none / x509+sha256 / pgp+sha256 / ecdsa+sha256.
     */
    val pkiType: String? = "none",

    /**
     * PKI-system data that identifies the merchant and can be used to create a digital signature.
     */
    val pkiData: String? = null,

    /**
     * Bulk of the information that contains PaymentDetails message.
     */
    val paymentDetails: PaymentDetails,

    /**
     * Digital signature over a hash of the protocol buffer serialized variation of the PaymentRequest message.
     */
    val signature: String? = null
)
