package com.netki.model

/**
 * Data of the sender of the message to be used to create a message.
 */
data class SenderParameters @JvmOverloads constructor(
    /**
     * PkiData associated to the sender.
     */
    val pkiDataParameters: PkiDataParameters? = null,

    /**
     * Parameters needed if you want to encrypt the protocol message.
     * If you add the parameters here, the encryption of the message will happen automatically.
     */
    val encryptionParameters: EncryptionParameters? = null,

    /**
     * EV Certificate in PEM format.
     */
    val evCertificatePem: String? = null
)
