package com.netki.exceptions

/**
 * Represents an error related with the key management storing process.
 * For example if key management is not reachable.
 */
class KeyManagementStoreException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
