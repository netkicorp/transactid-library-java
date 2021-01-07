package com.netki.exceptions

/**
 * Represents an error related with creating the certificate.
 * For example if the repo is not available.
 */
class CertificateProviderException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
