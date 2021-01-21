package com.netki.keymanagement.util

import com.netki.exceptions.CertificateProviderException
import com.netki.model.Attestation
import com.netki.model.IvmsConstraint
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AttestationExtensionKtTest {

    @Test
    fun `Test all the attestations principal string`() {
        val data = "testData"
        val data64Characters = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean"
        val extraData = "This is just random data"
        val dataOver64Characters = "$data64Characters$extraData"

        assertEquals(
            Attestation.LEGAL_PERSON_NAME.toPrincipal(dataOver64Characters, IvmsConstraint.LEGL),
            "CN=$data64Characters, C=$extraData, L=legalPersonNameType, O=legalPersonName, OU=legalPerson.legalPersonName, ST=${IvmsConstraint.LEGL}"
        )
        assertEquals(
            Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraint.SHRT),
            "CN=$data64Characters, C=$extraData, L=legalPersonNameType, O=legalPersonPhoneticNameIdentifier, OU=legalPerson.phoneticNameIdentifier, ST=${IvmsConstraint.SHRT}"
        )
        assertEquals(
            Attestation.ADDRESS_DEPARTMENT.toPrincipal(dataOver64Characters, IvmsConstraint.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=department, OU=address.department, ST=${IvmsConstraint.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_SUB_DEPARTMENT.toPrincipal(dataOver64Characters, IvmsConstraint.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=subDepartment, OU=address.subDepartment, ST=${IvmsConstraint.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_STREET_NAME.toPrincipal(dataOver64Characters, IvmsConstraint.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=streetName, OU=address.streetName, ST=${IvmsConstraint.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_BUILDING_NUMBER.toPrincipal(dataOver64Characters, IvmsConstraint.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=buildingNumber, OU=address.buildingNumber, ST=${IvmsConstraint.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_BUILDING_NAME.toPrincipal(dataOver64Characters, IvmsConstraint.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=buildingName, OU=address.buildingName, ST=${IvmsConstraint.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_FLOOR.toPrincipal(dataOver64Characters, IvmsConstraint.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=floor, OU=address.floor, ST=${IvmsConstraint.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_POSTBOX.toPrincipal(dataOver64Characters, IvmsConstraint.GEOG),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=postBox, OU=address.postBox, ST=${IvmsConstraint.GEOG}"
        )
        assertEquals(
            Attestation.ADDRESS_ROOM.toPrincipal(dataOver64Characters, IvmsConstraint.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=room, OU=address.room, ST=${IvmsConstraint.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_POSTCODE.toPrincipal(dataOver64Characters, IvmsConstraint.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=postcode, OU=address.postcode, ST=${IvmsConstraint.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_TOWN_NAME.toPrincipal(dataOver64Characters, IvmsConstraint.HOME),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=townName, OU=address.townName, ST=${IvmsConstraint.HOME}"
        )
        assertEquals(
            Attestation.ADDRESS_TOWN_LOCATION_NAME.toPrincipal(dataOver64Characters, IvmsConstraint.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=townLocationName, OU=address.townLocationName, ST=${IvmsConstraint.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_DISTRICT_NAME.toPrincipal(dataOver64Characters, IvmsConstraint.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=districtName, OU=address.districtName, ST=${IvmsConstraint.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_COUNTRY_SUB_DIVISION.toPrincipal(dataOver64Characters, IvmsConstraint.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=countrySubDivision, OU=address.countrySubDivision, ST=${IvmsConstraint.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_ADDRESS_LINE.toPrincipal(dataOver64Characters, IvmsConstraint.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=addressLine, OU=address.addressLine, ST=${IvmsConstraint.BIZZ}"
        )
        assertEquals(
            Attestation.ADDRESS_COUNTRY.toPrincipal(dataOver64Characters, IvmsConstraint.BIZZ),
            "CN=$data64Characters, C=$extraData, L=addressTypeCode, O=country, OU=address.country, ST=${IvmsConstraint.BIZZ}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraint.MAID),
            "CN=$data64Characters, C=$extraData, L=naturalPersonNameType, O=naturalPersonPrimaryIdentifier, OU=naturalName.primaryIdentifier, ST=${IvmsConstraint.MAID}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraint.MISC),
            "CN=$data64Characters, C=$extraData, L=naturalPersonNameType, O=naturalPersonSecondaryIdentifier, OU=naturalName.secondaryIdentifier, ST=${IvmsConstraint.MISC}"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.toPrincipal(dataOver64Characters, IvmsConstraint.BIRT),
            "CN=$data64Characters, C=$extraData, L=naturalPersonNameidentifierType, O=naturalPersonPhoneticNameIdentifier, OU=naturalPersonName.phoneticNameIdentifier, ST=${IvmsConstraint.BIRT}"
        )
        assertEquals(
            Attestation.DATE_OF_BIRTH.toPrincipal(data, IvmsConstraint.DATE),
            "CN=$data, C=, L=dateOfBirth, O=dateOfBirth, OU=naturalPerson.dateOfBirth, ST=${IvmsConstraint.DATE}"
        )
        assertEquals(
            Attestation.PLACE_OF_BIRTH.toPrincipal(data, IvmsConstraint.COUNTRYCODE),
            "CN=$data, C=, L=placeOfBirth, O=placeOfBirth, OU=naturalPerson.placeOfBirth, ST=${IvmsConstraint.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.COUNTRY_OF_RESIDENCE.toPrincipal(data, IvmsConstraint.COUNTRYCODE),
            "CN=$data, C=, L=countryCode, O=country, OU=naturalPerson.countryOfResidence, ST=${IvmsConstraint.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.COUNTRY_OF_ISSUE.toPrincipal(data, IvmsConstraint.COUNTRYCODE),
            "CN=$data, C=, L=nationalIdentifierType, O=countryOfIssue, OU=nationalIdentifier.countryOfIssue, ST=${IvmsConstraint.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.COUNTRY_OF_REGISTRATION.toPrincipal(data, IvmsConstraint.COUNTRYCODE),
            "CN=$data, C=, L=legalPersonNameType, O=countryOfRegistration, OU=legalPersonName.countryOfRegistration, ST=${IvmsConstraint.COUNTRYCODE}"
        )
        assertEquals(
            Attestation.NATIONAL_IDENTIFIER.toPrincipal(data, IvmsConstraint.TXID),
            "CN=$data, C=, L=nationalIdentifierType, O=nationalIdentifier, OU=nationalIdentification.nationalIdentifier, ST=${IvmsConstraint.TXID}"
        )
        assertEquals(
            Attestation.CUSTOMER_IDENTIFICATION.toPrincipal(data, IvmsConstraint.TEXT),
            "CN=$data, C=, L=customerIdentification, O=customerIdentification, OU=legalPerson.customerIdentification, ST=${IvmsConstraint.TEXT}"
        )
        assertEquals(
            Attestation.REGISTRATION_AUTHORITY.toPrincipal(data, IvmsConstraint.TEXT),
            "CN=$data, C=, L=registrationAuthority, O=registrationAuthority, OU=nationalIdentification.registrationAuthorityName, ST=${IvmsConstraint.TEXT}"
        )
        assertEquals(
            Attestation.ACCOUNT_NUMBER.toPrincipal(data, IvmsConstraint.TEXT),
            "CN=$data, C=, L=accountNumber, O=accountNumber, OU=beneficiary.accountNumber, ST=${IvmsConstraint.TEXT}"
        )
    }

    @Test
    fun `Pass incorrect ivmsConstraint to an attestation`() {
        val exception = Assertions.assertThrows(CertificateProviderException::class.java) {
            Attestation.LEGAL_PERSON_NAME.toPrincipal("Data", IvmsConstraint.TXID)
        }

        assertTrue(exception.message!!.contains("for the attestation:"))
    }

    @Test
    fun `Validate IVMS constrains for LEGAL_PERSON_PRIMARY_NAME`() {
        assertTrue(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.LEGL))
        assertTrue(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.SHRT))
        assertTrue(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.LEGAL_PERSON_NAME.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for LEGAL_PERSON_SECONDARY_NAME`() {
        assertTrue(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.LEGL))
        assertTrue(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.SHRT))
        assertTrue(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for NATURAL_PERSON_FIRST_NAME`() {
        assertTrue(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.ALIA))
        assertTrue(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.BIRT))
        assertTrue(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.MAID))
        assertTrue(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.LEGL))
        assertTrue(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.NATURAL_PERSON_PRIMARY_IDENTIFIER.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for NATURAL_PERSON_LAST_NAME`() {
        assertTrue(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.ALIA))
        assertTrue(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.BIRT))
        assertTrue(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.MAID))
        assertTrue(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.LEGL))
        assertTrue(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.NATURAL_PERSON_SECONDARY_IDENTIFIER.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for BENEFICIARY_PERSON_FIRST_NAME`() {
        assertTrue(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.ALIA))
        assertTrue(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.BIRT))
        assertTrue(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.MAID))
        assertTrue(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.LEGL))
        assertTrue(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.NATURAL_PERSON_PHONETIC_NAME_IDENTIFIER.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_DEPARTMENT`() {
        assertTrue(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_SUB_DEPARTMENT`() {
        assertTrue(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_STREET_NAME`() {
        assertTrue(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_BUILDING_NUMBER`() {
        assertTrue(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_BUILDING_NAME`() {
        assertTrue(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_FLOOR`() {
        assertTrue(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_POSTBOX`() {
        assertTrue(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_ROOM`() {
        assertTrue(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_POSTCODE`() {
        assertTrue(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_TOWN_NAME`() {
        assertTrue(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_TOWN_LOCATION_NAME`() {
        assertTrue(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_DISTRICT_NAME`() {
        assertTrue(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_COUNTRY_SUB_DIVISION`() {
        assertTrue(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_ADDRESS_LINE`() {
        assertTrue(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_COUNTRY`() {
        assertTrue(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.GEOG))
        assertTrue(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.BIZZ))
        assertTrue(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for NATIONAL_IDENTIFIER`() {
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.ARNU))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.CCPT))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.RAID))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.DRLC))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.FIIN))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.TXID))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.SOCS))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.IDCD))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.LEIX))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for BIRTH_DATE`() {
        assertTrue(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.DATE_OF_BIRTH.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for BIRTH_PLACE`() {
        assertTrue(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.COUNTRYCODE))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.PLACE_OF_BIRTH.validateConstraint(IvmsConstraint.DATE))
    }

    @Test
    fun `Validate IVMS constrains for COUNTRY_OF_RESIDENCE`() {
        assertTrue(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.COUNTRYCODE))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraint.DATE))
    }

    @Test
    fun `Validate IVMS constrains for COUNTRY_OF_ISSUE`() {
        assertTrue(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.COUNTRYCODE))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraint.DATE))
    }

    @Test
    fun `Validate IVMS constrains for COUNTRY_OF_REGISTRATION`() {
        assertTrue(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.COUNTRYCODE))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraint.DATE))
    }

    @Test
    fun `Validate IVMS constrains for ACCOUNT_NUMBER`() {
        assertTrue(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for CUSTOMER_IDENTIFICATION`() {
        assertTrue(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }

    @Test
    fun `Validate IVMS constrains for REGISTRATION_AUTHORITY`() {
        assertTrue(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.TEXT))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.ARNU))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.CCPT))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.RAID))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.DRLC))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.FIIN))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.TXID))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.SOCS))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.IDCD))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.LEIX))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.MISC))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.GEOG))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.BIZZ))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.HOME))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.ALIA))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.BIRT))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.MAID))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.LEGL))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.SHRT))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.TRAD))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.DATE))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraint.COUNTRYCODE))
    }
}

