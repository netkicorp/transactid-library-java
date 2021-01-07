package com.netki.exceptions

/**
 * Represents an error when a certificate chain is not valid.
 */
class InvalidCertificateChainException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
