package com.netki.exceptions

/**
 * Represents an error related with certificate validation.
 * For example if the client certificate is self signed.
 */
class InvalidCertificateException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
