package com.netki.exceptions

/**
 * Represents an error when a sygna owners is not valid.
 */
class InvalidSygnaOwnerException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
