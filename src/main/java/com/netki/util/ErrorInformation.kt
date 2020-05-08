package com.netki.util

object ErrorInformation {

    const val OWNERS_VALIDATION_NO_PRIMARY_OWNER = "There should be one primary owner"
    const val OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS = "There can be only one primary owner"
    const val SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE = "Sender signature is not valid"
    const val SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE = "Owner signature is not valid for attestation %s"
    const val CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA =
        "Sender certificate does not belong to any trusted CA"
    const val CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA =
        "Owner certificate for attestation %s does not belong to any trusted CA"
    const val CERTIFICATE_VALIDATION_CERTIFICATE_CHAINS_NOT_FOUND =
        "There is not certificate chains available (.cer files), make sure to put them on resources/certificates"
    const val CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR = "Certificate: %s, is not a valid %s certificate"
    const val CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND = "No client certificate found in the certificates"
    const val PARSE_BINARY_MESSAGE_INVALID_INPUT = "Invalid object for: %s, error: %s"
    const val KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION = "This is not a valid x509 certificate, error: %s"
    const val KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION = "This is not a valid private key, error: %s"
    const val KEY_MANAGEMENT_ERROR_STORING_CERTIFICATE = "There was an error storing the certificate, error: %s"
    const val KEY_MANAGEMENT_ERROR_STORING_PRIVATE_KEY = "There was an error storing the private key, error: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE = "There was an error fetching the certificate, error: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY = "There was an error fetching the private key, error: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE_NOT_FOUND = "Certificate not found for id: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY_NOT_FOUND = "Private key not found for id: %s"
}
