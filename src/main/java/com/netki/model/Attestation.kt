package com.netki.model

import com.google.gson.annotations.SerializedName

/**
 * List of types of attestations supported.
 */
enum class Attestation(id: Int) {
    @SerializedName("address1")
    ADDRESS_1(0),
    @SerializedName("address2")
    ADDRESS_2(1),
    @SerializedName("beneficiaryPersonFirstName")
    BENEFICIARY_PERSON_FIRST_NAME(2),
    @SerializedName("beneficiaryPersonLastName")
    BENEFICIARY_PERSON_LAST_NAME(3),
    @SerializedName("birthDate")
    BIRTH_DATE(4),
    @SerializedName("birthPlace")
    BIRTH_PLACE(5),
    @SerializedName("countryOfResidence")
    COUNTRY_OF_RESIDENCE(6),
    @SerializedName("customerIdentification")
    CUSTOMER_IDENTIFICATION(7),
    @SerializedName("issuingCountry")
    ISSUING_COUNTRY(8),
    @SerializedName("legalPersonName")
    LEGAL_PERSON_NAME(9),
    @SerializedName("nationalIdentifier")
    NATIONAL_IDENTIFIER(10),
    @SerializedName("nationalIdentifierNumber")
    NATIONAL_IDENTIFIER_NUMBER(11),
    @SerializedName("naturalPersonFirstName")
    NATURAL_PERSON_FIRST_NAME(12),
    @SerializedName("naturalPersonLastName")
    NATURAL_PERSON_LAST_NAME(13),
    @SerializedName("accountNumber")
    ACCOUNT_NUMBER(14),
    @SerializedName("registrationAuthority")
    REGISTRATION_AUTHORITY(15)
}
