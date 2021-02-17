package com.netki.exceptions

/**
 * Represents an error when a list of owners is not valid.
 */
class InvalidOwnersException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
