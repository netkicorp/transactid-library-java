package com.netki.model

/**
 * Parameters to encrypt message, this is needed if you want to create the EncryptedProtocolMessage.
 */
data class EncryptionParameters @JvmOverloads constructor(

    /**
     * SEC-encoded EC Private Key in PEM format.
     */
    val privateKeyPem: String? = "",

    /**
     * SEC-encoded EC Public Key in PEM format.
     */
    val publicKeyPem: String? = ""

)
