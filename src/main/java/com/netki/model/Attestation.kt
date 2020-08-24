package com.netki.model

import com.google.gson.annotations.SerializedName

/**
 * List of types of attestations supported.
 */
enum class Attestation(id: Int) {
    @SerializedName("legalPersonPrimaryIdentifier")
    LEGAL_PERSON_PRIMARY_NAME(0),
    @SerializedName("legalPersonSecondaryIdentifier")
    LEGAL_PERSON_SECONDARY_NAME(1),
    @SerializedName("addressDepartment")
    ADDRESS_DEPARTMENT(2),
    @SerializedName("addressSubDepartment")
    ADDRESS_SUB_DEPARTMENT(3),
    @SerializedName("addressStreetName")
    ADDRESS_STREET_NAME(4),
    @SerializedName("addressBuildingNumber")
    ADDRESS_BUILDING_NUMBER(5),
    @SerializedName("addressBuildingName")
    ADDRESS_BUILDING_NAME(6),
    @SerializedName("addressFloor")
    ADDRESS_FLOOR(7),
    @SerializedName("addressPostbox")
    ADDRESS_POSTBOX(8),
    @SerializedName("addressRoom")
    ADDRESS_ROOM(9),
    @SerializedName("addressPostcode")
    ADDRESS_POSTCODE(10),
    @SerializedName("addressTownName")
    ADDRESS_TOWN_NAME(11),
    @SerializedName("addressTownLocationName")
    ADDRESS_TOWN_LOCATION_NAME(12),
    @SerializedName("addressDistrictName")
    ADDRESS_DISTRICT_NAME(13),
    @SerializedName("addressCountrySubDivision")
    ADDRESS_COUNTRY_SUB_DIVISION(14),
    @SerializedName("addressAddressLine")
    ADDRESS_ADDRESS_LINE(15),
    @SerializedName("addressCountry")
    ADDRESS_COUNTRY(16),
    @SerializedName("naturalPersonFirstName")
    NATURAL_PERSON_FIRST_NAME(17),
    @SerializedName("naturalPersonLastName")
    NATURAL_PERSON_LAST_NAME(18),
    @SerializedName("beneficiaryPersonFirstName")
    BENEFICIARY_PERSON_FIRST_NAME(19),
    @SerializedName("beneficiaryPersonLastName")
    BENEFICIARY_PERSON_LAST_NAME(20),
    @SerializedName("birthDate")
    BIRTH_DATE(21),
    @SerializedName("birthPlace")
    BIRTH_PLACE(22),
    @SerializedName("countryOfResidence")
    COUNTRY_OF_RESIDENCE(23),
    @SerializedName("issuingCountry")
    ISSUING_COUNTRY(24),
    @SerializedName("nationalIdentifierNumber")
    NATIONAL_IDENTIFIER_NUMBER(25),
    @SerializedName("nationalIdentifier")
    NATIONAL_IDENTIFIER(26),
    @SerializedName("accountNumber")
    ACCOUNT_NUMBER(27),
    @SerializedName("customerIdentification")
    CUSTOMER_IDENTIFICATION(28),
    @SerializedName("registrationAuthority")
    REGISTRATION_AUTHORITY(29)
}

/**
 * List of IVMS Constraints.
 */
enum class IvmsConstraints() {
    GEOG,
    BIZZ,
    HOME,
    ALIA,
    BIRT,
    MAID,
    LEGL,
    MISC,
    ARNU,
    CCPT,
    RAID,
    DRLC,
    FIIN,
    TXID,
    SOCS,
    IDCD,
    LEIX
}
