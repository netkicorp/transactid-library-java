package com.netki.util

object ErrorInformation {

    const val OWNERS_VALIDATION_NO_PRIMARY_OWNER = "There should be one primary %s"
    const val OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS = "There can be only one primary %s"
    const val OWNERS_VALIDATION_EMPTY_ERROR = "There should be at least one %s for this message"
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
    const val PARSE_BINARY_MESSAGE_INVALID_INPUT = "Invalid object for message, error: %s"
    const val KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION = "This is not a valid x509 certificate, error: %s"
    const val KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION = "This is not a valid private key, error: %s"
    const val KEY_MANAGEMENT_ERROR_STORING_CERTIFICATE = "There was an error storing the certificate, error: %s"
    const val KEY_MANAGEMENT_ERROR_STORING_PRIVATE_KEY = "There was an error storing the private key, error: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE = "There was an error fetching the certificate, error: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY = "There was an error fetching the private key, error: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE_NOT_FOUND = "Certificate not found for id: %s"
    const val KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY_NOT_FOUND = "Private key not found for id: %s"
    const val ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER = "Provider internal error for address: %s, with error: %s"
    const val ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER =
        "Provider authorization error for address: %s. Make sure to initialize the library with the correct AuthorizationKey."
    const val CERTIFICATE_INFORMATION_INTERNAL_ERROR_PROVIDER = "Provider internal error for: %s, with error: %s"
    const val CERTIFICATE_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER =
        "Provider authorization error. Make sure to initialize the library with the correct AuthorizationKey."
    const val CERTIFICATE_INFORMATION_NOT_CORRECT_ERROR_PROVIDER =
        "The IvmConstraint: %s, for the attestation: %s, is not valid."
    const val CERTIFICATE_INFORMATION_STRING_NOT_CORRECT_ERROR_PROVIDER =
        "The data: %s, for the attestation: %s, is not valid, the valid characters for the data are a-zA-Z0-9_. -"
    const val ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR =
        "To encrypt the message you need to have the recipient's public key in your RecipientParameters.EncryptionParameters object."
    const val ENCRYPTION_MISSING_SENDER_KEYS_ERROR =
        "To encrypt the message you need to have the sender's public/private keys in your SenderParameters.EncryptionParameters object."
    const val ENCRYPTION_INCORRECT_KEY_FORMAT_ERROR =
        "To encrypt the message you need to have a private key with ECDSA algorithm."
    const val ENCRYPTION_INVALID_ERROR = "Unable to decrypt the message with the given keys, error: %s"
    const val DECRYPTION_MISSING_RECIPIENT_KEYS_ERROR =
        "To decrypt the message you need to have the recipient's private key in your RecipientParameters.EncryptionParameters object."
}
