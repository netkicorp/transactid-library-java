package com.netki.keymanagement.util

import com.netki.exceptions.CertificateProviderException
import com.netki.model.Attestation
import com.netki.model.IvmsConstraints
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

    @Test
    fun `Validate IVMS constrains for LEGAL_PERSON_PRIMARY_NAME`() {
        assertTrue(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertTrue(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertTrue(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.MAID))
        assertTrue(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertTrue(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.LEGAL_PERSON_PRIMARY_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for LEGAL_PERSON_SECONDARY_NAME`() {
        assertTrue(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertTrue(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertTrue(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.MAID))
        assertTrue(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertTrue(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.LEGAL_PERSON_SECONDARY_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for NATURAL_PERSON_FIRST_NAME`() {
        assertTrue(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertTrue(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertTrue(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.MAID))
        assertTrue(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertTrue(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.NATURAL_PERSON_FIRST_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for NATURAL_PERSON_LAST_NAME`() {
        assertTrue(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertTrue(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertTrue(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.MAID))
        assertTrue(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertTrue(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.NATURAL_PERSON_LAST_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for BENEFICIARY_PERSON_FIRST_NAME`() {
        assertTrue(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertTrue(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertTrue(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.MAID))
        assertTrue(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertTrue(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.BENEFICIARY_PERSON_FIRST_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for BENEFICIARY_PERSON_LAST_NAME`() {
        assertTrue(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertTrue(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertTrue(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.MAID))
        assertTrue(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertTrue(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.BENEFICIARY_PERSON_LAST_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_DEPARTMENT`() {
        assertTrue(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_DEPARTMENT.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_SUB_DEPARTMENT`() {
        assertTrue(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_SUB_DEPARTMENT.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_STREET_NAME`() {
        assertTrue(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_STREET_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_BUILDING_NUMBER`() {
        assertTrue(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_BUILDING_NUMBER.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_BUILDING_NAME`() {
        assertTrue(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_BUILDING_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_FLOOR`() {
        assertTrue(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_FLOOR.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_POSTBOX`() {
        assertTrue(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_POSTBOX.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_ROOM`() {
        assertTrue(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_ROOM.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_POSTCODE`() {
        assertTrue(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_POSTCODE.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_TOWN_NAME`() {
        assertTrue(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_TOWN_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_TOWN_LOCATION_NAME`() {
        assertTrue(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_TOWN_LOCATION_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_DISTRICT_NAME`() {
        assertTrue(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_DISTRICT_NAME.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_COUNTRY_SUB_DIVISION`() {
        assertTrue(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_ADDRESS_LINE`() {
        assertTrue(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_ADDRESS_LINE.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for ADDRESS_COUNTRY`() {
        assertTrue(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.GEOG))
        assertTrue(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.BIZZ))
        assertTrue(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ADDRESS_COUNTRY.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for NATIONAL_IDENTIFIER`() {
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.ARNU))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.CCPT))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.RAID))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.DRLC))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.FIIN))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.TXID))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.SOCS))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.IDCD))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.LEIX))
        assertTrue(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(IvmsConstraints.LEGL))
        assertFalse(Attestation.NATIONAL_IDENTIFIER.validateConstraint(null))
    }

    @Test
    fun `Validate IVMS constrains for BIRTH_DATE`() {
        assertTrue(Attestation.BIRTH_DATE.validateConstraint(null))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.BIRTH_DATE.validateConstraint(IvmsConstraints.LEGL))
    }

    @Test
    fun `Validate IVMS constrains for BIRTH_PLACE`() {
        assertTrue(Attestation.BIRTH_PLACE.validateConstraint(null))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.BIRTH_PLACE.validateConstraint(IvmsConstraints.LEGL))
    }

    @Test
    fun `Validate IVMS constrains for COUNTRY_OF_RESIDENCE`() {
        assertTrue(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(null))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.COUNTRY_OF_RESIDENCE.validateConstraint(IvmsConstraints.LEGL))
    }

    @Test
    fun `Validate IVMS constrains for COUNTRY_OF_ISSUE`() {
        assertTrue(Attestation.COUNTRY_OF_ISSUE.validateConstraint(null))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.COUNTRY_OF_ISSUE.validateConstraint(IvmsConstraints.LEGL))
    }

    @Test
    fun `Validate IVMS constrains for COUNTRY_OF_REGISTRATION`() {
        assertTrue(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(null))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.COUNTRY_OF_REGISTRATION.validateConstraint(IvmsConstraints.LEGL))
    }

    @Test
    fun `Validate IVMS constrains for ACCOUNT_NUMBER`() {
        assertTrue(Attestation.ACCOUNT_NUMBER.validateConstraint(null))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.ACCOUNT_NUMBER.validateConstraint(IvmsConstraints.LEGL))
    }

    @Test
    fun `Validate IVMS constrains for CUSTOMER_IDENTIFICATION`() {
        assertTrue(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(null))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.CUSTOMER_IDENTIFICATION.validateConstraint(IvmsConstraints.LEGL))
    }

    @Test
    fun `Validate IVMS constrains for REGISTRATION_AUTHORITY`() {
        assertTrue(Attestation.REGISTRATION_AUTHORITY.validateConstraint(null))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.ARNU))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.CCPT))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.RAID))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.DRLC))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.FIIN))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.TXID))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.SOCS))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.IDCD))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.LEIX))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.MISC))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.GEOG))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.BIZZ))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.HOME))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.ALIA))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.BIRT))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.MAID))
        assertFalse(Attestation.REGISTRATION_AUTHORITY.validateConstraint(IvmsConstraints.LEGL))
    }
}

