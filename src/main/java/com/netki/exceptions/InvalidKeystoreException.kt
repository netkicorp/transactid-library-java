package com.netki.exceptions

/**
 * Represents an error when related with the Keystore containing the Certificate chain.
 * For example if the keystore is missing or it contains more than one root certificate.
 */
class InvalidKeystoreException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
