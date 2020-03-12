package com.netki.util

import com.netki.exceptions.InvalidOwnersException
import com.netki.util.ErrorInformation.OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS
import com.netki.util.ErrorInformation.OWNERS_VALIDATION_NO_PRIMARY_OWNER
import com.netki.util.TestData.Owners.NO_PRIMARY_OWNER_PKI_X509SHA256
import com.netki.util.TestData.Owners.PRIMARY_OWNER_PKI_X509SHA256
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ExtensionsTest {

    @Test
    fun `Validate a correct list of owners`() {
        val validListOfOwners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )

        validListOfOwners.validate()
    }

    @Test
    fun `Validate a list of owners with no primary owner`() {
        val validListOfOwners = listOf(
            NO_PRIMARY_OWNER_PKI_X509SHA256,
            NO_PRIMARY_OWNER_PKI_X509SHA256
        )
        val exception = assertThrows(InvalidOwnersException::class.java) {
            validListOfOwners.validate()
        }

        assertTrue(exception.message == OWNERS_VALIDATION_NO_PRIMARY_OWNER)
    }

    @Test
    fun `Validate a list of owners with multiple primary owners`() {
        val validListOfOwners = listOf(
            PRIMARY_OWNER_PKI_X509SHA256,
            PRIMARY_OWNER_PKI_X509SHA256
        )
        val exception = assertThrows(InvalidOwnersException::class.java) {
            validListOfOwners.validate()
        }

        assertTrue(exception.message == OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS)
    }
}
