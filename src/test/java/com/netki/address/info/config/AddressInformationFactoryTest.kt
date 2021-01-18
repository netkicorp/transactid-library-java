package com.netki.address.info.config

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class AddressInformationFactoryTest {

    @Test
    fun `Validate proper instance creation of AddressInformation`() {
        val addressInformationInstance = AddressInformationFactory.getInstance("test_key")

        assertNotNull(addressInformationInstance)
    }
}
