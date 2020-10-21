package com.netki.model

/**
 * Data of the recipient of the message to be used to create a message.
 */
data class RecipientParameters(
    /**
     * Recipient's vasp name.
     */
    val vaspName: String? = null,

    /**
     * Recipient's vasp name.
     */
    val chainAddress: String? = null,

    /**
     * Parameters needed if you want to encrypt the protocol message.
     * If you add the parameters here, the encryption of the message will happen automatically.
     */
    val encryptionParameters: EncryptionParameters? = null
)
