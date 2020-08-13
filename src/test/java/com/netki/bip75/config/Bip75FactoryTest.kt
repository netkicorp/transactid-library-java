package com.netki.bip75.config

import com.netki.bip75.main.impl.Bip75Netki
import org.junit.jupiter.api.Test

internal class Bip75FactoryTest {

    @Test
    fun `Validate proper instance creation of Bip75`() {
        val bip75Instance = Bip75Factory.getInstance("random")

        assert(bip75Instance is Bip75Netki)
    }
}
