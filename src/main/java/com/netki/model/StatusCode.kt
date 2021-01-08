package com.netki.model

/**
 * Protocol Message Status Code.
 */
enum class StatusCode(val code: Long) {
    OK(1),
    CANCEL(2),
    GENERAL_UNKNOWN_ERROR(100),
    VERSION_TOO_HIGH(101),
    AUTHENTICATION_FAILED(102),
    ENCRYPTED_MESSAGE_REQUIRED(103),
    AMOUNT_TOO_HIGH(200),
    AMOUNT_TOO_LOW(201),
    AMOUNT_INVALID(202),
    PAYMENT_DOES_NOT_MEET_PAYMENT_REQUEST_REQUIREMENTS(203),
    CERTIFICATE_REQUIRED(300),
    CERTIFICATE_EXPIRED(301),
    CERTIFICATE_INVALID_FOR_TRANSACTION(302),
    CERTIFICATE_REVOKED(303),
    CERTIFICATE_NOT_WELL_ROOTED(304);

    companion object {
        private val values = values()
        fun getByCode(code: Long) = values.firstOrNull { it.code == code }
    }
}
