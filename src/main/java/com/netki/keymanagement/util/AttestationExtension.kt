package com.netki.keymanagement.util

import com.netki.exceptions.CertificateProviderException
import com.netki.model.Attestation
import com.netki.model.IvmsConstraint
import com.netki.util.ErrorInformation.CERTIFICATE_INFORMATION_NOT_CORRECT_ERROR_PROVIDER

private const val DATA_LIMIT_LENGTH = 64
private const val PRINCIPAL_STRING = "CN=%s, C=%s, L=%s, O=%s, OU=%s, ST=%s"
internal fun Attestation.toPrincipal(data: String, ivmsConstraint: IvmsConstraint): String {

    var data64Characters = ""
    var extraData = ""

    if (data.length > DATA_LIMIT_LENGTH) {
        data64Characters = data.substring(0, DATA_LIMIT_LENGTH)
        extraData = data.substring(DATA_LIMIT_LENGTH, data.length)
    } else {
        data64Characters = data
    }

    if (!this.validateConstraint(ivmsConstraint)) {
        throw CertificateProviderException(
            String.format(
                CERTIFICATE_INFORMATION_NOT_CORRECT_ERROR_PROVIDER,
                ivmsConstraint,
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
            ivmsConstraint
        )
        Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "legalPersonNameType",
            "legalPersonPhoneticNameIdentifier",
            "legalPerson.phoneticNameIdentifier",
            ivmsConstraint
        )
        Attestation.ADDRESS_DEPARTMENT -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "department",
            "address.department",
            ivmsConstraint
        )
        Attestation.ADDRESS_SUB_DEPARTMENT -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "subDepartment",
            "address.subDepartment",
            ivmsConstraint
        )
        Attestation.ADDRESS_STREET_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "streetName",
            "address.streetName",
            ivmsConstraint
        )
        Attestation.ADDRESS_BUILDING_NUMBER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "buildingNumber",
            "address.buildingNumber",
            ivmsConstraint
        )
        Attestation.ADDRESS_BUILDING_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "buildingName",
            "address.buildingName",
            ivmsConstraint
        )
        Attestation.ADDRESS_FLOOR -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "floor",
            "address.floor",
            ivmsConstraint
        )
        Attestation.ADDRESS_POSTBOX -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "postBox",
            "address.postBox",
            ivmsConstraint
        )
        Attestation.ADDRESS_ROOM -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "room",
            "address.room",
            ivmsConstraint
        )
        Attestation.ADDRESS_POSTCODE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "postcode",
            "address.postcode",
            ivmsConstraint
        )
        Attestation.ADDRESS_TOWN_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "townName",
            "address.townName",
            ivmsConstraint
        )
        Attestation.ADDRESS_TOWN_LOCATION_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "townLocationName",
            "address.townLocationName",
            ivmsConstraint
        )
        Attestation.ADDRESS_DISTRICT_NAME -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "districtName",
            "address.districtName",
            ivmsConstraint
        )
        Attestation.ADDRESS_COUNTRY_SUB_DIVISION -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "countrySubDivision",
            "address.countrySubDivision",
            ivmsConstraint
        )
        Attestation.ADDRESS_ADDRESS_LINE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "addressLine",
            "address.addressLine",
            ivmsConstraint
        )
        Attestation.ADDRESS_COUNTRY -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "addressTypeCode",
            "country",
            "address.country",
            ivmsConstraint
        )
        Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "naturalPersonNameType",
            "naturalPersonPrimaryIdentfier",
            "naturalName.primaryIdentifier",
            ivmsConstraint
        )
        Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "naturalPersonNameType",
            "naturalPersonSecondaryIdentifier",
            "naturalName.secondaryIdentifier",
            ivmsConstraint
        )
        Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "naturalPersonNameidentifierType",
            "naturalPersonPhoneticNameIdentifier",
            "naturalPersonName.phoneticNameIdentifier",
            ivmsConstraint
        )
        Attestation.DATE_OF_BIRTH -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "dateOfBirth",
            "dateOfBirth",
            "naturalPerson.dateOfBirth",
            ivmsConstraint
        )
        Attestation.PLACE_OF_BIRTH -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "placeOfBirth",
            "placeOfBirth",
            "naturalPerson.placeOfBirth",
            ivmsConstraint
        )
        Attestation.COUNTRY_OF_RESIDENCE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "countryCode",
            "country",
            "naturalPerson.countryOfResidence",
            ivmsConstraint
        )
        Attestation.COUNTRY_OF_ISSUE -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "nationalIdentifierType",
            "countryOfIssue",
            "nationalIdentifier.countryOfIssue",
            ivmsConstraint
        )
        Attestation.COUNTRY_OF_REGISTRATION -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "legalPersonNameType",
            "countryOfRegistration",
            "legalPersonName.countryOfRegistration",
            ivmsConstraint
        )
        Attestation.NATIONAL_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "nationalIdentifierType",
            "nationalIdentifier",
            "nationalIdentification.nationalIdentifier",
            ivmsConstraint
        )
        Attestation.CUSTOMER_IDENTIFICATION -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "customerIdentification",
            "customerIdentification",
            "legalPerson.customerIdentification",
            ivmsConstraint
        )
        Attestation.REGISTRATION_AUTHORITY -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "registrationAuthority",
            "registrationAuthority",
            "nationalIdentification.registrationAuthorityName",
            ivmsConstraint
        )
        Attestation.ACCOUNT_NUMBER -> String.format(
            PRINCIPAL_STRING,
            data64Characters,
            extraData,
            "accountNumber",
            "accountNumber",
            "beneficiary.accountNumber",
            ivmsConstraint
        )
    }
}

internal fun Attestation.validateConstraint(ivmsConstraint: IvmsConstraint) = when (this) {
    Attestation.LEGAL_PERSON_NAME,
    Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER -> ivmsConstraint == IvmsConstraint.LEGL ||
            ivmsConstraint == IvmsConstraint.SHRT ||
            ivmsConstraint == IvmsConstraint.TRAD
    Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER,
    Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER,
    Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER -> ivmsConstraint == IvmsConstraint.ALIA ||
            ivmsConstraint == IvmsConstraint.BIRT ||
            ivmsConstraint == IvmsConstraint.MAID ||
            ivmsConstraint == IvmsConstraint.LEGL ||
            ivmsConstraint == IvmsConstraint.MISC
    Attestation.ADDRESS_DEPARTMENT,
    Attestation.ADDRESS_SUB_DEPARTMENT,
    Attestation.ADDRESS_STREET_NAME,
    Attestation.ADDRESS_BUILDING_NUMBER,
    Attestation.ADDRESS_BUILDING_NAME,
    Attestation.ADDRESS_FLOOR,
    Attestation.ADDRESS_POSTBOX,
    Attestation.ADDRESS_ROOM,
    Attestation.ADDRESS_POSTCODE,
    Attestation.ADDRESS_TOWN_NAME,
    Attestation.ADDRESS_TOWN_LOCATION_NAME,
    Attestation.ADDRESS_DISTRICT_NAME,
    Attestation.ADDRESS_COUNTRY_SUB_DIVISION,
    Attestation.ADDRESS_ADDRESS_LINE,
    Attestation.ADDRESS_COUNTRY -> ivmsConstraint == IvmsConstraint.GEOG ||
            ivmsConstraint == IvmsConstraint.BIZZ ||
            ivmsConstraint == IvmsConstraint.HOME
    Attestation.NATIONAL_IDENTIFIER -> ivmsConstraint == IvmsConstraint.ARNU ||
            ivmsConstraint == IvmsConstraint.CCPT ||
            ivmsConstraint == IvmsConstraint.RAID ||
            ivmsConstraint == IvmsConstraint.DRLC ||
            ivmsConstraint == IvmsConstraint.FIIN ||
            ivmsConstraint == IvmsConstraint.TXID ||
            ivmsConstraint == IvmsConstraint.SOCS ||
            ivmsConstraint == IvmsConstraint.IDCD ||
            ivmsConstraint == IvmsConstraint.LEIX ||
            ivmsConstraint == IvmsConstraint.MISC
    Attestation.REGISTRATION_AUTHORITY,
    Attestation.ACCOUNT_NUMBER,
    Attestation.CUSTOMER_IDENTIFICATION -> ivmsConstraint == IvmsConstraint.TEXT
    Attestation.PLACE_OF_BIRTH,
    Attestation.COUNTRY_OF_REGISTRATION,
    Attestation.COUNTRY_OF_ISSUE,
    Attestation.COUNTRY_OF_RESIDENCE -> ivmsConstraint == IvmsConstraint.COUNTRYCODE
    Attestation.DATE_OF_BIRTH -> ivmsConstraint == IvmsConstraint.DATE
}
