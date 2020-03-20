package com.netki.util

object ErrorInformation {

    const val OWNERS_VALIDATION_NO_PRIMARY_OWNER = "There should be one primary owner"
    const val OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS = "There can be only one primary owner"
    const val SIGNATURE_VALIDATION_INVALID_SENDER_SIGNATURE = "Sender signature is not valid"
    const val SIGNATURE_VALIDATION_INVALID_OWNER_SIGNATURE = "Owner signature is not valid for attestation %s"
    const val CERTIFICATE_VALIDATION_INVALID_SENDER_CERTIFICATE_CA = "Sender certificate does not belong to any trusted CA"
    const val CERTIFICATE_VALIDATION_INVALID_OWNER_CERTIFICATE_CA = "Owner certificate for attestation %s does not belong to any trusted CA"
    const val CERTIFICATE_VALIDATION_CERTIFICATE_CHAINS_NOT_FOUND = "There is not certificate chains available (.cer files), make sure to put them on resources/certificates"
    const val CERTIFICATE_VALIDATION_SELF_SIGNED_ERROR = "%s certificate: %s is self signed"
    const val CERTIFICATE_VALIDATION_NOT_SELF_SIGNED_ERROR = "%s certificate: %s is not self signed"
    const val PARSE_BINARY_MESSAGE_INVALID_INPUT = "Invalid object for: %s, error: %s"
}
