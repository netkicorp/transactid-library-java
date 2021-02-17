package com.netki.exceptions

/**
 * Represents an error related with key validation.
 * For example if the key is not a well formed key.
 */
class InvalidPrivateKeyException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
