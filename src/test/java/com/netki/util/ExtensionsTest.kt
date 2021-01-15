package com.netki.util

import com.netki.bip75.util.validate
import com.netki.exceptions.InvalidOwnersException
import com.netki.model.BeneficiaryParameters
import com.netki.model.OriginatorParameters
import com.netki.model.OwnerType
import com.netki.util.ErrorInformation.OWNERS_VALIDATION_EMPTY_ERROR
import com.netki.util.ErrorInformation.OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS
import com.netki.util.ErrorInformation.OWNERS_VALIDATION_NO_PRIMARY_OWNER
import com.netki.util.TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ExtensionsTest {

    @Test
    fun `Validate a correct list of beneficiaries required`() {
        val validListOfBeneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )

        validListOfBeneficiaries.validate(true, OwnerType.BENEFICIARY)
    }

    @Test
    fun `Validate a correct list of originators required`() {
        val validListOfOriginators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
        )

        validListOfOriginators.validate(true, OwnerType.ORIGINATOR)
    }

    @Test
    fun `Validate a incorrect list of beneficiaries required`() {
        val ownerType = OwnerType.BENEFICIARY
        val invalidListOfBeneficiaries = emptyList<BeneficiaryParameters>()

        val exception = assertThrows(InvalidOwnersException::class.java) {
            invalidListOfBeneficiaries.validate(true, ownerType)
        }

        assertEquals(exception.message, String.format(OWNERS_VALIDATION_EMPTY_ERROR, ownerType.description))
    }

    @Test
    fun `Validate a incorrect list of originators required`() {
        val ownerType = OwnerType.ORIGINATOR
        val invalidListOfOriginators = emptyList<OriginatorParameters>()

        val exception = assertThrows(InvalidOwnersException::class.java) {
            invalidListOfOriginators.validate(true, ownerType)
        }

        assertEquals(exception.message, String.format(OWNERS_VALIDATION_EMPTY_ERROR, ownerType.description))
    }

    @Test
    fun `Validate a incorrect list of beneficiaries not required`() {
        val ownerType = OwnerType.BENEFICIARY
        val invalidListOfBeneficiaries = emptyList<BeneficiaryParameters>()

        invalidListOfBeneficiaries.validate(false, ownerType)
    }

    @Test
    fun `Validate a incorrect list of originators not required`() {
        val ownerType = OwnerType.ORIGINATOR
        val invalidListOfOriginators = emptyList<OriginatorParameters>()

        invalidListOfOriginators.validate(false, ownerType)
    }

    @Test
    fun `Validate a list of beneficiaries with no primary owner`() {
        val ownerType = OwnerType.BENEFICIARY
        val invalidListOfBeneficiaries = listOf(
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val exception = assertThrows(InvalidOwnersException::class.java) {
            invalidListOfBeneficiaries.validate(true, ownerType)
        }

        assertEquals(exception.message, String.format(OWNERS_VALIDATION_NO_PRIMARY_OWNER, ownerType.description))
    }

    @Test
    fun `Validate a list of originators with no primary owner`() {
        val ownerType = OwnerType.ORIGINATOR
        val invalidListOfOriginators = listOf(
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256,
            NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val exception = assertThrows(InvalidOwnersException::class.java) {
            invalidListOfOriginators.validate(true, ownerType)
        }

        assertEquals(exception.message, String.format(OWNERS_VALIDATION_NO_PRIMARY_OWNER, ownerType.description))
    }

    @Test
    fun `Validate a list of beneficiaries with multiple primary owners`() {
        val ownerType = OwnerType.BENEFICIARY
        val invalidListOfBeneficiaries = listOf(
            PRIMARY_BENEFICIARY_PKI_X509SHA256,
            PRIMARY_BENEFICIARY_PKI_X509SHA256
        )
        val exception = assertThrows(InvalidOwnersException::class.java) {
            invalidListOfBeneficiaries.validate(true, ownerType)
        }

        assertEquals(exception.message, String.format(OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS, ownerType.description))
    }

    @Test
    fun `Validate a list of originators with multiple primary owners`() {
        val ownerType = OwnerType.ORIGINATOR
        val invalidListOfOriginators = listOf(
            PRIMARY_ORIGINATOR_PKI_X509SHA256,
            PRIMARY_ORIGINATOR_PKI_X509SHA256
        )
        val exception = assertThrows(InvalidOwnersException::class.java) {
            invalidListOfOriginators.validate(true, ownerType)
        }

        assertEquals(exception.message, String.format(OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS, ownerType.description))
    }

    @Test
    fun `Validate that strings are valid alphanumeric strings`() {
        assertTrue("Abc1234".isAlphaNumeric())
        assertTrue("Valid_string".isAlphaNumeric())
        assertTrue("Another-Valid string 1234.5".isAlphaNumeric())
        assertFalse("Not Valid #".isAlphaNumeric())
        assertFalse("#$% less valid".isAlphaNumeric())
        assertFalse("%1234".isAlphaNumeric())
    }
}
