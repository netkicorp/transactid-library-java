package com.netki.exceptions

/**
 * Represents an error related with the authorization to connect to the provider.
 */
class CertificateProviderUnauthorizedException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
