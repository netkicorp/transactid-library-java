package com.netki.exceptions

/**
 * Represents an error when trying to fetch an specific object.
 * For example if a certificate or key is not found by Id
 */
class ObjectNotFoundException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

