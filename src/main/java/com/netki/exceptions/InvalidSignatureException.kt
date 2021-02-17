package com.netki.exceptions

/**
 * Represents an error related with the signature.
 * For example if is a malformed or not valid.
 */
class InvalidSignatureException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
