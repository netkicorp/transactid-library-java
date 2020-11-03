package com.netki.model

/**
 * Pki data in a message.
 */
data class PkiData @JvmOverloads constructor(

    /**
     * Type of certificate.
     */
    val attestation: Attestation? = null,

    /**
     * Certificate in PEM format associated with PrivateKey.
     */
    val certificatePem: String,

    /**
     * Pki type.
     */
    val type: PkiType = PkiType.NONE,

    /**
     * Signature created with this attestation.
     */
    val signature: String? = ""
)
