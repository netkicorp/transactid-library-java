package com.netki.model

/**
 * Supported Pki types.
 */
enum class PkiType(val value: String) {

    /**
     * No Pki type defined, there won't be signature created.
     */
    NONE("none"),

    /**
     * Pki type X509, the signature will use SHA256 algorithm.
     */
    X509SHA256("x509+sha256")
}
