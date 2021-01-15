package com.netki.bip75.util

import com.netki.bip75.protocol.Messages
import com.netki.model.AddressCurrency
import com.netki.model.Attestation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MessagesExtensionsKtTest {

    @Test
    fun `Test Attestation Object conversion to Message Object`() {
        assertEquals(Attestation.LEGAL_PERSON_PRIMARY_NAME.toAttestationType(), Messages.AttestationType.LEGAL_PERSON_PRIMARY_NAME)
        assertEquals(Attestation.LEGAL_PERSON_SECONDARY_NAME.toAttestationType(), Messages.AttestationType.LEGAL_PERSON_SECONDARY_NAME)
        assertEquals(Attestation.ADDRESS_DEPARTMENT.toAttestationType(), Messages.AttestationType.ADDRESS_DEPARTMENT)
        assertEquals(Attestation.ADDRESS_SUB_DEPARTMENT.toAttestationType(), Messages.AttestationType.ADDRESS_SUB_DEPARTMENT)
        assertEquals(Attestation.ADDRESS_STREET_NAME.toAttestationType(), Messages.AttestationType.ADDRESS_STREET_NAME)
        assertEquals(Attestation.ADDRESS_BUILDING_NUMBER.toAttestationType(), Messages.AttestationType.ADDRESS_BUILDING_NUMBER)
        assertEquals(Attestation.ADDRESS_BUILDING_NAME.toAttestationType(), Messages.AttestationType.ADDRESS_BUILDING_NAME)
        assertEquals(Attestation.ADDRESS_FLOOR.toAttestationType(), Messages.AttestationType.ADDRESS_FLOOR)
        assertEquals(Attestation.ADDRESS_POSTBOX.toAttestationType(), Messages.AttestationType.ADDRESS_POSTBOX)
        assertEquals(Attestation.ADDRESS_ROOM.toAttestationType(), Messages.AttestationType.ADDRESS_ROOM)
        assertEquals(Attestation.ADDRESS_POSTCODE.toAttestationType(), Messages.AttestationType.ADDRESS_POSTCODE)
        assertEquals(Attestation.ADDRESS_TOWN_NAME.toAttestationType(), Messages.AttestationType.ADDRESS_TOWN_NAME)
        assertEquals(Attestation.ADDRESS_TOWN_LOCATION_NAME.toAttestationType(), Messages.AttestationType.ADDRESS_TOWN_LOCATION_NAME)
        assertEquals(Attestation.ADDRESS_DISTRICT_NAME.toAttestationType(), Messages.AttestationType.ADDRESS_DISTRICT_NAME)
        assertEquals(Attestation.ADDRESS_COUNTRY_SUB_DIVISION.toAttestationType(), Messages.AttestationType.ADDRESS_COUNTRY_SUB_DIVISION)
        assertEquals(Attestation.ADDRESS_ADDRESS_LINE.toAttestationType(), Messages.AttestationType.ADDRESS_ADDRESS_LINE)
        assertEquals(Attestation.ADDRESS_COUNTRY.toAttestationType(), Messages.AttestationType.ADDRESS_COUNTRY)
        assertEquals(Attestation.NATURAL_PERSON_FIRST_NAME.toAttestationType(), Messages.AttestationType.NATURAL_PERSON_FIRST_NAME)
        assertEquals(Attestation.NATURAL_PERSON_LAST_NAME.toAttestationType(), Messages.AttestationType.NATURAL_PERSON_LAST_NAME)
        assertEquals(Attestation.BENEFICIARY_PERSON_FIRST_NAME.toAttestationType(), Messages.AttestationType.BENEFICIARY_PERSON_FIRST_NAME)
        assertEquals(Attestation.BENEFICIARY_PERSON_LAST_NAME.toAttestationType(), Messages.AttestationType.BENEFICIARY_PERSON_LAST_NAME)
        assertEquals(Attestation.BIRTH_DATE.toAttestationType(), Messages.AttestationType.BIRTH_DATE)
        assertEquals(Attestation.BIRTH_PLACE.toAttestationType(), Messages.AttestationType.BIRTH_PLACE)
        assertEquals(Attestation.COUNTRY_OF_RESIDENCE.toAttestationType(), Messages.AttestationType.COUNTRY_OF_RESIDENCE)
        assertEquals(Attestation.COUNTRY_OF_ISSUE.toAttestationType(), Messages.AttestationType.COUNTRY_OF_ISSUE)
        assertEquals(Attestation.COUNTRY_OF_REGISTRATION.toAttestationType(), Messages.AttestationType.COUNTRY_OF_REGISTRATION)
        assertEquals(Attestation.NATIONAL_IDENTIFIER.toAttestationType(), Messages.AttestationType.NATIONAL_IDENTIFIER)
        assertEquals(Attestation.ACCOUNT_NUMBER.toAttestationType(), Messages.AttestationType.ACCOUNT_NUMBER)
        assertEquals(Attestation.CUSTOMER_IDENTIFICATION.toAttestationType(), Messages.AttestationType.CUSTOMER_IDENTIFICATION)
        assertEquals(Attestation.REGISTRATION_AUTHORITY.toAttestationType(), Messages.AttestationType.REGISTRATION_AUTHORITY)
    }

    @Test
    fun `Test Message conversion to Attestation Object`() {
        assertEquals(Messages.AttestationType.LEGAL_PERSON_PRIMARY_NAME.toAttestation(), Attestation.LEGAL_PERSON_PRIMARY_NAME)
        assertEquals(Messages.AttestationType.LEGAL_PERSON_SECONDARY_NAME.toAttestation(), Attestation.LEGAL_PERSON_SECONDARY_NAME)
        assertEquals(Messages.AttestationType.ADDRESS_DEPARTMENT.toAttestation(), Attestation.ADDRESS_DEPARTMENT)
        assertEquals(Messages.AttestationType.ADDRESS_SUB_DEPARTMENT.toAttestation(), Attestation.ADDRESS_SUB_DEPARTMENT)
        assertEquals(Messages.AttestationType.ADDRESS_STREET_NAME.toAttestation(), Attestation.ADDRESS_STREET_NAME)
        assertEquals(Messages.AttestationType.ADDRESS_BUILDING_NUMBER.toAttestation(), Attestation.ADDRESS_BUILDING_NUMBER)
        assertEquals(Messages.AttestationType.ADDRESS_BUILDING_NAME.toAttestation(), Attestation.ADDRESS_BUILDING_NAME)
        assertEquals(Messages.AttestationType.ADDRESS_FLOOR.toAttestation(), Attestation.ADDRESS_FLOOR)
        assertEquals(Messages.AttestationType.ADDRESS_POSTBOX.toAttestation(), Attestation.ADDRESS_POSTBOX)
        assertEquals(Messages.AttestationType.ADDRESS_ROOM.toAttestation(), Attestation.ADDRESS_ROOM)
        assertEquals(Messages.AttestationType.ADDRESS_POSTCODE.toAttestation(), Attestation.ADDRESS_POSTCODE)
        assertEquals(Messages.AttestationType.ADDRESS_TOWN_NAME.toAttestation(), Attestation.ADDRESS_TOWN_NAME)
        assertEquals(Messages.AttestationType.ADDRESS_TOWN_LOCATION_NAME.toAttestation(), Attestation.ADDRESS_TOWN_LOCATION_NAME)
        assertEquals(Messages.AttestationType.ADDRESS_DISTRICT_NAME.toAttestation(), Attestation.ADDRESS_DISTRICT_NAME)
        assertEquals(Messages.AttestationType.ADDRESS_COUNTRY_SUB_DIVISION.toAttestation(), Attestation.ADDRESS_COUNTRY_SUB_DIVISION)
        assertEquals(Messages.AttestationType.ADDRESS_ADDRESS_LINE.toAttestation(), Attestation.ADDRESS_ADDRESS_LINE)
        assertEquals(Messages.AttestationType.ADDRESS_COUNTRY.toAttestation(), Attestation.ADDRESS_COUNTRY)
        assertEquals(Messages.AttestationType.NATURAL_PERSON_FIRST_NAME.toAttestation(), Attestation.NATURAL_PERSON_FIRST_NAME)
        assertEquals(Messages.AttestationType.NATURAL_PERSON_LAST_NAME.toAttestation(), Attestation.NATURAL_PERSON_LAST_NAME)
        assertEquals(Messages.AttestationType.BENEFICIARY_PERSON_FIRST_NAME.toAttestation(), Attestation.BENEFICIARY_PERSON_FIRST_NAME)
        assertEquals(Messages.AttestationType.BENEFICIARY_PERSON_LAST_NAME.toAttestation(), Attestation.BENEFICIARY_PERSON_LAST_NAME)
        assertEquals(Messages.AttestationType.BIRTH_DATE.toAttestation(), Attestation.BIRTH_DATE)
        assertEquals(Messages.AttestationType.BIRTH_PLACE.toAttestation(), Attestation.BIRTH_PLACE)
        assertEquals(Messages.AttestationType.COUNTRY_OF_RESIDENCE.toAttestation(), Attestation.COUNTRY_OF_RESIDENCE)
        assertEquals(Messages.AttestationType.COUNTRY_OF_ISSUE.toAttestation(), Attestation.COUNTRY_OF_ISSUE)
        assertEquals(Messages.AttestationType.COUNTRY_OF_REGISTRATION.toAttestation(), Attestation.COUNTRY_OF_REGISTRATION)
        assertEquals(Messages.AttestationType.NATIONAL_IDENTIFIER.toAttestation(), Attestation.NATIONAL_IDENTIFIER)
        assertEquals(Messages.AttestationType.ACCOUNT_NUMBER.toAttestation(), Attestation.ACCOUNT_NUMBER)
        assertEquals(Messages.AttestationType.CUSTOMER_IDENTIFICATION.toAttestation(), Attestation.CUSTOMER_IDENTIFICATION)
        assertEquals(Messages.AttestationType.REGISTRATION_AUTHORITY.toAttestation(), Attestation.REGISTRATION_AUTHORITY)
    }

    @Test
    fun `Test Address Currency Object conversion to Message`() {
        assertEquals(AddressCurrency.BITCOIN.toCurrencyType(), Messages.CurrencyType.BITCOIN)
        assertEquals(AddressCurrency.ETHEREUM.toCurrencyType(), Messages.CurrencyType.ETHEREUM)
        assertEquals(AddressCurrency.LITECOIN.toCurrencyType(), Messages.CurrencyType.LITECOIN)
        assertEquals(AddressCurrency.BITCOIN_CASH.toCurrencyType(), Messages.CurrencyType.BITCOIN_CASH)
    }

    @Test
    fun `Test Message conversion to Address Currency Object `() {
        assertEquals(Messages.CurrencyType.BITCOIN.toAddressCurrency(), AddressCurrency.BITCOIN)
        assertEquals(Messages.CurrencyType.ETHEREUM.toAddressCurrency(), AddressCurrency.ETHEREUM)
        assertEquals(Messages.CurrencyType.LITECOIN.toAddressCurrency(), AddressCurrency.LITECOIN)
        assertEquals(Messages.CurrencyType.BITCOIN_CASH.toAddressCurrency(), AddressCurrency.BITCOIN_CASH)
    }
}
