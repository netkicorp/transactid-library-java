package com.netki.exceptions

/**
 * Represents an error related with the key management fetching process.
 * For example if key management is not reachable.
 */
class KeyManagementFetchException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
