package com.netki.keymanagement.util

import com.netki.exceptions.CertificateProviderException
import com.netki.model.Attestation
import com.netki.model.IvmsConstraints
import com.netki.util.ErrorInformation.CERTIFICATE_INFORMATION_NOT_CORRECT_ERROR_PROVIDER

private const val DATA_LIMIT_LENGTH = 64
private const val PRINCIPAL_STRING = "CN=%s, C=%s, L=%s, O=%s, OU=%s, ST=%s"
internal fun Attestation.toPrincipal(data: String, ivmsConstraints: IvmsConstraints? = null): String {

    var data64Characters = ""
    var extraData = ""

    if (data.length > DATA_LIMIT_LENGTH) {
        data64Characters = data.substring(0, DATA_LIMIT_LENGTH)
        extraData = data.substring(DATA_LIMIT_LENGTH, data.length)
    } else {
        data64Characters = data
    }

    val ivmConstraintValue = ivmsConstraints ?: ""

    if (!this.validateConstraint(ivmsConstraints)) {
        throw CertificateProviderException(
            String.format(
                CERTIFICATE_INFORMATION_NOT_CORRECT_ERROR_PROVIDER,
                ivmConstraintValue,
                this
            )
        )
    }

    return when (this) {
        Attestation.LEGAL_PERSON_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "legalPersonNameType",
            "legalPersonName",
            "legalPerson.legalPersonName",
            ivmConstraintValue
        )
        Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "legalPersonNameType",
            "phoneticNameIdentifier",
            "legalPerson.phoneticNameIdentifier",
            ivmConstraintValue
        )
        Attestation.ADDRESS_DEPARTMENT -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "department",
            "address.department",
            ivmConstraintValue
        )
        Attestation.ADDRESS_SUB_DEPARTMENT -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "subDepartment",
            "address.subDepartment",
            ivmConstraintValue
        )
        Attestation.ADDRESS_STREET_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "streetName",
            "address.streetName",
            ivmConstraintValue
        )
        Attestation.ADDRESS_BUILDING_NUMBER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "buildingNumber",
            "address.buildingNumber",
            ivmConstraintValue
        )
        Attestation.ADDRESS_BUILDING_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "buildingName",
            "address.buildingName",
            ivmConstraintValue
        )
        Attestation.ADDRESS_FLOOR -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "floor",
            "address.floor",
            ivmConstraintValue
        )
        Attestation.ADDRESS_POSTBOX -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "postBox",
            "address.postBox",
            ivmConstraintValue
        )
        Attestation.ADDRESS_ROOM -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "room",
            "address.room",
            ivmConstraintValue
        )
        Attestation.ADDRESS_POSTCODE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "postcode",
            "address.postcode",
            ivmConstraintValue
        )
        Attestation.ADDRESS_TOWN_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "townName",
            "address.townName",
            ivmConstraintValue
        )
        Attestation.ADDRESS_TOWN_LOCATION_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "townLocationName",
            "address.townLocationName",
            ivmConstraintValue
        )
        Attestation.ADDRESS_DISTRICT_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "districtName",
            "address.districtName",
            ivmConstraintValue
        )
        Attestation.ADDRESS_COUNTRY_SUB_DIVISION -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "countrySubDivision",
            "address.countrySubDivision",
            ivmConstraintValue
        )
        Attestation.ADDRESS_ADDRESS_LINE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "addressLine",
            "address.addressLine",
            ivmConstraintValue
        )
        Attestation.ADDRESS_COUNTRY -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "country",
            "address.country",
            ivmConstraintValue
        )
        Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "naturalPersonNameType",
            "naturalPersonPrimaryIdentffier",
            "naturalName.primaryIdentifier",
            ivmConstraintValue
        )
        Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "naturalPersonNameType",
            "naturalPersonSecondaryIdentifier",
            "naturalName.secondaryIdentifier",
            ivmConstraintValue
        )
        Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "naturalPersonNameidentifierType",
            "phoneticNameIdentifier",
            "naturalPersonName.phoneticNameIdentifier",
            ivmConstraintValue
        )
        Attestation.DATE_OF_BIRTH -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "dateOfBirth",
            "dateOfBirth",
            "naturalPerson.dateOfBirth",
            ivmConstraintValue
        )
        Attestation.PLACE_OF_BIRTH -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "placeOfBirth",
            "placeOfBirth",
            "naturalPerson.placeOfBirth",
            ivmConstraintValue
        )
        Attestation.COUNTRY_OF_RESIDENCE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "countryCode",
            "country",
            "naturalPerson.countryOfResidence",
            ivmConstraintValue
        )
        Attestation.COUNTRY_OF_ISSUE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "nationalIdentifierType",
            "nationalIdentifier",
            "nationalIdentifier.countryOfIssue",
            ivmConstraintValue
        )
        Attestation.COUNTRY_OF_REGISTRATION -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "legalPersonNameType",
            "countryOfRegistration",
            "legalPersonName.countryOfRegistration",
            ivmConstraintValue
        )
        Attestation.NATIONAL_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "nationalIdentifierType",
            "nationalIdentifier",
            "nationalIdentification.nationalIdentifier",
            ivmConstraintValue
        )
        Attestation.CUSTOMER_IDENTIFICATION -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "customerIdentification",
            "customerIdentification",
            "legalPerson.customerIdentification",
            ivmConstraintValue
        )
        Attestation.REGISTRATION_AUTHORITY -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "registrationAuthority",
            "registrationAuthority",
            "nationalIdentification.registrationAuthorityName",
            ivmConstraintValue
        )
        Attestation.ACCOUNT_NUMBER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "accountNumber",
            "accountNumber",
            "beneficiary.accountNumber",
            ivmConstraintValue
        )
    }
}

internal fun Attestation.validateConstraint(ivmsConstraints: IvmsConstraints?) = when (this) {
    Attestation.LEGAL_PERSON_NAME -> ivmsConstraints == IvmsConstraints.LEGL ||
            ivmsConstraints == IvmsConstraints.SHRT ||
            ivmsConstraints == IvmsConstraints.TRAD
    Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER -> ivmsConstraints == IvmsConstraints.LEGL ||
            ivmsConstraints == IvmsConstraints.SHRT ||
            ivmsConstraints == IvmsConstraints.TRAD     
    Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER -> ivmsConstraints == IvmsConstraints.ALIA ||
            ivmsConstraints == IvmsConstraints.BIRT ||
            ivmsConstraints == IvmsConstraints.MAID ||
            ivmsConstraints == IvmsConstraints.LEGL ||
            ivmsConstraints == IvmsConstraints.MISC
    Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER -> ivmsConstraints == IvmsConstraints.ALIA ||
            ivmsConstraints == IvmsConstraints.BIRT ||
            ivmsConstraints == IvmsConstraints.MAID ||
            ivmsConstraints == IvmsConstraints.LEGL ||
            ivmsConstraints == IvmsConstraints.MISC
    Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER -> ivmsConstraints == IvmsConstraints.ALIA ||
            ivmsConstraints == IvmsConstraints.BIRT ||
            ivmsConstraints == IvmsConstraints.MAID ||
            ivmsConstraints == IvmsConstraints.LEGL ||
            ivmsConstraints == IvmsConstraints.MISC
    Attestation.ADDRESS_DEPARTMENT -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_SUB_DEPARTMENT -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_STREET_NAME -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_BUILDING_NUMBER -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_BUILDING_NAME -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_FLOOR -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_POSTBOX -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_ROOM -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_POSTCODE -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_TOWN_NAME -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_TOWN_LOCATION_NAME -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_DISTRICT_NAME -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_COUNTRY_SUB_DIVISION -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_ADDRESS_LINE -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.ADDRESS_COUNTRY -> ivmsConstraints == IvmsConstraints.GEOG ||
            ivmsConstraints == IvmsConstraints.BIZZ ||
            ivmsConstraints == IvmsConstraints.HOME
    Attestation.NATIONAL_IDENTIFIER -> ivmsConstraints == IvmsConstraints.ARNU ||
            ivmsConstraints == IvmsConstraints.CCPT ||
            ivmsConstraints == IvmsConstraints.RAID ||
            ivmsConstraints == IvmsConstraints.DRLC ||
            ivmsConstraints == IvmsConstraints.FIIN ||
            ivmsConstraints == IvmsConstraints.TXID ||
            ivmsConstraints == IvmsConstraints.SOCS ||
            ivmsConstraints == IvmsConstraints.IDCD ||
            ivmsConstraints == IvmsConstraints.LEIX ||
            ivmsConstraints == IvmsConstraints.MISC
    Attestation.REGISTRATION_AUTHORITY -> ivmsConstraints == IvmsConstraints.TEXT
    Attestation.ACCOUNT_NUMBER -> ivmsConstraints == IvmsConstraints.TEXT
    Attestation.CUSTOMER_IDENTIFICATION -> ivmsConstraints == IvmsConstraints.TEXT
    Attestation.DATE_OF_BIRTH -> ivmsConstraints == IvmsConstraints.DATE
    Attestation.PLACE_OF_BIRTH -> ivmsConstraints == IvmsConstraints.COUNTRYCODE
    Attestation.COUNTRY_OF_REGISTRATION -> ivmsConstraints == IvmsConstraints.COUNTRYCODE
    Attestation.COUNTRY_OF_ISSUE -> ivmsConstraints == IvmsConstraints.COUNTRYCODE
    Attestation.COUNTRY_OF_RESIDENCE -> ivmsConstraints == IvmsConstraints.COUNTRYCODE
}
