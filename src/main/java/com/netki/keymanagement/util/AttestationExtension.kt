package com.netki.keymanagement.util

import com.netki.model.Attestation

private const val PRINCIPAL_STRING = "CN=%s, C=%s, L=%s, O=%s, OU=%s, ST=%s"

fun Attestation.toPrincipal(data: String): String {

    return when (this) {
        Attestation.NATURAL_PERSON_FIRST_NAME -> String.format(
            PRINCIPAL_STRING,
            "naturalName.primaryIdentifier",
            "",
            "naturalPersonNameType=",
            "naturalFirst=",
            data,
            "ALIAS"
        )
        Attestation.NATURAL_PERSON_LAST_NAME -> String.format(
            PRINCIPAL_STRING,
            "naturalName.secondaryIdentifier",
            "",
            "naturalPersonNameType=",
            "naturalLast=",
            data,
            "LEGL"
        )
        Attestation.LEGAL_PERSON_NAME -> String.format(
            PRINCIPAL_STRING,
            "legalPersonName.primaryIdentifier",
            "Legal Entity",
            "legalPersonNameIdentifierType=",
            "legalPersonName=",
            data,
            "LEGL"
        )
        Attestation.BENEFICIARY_PERSON_FIRST_NAME -> String.format(
            PRINCIPAL_STRING,
            "beneficiaryName.primaryIdentifier",
            "",
            "naturalPersonNameType=",
            "beneficiaryFirst=",
            data,
            "ALIAS"
        )
        Attestation.BENEFICIARY_PERSON_LAST_NAME -> String.format(
            PRINCIPAL_STRING,
            "beneficiaryName.secondaryIdentifier",
            "",
            "naturalPersonNameType=",
            "beneficiaryLast=",
            data,
            "LEGL"
        )
        Attestation.BIRTH_DATE -> String.format(
            PRINCIPAL_STRING,
            "dateOfBirth",
            "",
            "dateInPast=",
            "birthdate=",
            data,
            "XX"
        )
        Attestation.BIRTH_PLACE -> String.format(
            PRINCIPAL_STRING,
            "placeOfBirth",
            "",
            "countryCode=",
            "country=",
            data,
            "XX"
        )
        Attestation.ADDRESS_1 -> String.format(
            PRINCIPAL_STRING,
            "geographicAddress",
            "",
            "addressTypeCode=",
            "address=",
            data,
            "HOME"
        )
        Attestation.ADDRESS_2 -> String.format(
            PRINCIPAL_STRING,
            "geographicAddress2",
            "",
            "addressTypeCode=",
            "address=",
            data,
            "GEOG"
        )
        Attestation.COUNTRY_OF_RESIDENCE -> String.format(
            PRINCIPAL_STRING,
            "countryOfResidence",
            "",
            "countryCode=",
            "country=",
            data,
            "XX"
        )
        Attestation.ISSUING_COUNTRY -> String.format(
            PRINCIPAL_STRING,
            "nationalIdentifier",
            "Miscellaneous",
            "nationalIdentifierType=",
            "nationalIdentifier=",
            data,
            "MISC"
        )
        Attestation.NATIONAL_IDENTIFIER_NUMBER -> String.format(
            PRINCIPAL_STRING,
            "nationalIdentifier.number",
            "",
            "number=",
            "docnumber=",
            data,
            ""
        )
        Attestation.NATIONAL_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            "nationalIdentifier.docType",
            "Drivers License",
            "nationalIdentifierType=",
            "doctype=",
            data,
            "DRLC"
        )
        Attestation.ACCOUNT_NUMBER -> String.format(
            PRINCIPAL_STRING,
            "accountNumber",
            "",
            "accountNumber=",
            "accountNumber=",
            data,
            ""
        )
        Attestation.CUSTOMER_IDENTIFICATION -> String.format(
            PRINCIPAL_STRING,
            "customerIdentification",
            "",
            "customerIdentification=",
            "customerIdentification=",
            data,
            "XX"
        )
        Attestation.REGISTRATION_AUTHORITY -> String.format(
            PRINCIPAL_STRING,
            "registrationAuthority",
            "",
            "registrationAuthority=",
            "registrationAuthority=",
            data,
            ""
        )
    }

}
