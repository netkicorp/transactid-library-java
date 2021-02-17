package com.netki.exceptions

/**
 * Represents an error related with fetching the information of an address.
 * For example if the provider is not available.
 */
class AddressProviderErrorException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
