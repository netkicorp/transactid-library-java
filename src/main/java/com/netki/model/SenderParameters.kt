package com.netki.model

/**
 * Data of the sender of the message to be used to create a message.
 */
data class SenderParameters(
    /**
     * PkiData associated to the sender.
     */
    val pkiDataParameters: PkiDataParameters,

    /**
     * Parameters needed if you want to encrypt the protocol message.
     * If you add the parameters here, the encryption of the message will happen automatically.
     */
    val encryptionParameters: EncryptionParameters? = null
)
