package com.netki.exceptions

/**
 * Represents an error when a sygna transfer id is not valid.
 */
class InvalidSygnaTransferIDException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
