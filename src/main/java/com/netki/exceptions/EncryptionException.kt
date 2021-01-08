package com.netki.exceptions

/**
 * Represents an error related with the encryption of the protocol message.
 * For example if there is missing data to encrypt or the encryption is not valid.
 */
class EncryptionException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
