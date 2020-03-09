package com.netki.model

/**
 * Pki data to be used to create a message.
 */
data class PkiDataParameters(

    /**
     * Type of certificate.
     */
    val attestation: String? = null,

    /**
     * PrivateKey in PEM format.
     */
    val privateKeyPem: String,

    /**
     * Certificate in PEM format associated with PrivateKey.
     */
    val certificatePem: String,

    /**
     * Type of the Pki data associated.
     */
    val type: PkiType = PkiType.NONE
)
