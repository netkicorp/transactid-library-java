package com.netki.keymanagement.util

import com.netki.exceptions.CertificateProviderException
import com.netki.model.Attestation
import com.netki.model.IvmsConstraints
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AttestationExtensionKtTest {

    @Test
    fun `Test all the attestations principal string`() {
        val data = "testData"
        val data64Characters = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean"
        val extraData = "This is just random data"
        val dataOver64Characters = "$data64Characters$extraData"

        assertEquals(
            Attestation.LEGAL_PERSON_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.LEGL),
            "CN=$data64Characters, C=$extraData, L=legalPersonNameType, O=legalPersonName, OU=legalPerson.legalPersonName, ST=${IvmsConstraints.LEGL}"
        )
        assertEquals(
            Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraints.SHRT),
            "CN=$data64Characters, C=$extraData, L=legalPersonNameType, O=phoneticNameIdentifier, OU=legalPerson.phoneticNameIdentifier, ST=${IvmsConstraints.SHRT}"
        )
        assertEquals(
            Attestation.ADDRESS_DEPARTMENT.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=department, OU=address.department, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_SUB_DEPARTMENT.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=subDepartment, OU=address.subDepartment, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_STREET_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=streetName, OU=address.streetName, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_BUILDING_NUMBER.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=buildingNumber, OU=address.buildingNumber, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_BUILDING_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=buildingName, OU=address.buildingName, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_FLOOR.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=floor, OU=address.floor, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_POSTBOX.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=postBox, OU=address.postBox, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_ROOM.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=room, OU=address.room, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_POSTCODE.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=postcode, OU=address.postcode, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_TOWN_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=townName, OU=address.townName, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_TOWN_LOCATION_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=townLocationName, OU=address.townLocationName, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_DISTRICT_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=districtName, OU=address.districtName, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_COUNTRY_SUB_DIVISION.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=countrySubDivision, OU=address.countrySubDivision, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_ADDRESS_LINE.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=addressLine, OU=address.addressLine, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_COUNTRY.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=country, OU=address.country, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraints.MAID),
            "CN=$data64Characters, C=$extraData, L=naturalPersonNameType, O=naturalPersonPrimaryIdentffier, OU=naturalName.primaryIdentifier, ST=${IvmsConstraints.MAID}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraints.MISC),
            "CN=$data64Characters, C=$extraData, L=naturalPersonNameType, O=naturalPersonSecondaryIdentifier, OU=naturalName.secondaryIdentifier, ST=${IvmsConstraints.MISC}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraints.BIRT),
            "CN=$data64Characters, C=$extraData, L=naturalPersonNameidentifierType, O=phoneticNameIdentifier, OU=naturalPersonName.phoneticNameIdentifier, ST=${IvmsConstraints.BIRT}"
        )
        assertEquals(
            Attestation.DATE_OF_BIRTH.toPrincipal(data, IvmsConstraints.DATE),
            "CN=$data, C=, L=dateOfBirth, O=dateOfBirth, OU=naturalPerson.dateOfBirth, ST=${IvmsConstraints.DATE}"
        )
        assertEquals(
            Attestation.PLACE_OF_BIRTH.toPrincipal(data, IvmsConstraints.COUNTRYCODE),
            "CN=$data, C=, L=placeOfBirth, O=placeOfBirth, OU=naturalPerson.placeOfBirth, ST=${IvmsConstraints.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.COUNTRY_OF_RESIDENCE.toPrincipal(data, IvmsConstraints.COUNTRYCODE),
            "CN=$data, C=, L=countryCode, O=country, OU=naturalPerson.countryOfResidence, ST=${IvmsConstraints.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.COUNTRY_OF_ISSUE.toPrincipal(data, IvmsConstraints.COUNTRYCODE),
            "CN=$data, C=, L=nationalIdentifierType, O=nationalIdentifier, OU=nationalIdentifier.countryOfIssue, ST=${IvmsConstraints.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.COUNTRY_OF_REGISTRATION.toPrincipal(data, IvmsConstraints.COUNTRYCODE),
            "CN=$data, C=, L=legalPersonNameType, O=countryOfRegistration, OU=legalPersonName.countryOfRegistration, ST=${IvmsConstraints.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.NATIONAL_IDENTIFIER.toPrincipal(data, IvmsConstraints.TXID),
            "CN=$data, C=, L=nationalIdentifierType, O=nationalIdentifier, OU=nationalIdentification.nationalIdentifier, ST=${IvmsConstraints.TXID}"
        )
        assertEquals(
            Attestation.CUSTOMER_IDENTIFICATION.toPrincipal(data, IvmsConstraints.TEXT),
            "CN=$data, C=, L=customerIdentification, O=customerIdentification, OU=legalPerson.customerIdentification, ST=${IvmsConstraints.TEXT}"
        )
        assertEquals(
            Attestation.REGISTRATION_AUTHORITY.toPrincipal(data, IvmsConstraints.TEXT),
            "CN=$data, C=, L=registrationAuthority, O=registrationAuthority, OU=nationalIdentification.registrationAuthorityName, ST=${IvmsConstraints.TEXT}"
        )
        assertEquals(
            Attestation.ACCOUNT_NUMBER.toPrincipal(data, IvmsConstraints.TEXT),
            "CN=$data, C=, L=accountNumber, O=accountNumber, OU=beneficiary.accountNumber, ST=${IvmsConstraints.TEXT}"
        )
    }

    @Test
    fun `Pass incorrect ivmsConstraint to an attestation`() {
        val exception = Assertions.assertThrows(CertificateProviderException::class.java) {
            Attestation.LEGAL_PERSON_NAME.toPrincipal("Data", IvmsConstraints.TXID)
        }

        assertTrue(exception.message!!.contains("for the attestation:"))
    }
}

