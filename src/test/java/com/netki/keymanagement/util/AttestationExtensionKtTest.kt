package com.netki.keymanagement.util

import com.netki.model.Attestation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AttestationExtensionKtTest {

    @Test
    fun `Test all the principal combinations got all attestations`() {
        val data = "testData"
        assertEquals(
            Attestation.NATURAL_PERSON_FIRST_NAME.toPrincipal(data),
            "CN=naturalName.primaryIdentifier, C=, L=naturalPersonNameType=, O=naturalFirst=, OU=$data, ST=ALIAS"
        )
        assertEquals(
            Attestation.NATURAL_PERSON_LAST_NAME.toPrincipal(data),
            "CN=naturalName.secondaryIdentifier, C=, L=naturalPersonNameType=, O=naturalLast=, OU=$data, ST=LEGL"
        )
        assertEquals(
            Attestation.LEGAL_PERSON_NAME.toPrincipal(data),
            "CN=legalPersonName.primaryIdentifier, C=Legal Entity, L=legalPersonNameIdentifierType=, O=legalPersonName=, OU=$data, ST=LEGL"
        )
        assertEquals(
            Attestation.BENEFICIARY_PERSON_FIRST_NAME.toPrincipal(data),
            "CN=beneficiaryName.primaryIdentifier, C=, L=naturalPersonNameType=, O=beneficiaryFirst=, OU=$data, ST=ALIAS"
        )
        assertEquals(
            Attestation.BENEFICIARY_PERSON_LAST_NAME.toPrincipal(data),
            "CN=beneficiaryName.secondaryIdentifier, C=, L=naturalPersonNameType=, O=beneficiaryLast=, OU=$data, ST=LEGL"
        )
        assertEquals(
            Attestation.BIRTH_DATE.toPrincipal(data),
            "CN=dateOfBirth, C=, L=dateInPast=, O=birthdate=, OU=$data, ST=XX"
        )
        assertEquals(
            Attestation.BIRTH_PLACE.toPrincipal(data),
            "CN=placeOfBirth, C=, L=countryCode=, O=country=, OU=$data, ST=XX"
        )
        assertEquals(
            Attestation.ADDRESS_1.toPrincipal(data),
            "CN=geographicAddress, C=, L=addressTypeCode=, O=address=, OU=$data, ST=HOME"
        )
        assertEquals(
            Attestation.ADDRESS_2.toPrincipal(data),
            "CN=geographicAddress2, C=, L=addressTypeCode=, O=address=, OU=$data, ST=GEOG"
        )
        assertEquals(
            Attestation.COUNTRY_OF_RESIDENCE.toPrincipal(data),
            "CN=countryOfResidence, C=, L=countryCode=, O=country=, OU=$data, ST=XX"
        )
        assertEquals(
            Attestation.ISSUING_COUNTRY.toPrincipal(data),
            "CN=nationalIdentifier, C=Miscellaneous, L=nationalIdentifierType=, O=nationalIdentifier=, OU=$data, ST=MISC"
        )
        assertEquals(
            Attestation.NATIONAL_IDENTIFIER_NUMBER.toPrincipal(data),
            "CN=nationalIdentifier.number, C=, L=number=, O=docnumber=, OU=$data, ST="
        )
        assertEquals(
            Attestation.NATIONAL_IDENTIFIER.toPrincipal(data),
            "CN=nationalIdentifier.docType, C=Drivers License, L=nationalIdentifierType=, O=doctype=, OU=$data, ST=DRLC"
        )
        assertEquals(
            Attestation.ACCOUNT_NUMBER.toPrincipal(data),
            "CN=accountNumber, C=, L=accountNumber=, O=accountNumber=, OU=$data, ST="
        )
        assertEquals(
            Attestation.CUSTOMER_IDENTIFICATION.toPrincipal(data),
            "CN=customerIdentification, C=, L=customerIdentification=, O=customerIdentification=, OU=$data, ST=XX"
        )
        assertEquals(
            Attestation.REGISTRATION_AUTHORITY.toPrincipal(data),
            "CN=registrationAuthority, C=, L=registrationAuthority=, O=registrationAuthority=, OU=$data, ST="
        )
    }
}

