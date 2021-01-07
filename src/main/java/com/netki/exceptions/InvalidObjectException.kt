package com.netki.exceptions

/**
 * Represents an error related with parsing the object.
 * For example if the object has missing mandatory data or is malformed.
 */
class InvalidObjectException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
