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
            Attestation.LEGAL_PERSON_PRIMARY_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.ALIA),
            "CN=legalPersonName.primaryIdentifier, C=$extraData, L=legalPersonNameType, O=legalPrimaryName, OU=$data64Characters, ST=${IvmsConstraints.ALIA}"
        )
        assertEquals(
            Attestation.LEGAL_PERSON_SECONDARY_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.BIRT),
            "CN=legalPersonName.secondaryIdentifier, C=$extraData, L=legalPersonNameType, O=legalSecondaryName, OU=$data64Characters, ST=${IvmsConstraints.BIRT}"
        )
        assertEquals(
            Attestation.ADDRESS_DEPARTMENT.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=address.department, C=$extraData, L=department, O=department, OU=$data64Characters, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_SUB_DEPARTMENT.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=address.subDepartment, C=$extraData, L=subDepartment, O=subDepartment, OU=$data64Characters, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_STREET_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=address.streetName, C=$extraData, L=streetName, O=streetName, OU=$data64Characters, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_BUILDING_NUMBER.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=address.buildingNumber, C=$extraData, L=buildingNumber, O=buildingNumber, OU=$data64Characters, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_BUILDING_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=address.buildingName, C=$extraData, L=buildingName, O=buildingName, OU=$data64Characters, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_FLOOR.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=address.floor, C=$extraData, L=floor, O=floor, OU=$data64Characters, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_POSTBOX.toPrincipal(dataOver64Characters, IvmsConstraints.GEOG),
            "CN=address.postBox, C=$extraData, L=postBox, O=postBox, OU=$data64Characters, ST=${IvmsConstraints.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_ROOM.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=address.room, C=$extraData, L=room, O=room, OU=$data64Characters, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_POSTCODE.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=address.postCode, C=$extraData, L=postCode, O=postCode, OU=$data64Characters, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_TOWN_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.HOME),
            "CN=address.townName, C=$extraData, L=townName, O=townName, OU=$data64Characters, ST=${IvmsConstraints.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_TOWN_LOCATION_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=address.townLocationName, C=$extraData, L=townLocationName, O=townLocationName, OU=$data64Characters, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_DISTRICT_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=address.districtName, C=$extraData, L=districtName, O=districtName, OU=$data64Characters, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_COUNTRY_SUB_DIVISION.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=address.countrySubDivision, C=$extraData, L=countrySubDivision, O=countrySubDivision, OU=$data64Characters, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_ADDRESS_LINE.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=address.addressLine, C=$extraData, L=addressLine, O=addressLine, OU=$data64Characters, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_COUNTRY.toPrincipal(dataOver64Characters, IvmsConstraints.BIZZ),
            "CN=address.country, C=$extraData, L=country, O=country, OU=$data64Characters, ST=${IvmsConstraints.BIZZ}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_FIRST_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.MAID),
            "CN=naturalName.secondaryIdentifier, C=$extraData, L=naturalPersonNameType, O=naturalPersonFirstName, OU=$data64Characters, ST=${IvmsConstraints.MAID}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_LAST_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.MISC),
            "CN=naturalName.primaryIdentifier, C=$extraData, L=naturalPersonNameType, O=naturalPersonLastName, OU=$data64Characters, ST=${IvmsConstraints.MISC}"
        )
        assertEquals(
            Attestation.BENEFICIARY_PERSON_FIRST_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.BIRT),
            "CN=beneficiaryName.secondaryIdentifier, C=$extraData, L=beneficiaryPersonNameType, O=beneficiaryPersonFirstName, OU=$data64Characters, ST=${IvmsConstraints.BIRT}"
        )
        assertEquals(
            Attestation.BENEFICIARY_PERSON_LAST_NAME.toPrincipal(dataOver64Characters, IvmsConstraints.BIRT),
            "CN=beneficiaryName.primaryIdentifier, C=$extraData, L=beneficiaryPersonNameType, O=beneficiaryPersonLastName, OU=$data64Characters, ST=${IvmsConstraints.BIRT}"
        )
        assertEquals(
            Attestation.BIRTH_DATE.toPrincipal(dataOver64Characters),
            "CN=naturalPerson.dateOfBirth, C=$extraData, L=dateInPast, O=birthdate, OU=$data64Characters, ST="
        )
        assertEquals(
            Attestation.BIRTH_PLACE.toPrincipal(data, null),
            "CN=naturalPerson.placeOfBirth, C=, L=countryCode, O=country, OU=$data, ST="
        )
        assertEquals(
            Attestation.COUNTRY_OF_RESIDENCE.toPrincipal(data),
            "CN=naturalPerson.countryOfResidence, C=, L=countryCode, O=country, OU=$data, ST="
        )
        assertEquals(
            Attestation.COUNTRY_OF_ISSUE.toPrincipal(data),
            "CN=nationalIdentifier.countryOfIssue, C=, L=nationalIdentifierType, O=nationalIdentifier, OU=$data, ST="
        )
        assertEquals(
            Attestation.COUNTRY_OF_REGISTRATION.toPrincipal(data),
            "CN=legalPersonName.countryOfRegistration, C=, L=legalPersonNameType, O=countryOfRegistration, OU=$data, ST="
        )
        assertEquals(
            Attestation.NATIONAL_IDENTIFIER.toPrincipal(data, IvmsConstraints.TXID),
            "CN=nationalIdentifier.documentType, C=, L=nationalIdentifierType, O=documentType, OU=$data, ST=${IvmsConstraints.TXID}"
        )
        assertEquals(
            Attestation.ACCOUNT_NUMBER.toPrincipal(data),
            "CN=accountNumber, C=, L=accountNumber, O=accountNumber, OU=$data, ST="
        )
        assertEquals(
            Attestation.CUSTOMER_IDENTIFICATION.toPrincipal(data),
            "CN=customerIdentification, C=, L=customerIdentification, O=customerIdentification, OU=$data, ST="
        )
        assertEquals(
            Attestation.REGISTRATION_AUTHORITY.toPrincipal(data),
            "CN=registrationAuthority, C=, L=registrationAuthority, O=registrationAuthority, OU=$data, ST="
        )
    }

    @Test
    fun `Pass incorrect ivmsConstraint to an attestation`() {
        val exception = Assertions.assertThrows(CertificateProviderException::class.java) {
            Attestation.BENEFICIARY_PERSON_LAST_NAME.toPrincipal("Data", IvmsConstraints.TXID)
        }

        assertTrue(exception.message!!.contains("for the attestation:"))
    }
}

