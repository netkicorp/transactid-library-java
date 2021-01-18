package com.netki.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

/**
 * List of types of attestations supported.
 */
enum class Attestation(id: Int) {

    @JsonProperty("legalPersonName")
    @SerializedName("legalPersonName")
    LEGAL_PERSON_NAME(0),

    @JsonProperty("legalPersonPhoneticNameIdentifier")
    @SerializedName("legalPersonPhoneticNameIdentifier")
    LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER(1),

    @JsonProperty("addressDepartment")
    @SerializedName("addressDepartment")
    ADDRESS_DEPARTMENT(2),

    @JsonProperty("addressSubDepartment")
    @SerializedName("addressSubDepartment")
    ADDRESS_SUB_DEPARTMENT(3),

    @JsonProperty("addressStreetName")
    @SerializedName("addressStreetName")
    ADDRESS_STREET_NAME(4),

    @JsonProperty("addressBuildingNumber")
    @SerializedName("addressBuildingNumber")
    ADDRESS_BUILDING_NUMBER(5),

    @JsonProperty("addressBuildingName")
    @SerializedName("addressBuildingName")
    ADDRESS_BUILDING_NAME(6),

    @JsonProperty("addressFloor")
    @SerializedName("addressFloor")
    ADDRESS_FLOOR(7),

    @JsonProperty("addressPostbox")
    @SerializedName("addressPostbox")
    ADDRESS_POSTBOX(8),

    @JsonProperty("addressRoom")
    @SerializedName("addressRoom")
    ADDRESS_ROOM(9),

    @JsonProperty("addressPostcode")
    @SerializedName("addressPostcode")
    ADDRESS_POSTCODE(10),

    @JsonProperty("addressTownName")
    @SerializedName("addressTownName")
    ADDRESS_TOWN_NAME(11),

    @JsonProperty("addressTownLocationName")
    @SerializedName("addressTownLocationName")
    ADDRESS_TOWN_LOCATION_NAME(12),

    @JsonProperty("addressDistrictName")
    @SerializedName("addressDistrictName")
    ADDRESS_DISTRICT_NAME(13),

    @JsonProperty("addressCountrySubDivision")
    @SerializedName("addressCountrySubDivision")
    ADDRESS_COUNTRY_SUB_DIVISION(14),

    @JsonProperty("addressAddressLine")
    @SerializedName("addressAddressLine")
    ADDRESS_ADDRESS_LINE(15),

    @JsonProperty("addressCountry")
    @SerializedName("addressCountry")
    ADDRESS_COUNTRY(16),

    @JsonProperty("naturalPersonPrimaryIdentifier")
    @SerializedName("naturalPersonPrimaryIdentifier")
    NATURAL_PERSON_PRIMARY_IDENTIFIER(17),

    @JsonProperty("naturalPersonSecondaryIdentifier")
    @SerializedName("naturalPersonSecondaryIdentifier")
    NATURAL_PERSON_SECONDARY_IDENTIFIER(18),

    @JsonProperty("naturalPersonPhoneticNameIdentifier")
    @SerializedName("naturalPersonPhoneticNameIdentifier")
    NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER(19),

    @JsonProperty("dateOfBirth")
    @SerializedName("dateOfBirth")
    DATE_OF_BIRTH(20),

    @JsonProperty("placeOfBirth")
    @SerializedName("placeOfBirth")
    PLACE_OF_BIRTH(21),

    @JsonProperty("countryOfResidence")
    @SerializedName("countryOfResidence")
    COUNTRY_OF_RESIDENCE(22),

    @JsonProperty("countryOfIssue")
    @SerializedName("countryOfIssue")
    COUNTRY_OF_ISSUE(23),

    @JsonProperty("countryOfRegistration")
    @SerializedName("countryOfRegistration")
    COUNTRY_OF_REGISTRATION(24),

    @JsonProperty("nationalIdentifier")
    @SerializedName("nationalIdentifier")
    NATIONAL_IDENTIFIER(25),

    @JsonProperty("accountNumber")
    @SerializedName("accountNumber")
    ACCOUNT_NUMBER(26),

    @JsonProperty("customerIdentification")
    @SerializedName("customerIdentification")
    CUSTOMER_IDENTIFICATION(27),

    @JsonProperty("registrationAuthority")
    @SerializedName("registrationAuthority")
    REGISTRATION_AUTHORITY(28),
}

/**
 * List of IVMS Constraints.
 */
enum class IvmsConstraint {
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
    LEIX,
    SHRT,
    TRAD,
    TEXT,
    DATE,
    COUNTRYCODE
}
