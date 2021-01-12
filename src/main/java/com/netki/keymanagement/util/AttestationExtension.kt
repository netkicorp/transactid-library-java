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
        Attestation.LEGAL_PERSON_PRIMARY_NAME -> String.format(
            PRINCIPAL_STRING,
            "legalPersonName.primaryIdentifier",
            extraData,
            "legalPersonNameIdentifierType",
            "legalPrimaryName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.LEGAL_PERSON_SECONDARY_NAME -> String.format(
            PRINCIPAL_STRING,
            "legalPersonName.secondaryIdentifier",
            extraData,
            "legalPersonNameIdentifierType",
            "legalSecondaryName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_DEPARTMENT -> String.format(
            PRINCIPAL_STRING,
            "address.department",
            extraData,
            "addressTypeCode",
            "department",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_SUB_DEPARTMENT -> String.format(
            PRINCIPAL_STRING,
            "address.subDepartment",
            extraData,
            "addressTypeCode",
            "subDepartment",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_STREET_NAME -> String.format(
            PRINCIPAL_STRING,
            "address.streetName",
            extraData,
            "addressTypeCode",
            "streetName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_BUILDING_NUMBER -> String.format(
            PRINCIPAL_STRING,
            "address.buildingNumber",
            extraData,
            "addressTypeCode",
            "buildingNumber",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_BUILDING_NAME -> String.format(
            PRINCIPAL_STRING,
            "address.buildingName",
            extraData,
            "addressTypeCode",
            "buildingName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_FLOOR -> String.format(
            PRINCIPAL_STRING,
            "address.floor",
            extraData,
            "addressTypeCode",
            "floor",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_POSTBOX -> String.format(
            PRINCIPAL_STRING,
            "address.postBox",
            extraData,
            "addressTypeCode",
            "postBox",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_ROOM -> String.format(
            PRINCIPAL_STRING,
            "address.room",
            extraData,
            "addressTypeCode",
            "room",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_POSTCODE -> String.format(
            PRINCIPAL_STRING,
            "address.postCode",
            extraData,
            "addressTypeCode",
            "postCode",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_TOWN_NAME -> String.format(
            PRINCIPAL_STRING,
            "address.townName",
            extraData,
            "addressTypeCode",
            "townName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_TOWN_LOCATION_NAME -> String.format(
            PRINCIPAL_STRING,
            "address.townLocationName",
            extraData,
            "addressTypeCode",
            "townLocationName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_DISTRICT_NAME -> String.format(
            PRINCIPAL_STRING,
            "address.districtName",
            extraData,
            "addressTypeCode",
            "districtName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_COUNTRY_SUB_DIVISION -> String.format(
            PRINCIPAL_STRING,
            "address.countrySubDivision",
            extraData,
            "addressTypeCode",
            "countrySubDivision",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_ADDRESS_LINE -> String.format(
            PRINCIPAL_STRING,
            "address.addressLine",
            extraData,
            "addressTypeCode",
            "addressLine",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ADDRESS_COUNTRY -> String.format(
            PRINCIPAL_STRING,
            "address.country",
            extraData,
            "addressTypeCode",
            "country",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            "naturalName.secondaryIdentifier",
            extraData,
            "naturalPersonNameType",
            "naturalPersonSecondaryIdentifier",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            "naturalName.primaryIdentifier",
            extraData,
            "naturalPersonNameType",
            "naturalPersonPrimaryIdentffier",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.BENEFICIARY_PERSON_FIRST_NAME -> String.format(
            PRINCIPAL_STRING,
            "beneficiaryName.secondaryIdentifier",
            extraData,
            "beneficiaryPersonNameType",
            "beneficiaryPersonFirstName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.BENEFICIARY_PERSON_LAST_NAME -> String.format(
            PRINCIPAL_STRING,
            "beneficiaryName.primaryIdentifier",
            extraData,
            "beneficiaryPersonNameType",
            "beneficiaryPersonLastName",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.BIRTH_DATE -> String.format(
            PRINCIPAL_STRING,
            "naturalPerson.dateOfBirth",
            extraData,
            "dateInPast",
            "birthdate",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.BIRTH_PLACE -> String.format(
            PRINCIPAL_STRING,
            "naturalPerson.placeOfBirth",
            extraData,
            "countryCode",
            "country",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.COUNTRY_OF_RESIDENCE -> String.format(
            PRINCIPAL_STRING,
            "naturalPerson.countryOfResidence",
            extraData,
            "countryCode",
            "country",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.COUNTRY_OF_ISSUE -> String.format(
            PRINCIPAL_STRING,
            "nationalIdentifier.countryOfIssue",
            extraData,
            "nationalIdentifierType",
            "nationalIdentifier",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.COUNTRY_OF_REGISTRATION -> String.format(
            PRINCIPAL_STRING,
            "legalPersonName.countryOfRegistration",
            extraData,
            "legalPersonNameType",
            "countryOfRegistration",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.NATIONAL_IDENTIFIER -> String.format(
            PRINCIPAL_STRING,
            "nationalIdentifier.documentType",
            extraData,
            "nationalIdentifierType",
            "documentType",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.ACCOUNT_NUMBER -> String.format(
            PRINCIPAL_STRING,
            "accountNumber",
            extraData,
            "accountNumber",
            "accountNumber",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.CUSTOMER_IDENTIFICATION -> String.format(
            PRINCIPAL_STRING,
            "customerIdentification",
            extraData,
            "customerIdentification",
            "customerIdentification",
            data64Characters,
            ivmConstraintValue
        )
        Attestation.REGISTRATION_AUTHORITY -> String.format(
            PRINCIPAL_STRING,
            "GLEIFregistrationAuthorityName",
            extraData,
            "GLEIFregistrationAuthority",
            "GLEIFregistrationAuthority",
            data64Characters,
            ivmConstraintValue
        )
    }
}

internal fun Attestation.validateConstraint(ivmsConstraints: IvmsConstraints?) = when (this) {
    Attestation.LEGAL_PERSON_PRIMARY_NAME,
    Attestation.LEGAL_PERSON_SECONDARY_NAME,
    Attestation.NATURAL_PERSON_FIRST_NAME,
    Attestation.NATURAL_PERSON_LAST_NAME,
    Attestation.BENEFICIARY_PERSON_FIRST_NAME,
    Attestation.BENEFICIARY_PERSON_LAST_NAME -> ivmsConstraints == IvmsConstraints.ALIA ||
            ivmsConstraints == IvmsConstraints.BIRT ||
            ivmsConstraints == IvmsConstraints.MAID ||
            ivmsConstraints == IvmsConstraints.LEGL ||
            ivmsConstraints == IvmsConstraints.MISC
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
    else -> ivmsConstraints == null
}
