package com.netki.model

/**
 * Pki data.
 */
data class KeyPairPem(

    /**
     * PrivateKey in PEM format.
     */
    val privateKeyPem: String,

    /**
     * Certificate in PEM format associated with PrivateKey.
     */
    val certificatePem: String,

    /**
     * Pki type
     */
    val type: PkiType = PkiType.NONE
)

/**
 * Supported Pki types
 */
enum class PkiType(val value: String) {

    /**
     * No Pki type defined, there won't be signature created
     */
    NONE("none"),

    /**
     * Pki type X509, the signature will use SHA256 algorithm
     */
    X509SHA256("x509+sha256")
}
