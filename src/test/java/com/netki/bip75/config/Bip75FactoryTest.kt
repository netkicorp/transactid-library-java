package com.netki.bip75.config

import com.netki.bip75.main.impl.Bip75Netki
import org.junit.jupiter.api.Test
import java.io.File

internal class Bip75FactoryTest {

    @Test
    fun `Validate proper instance creation of Bip75`() {
        val trustStoreUrl = "src/test/resources/certificates"
        val certificateName = "TransactIdCA.pem"
        val bip75Instance = Bip75Factory.getInstance(trustStoreUrl)

        assert(bip75Instance is Bip75Netki)
        assert(File("$trustStoreUrl/$certificateName").exists())
    }

    @Test
    fun `Validate proper instance creation of Bip75 with Authorization key`() {
        val trustStoreUrl = "src/test/resources/certificates"
        val certificateName = "TransactIdCA.pem"
        val authorizationKey = "fake_key"
        val bip75Instance = Bip75Factory.getInstance(trustStoreUrl, authorizationKey)

        assert(bip75Instance is Bip75Netki)
        assert(File("$trustStoreUrl/$certificateName").exists())
    }

    @Test
    fun `Validate proper instance creation of Bip75 in development mode`() {
        val trustStoreUrl = "src/test/resources/certificates"
        val certificateName = "TransactIdCA.pem"
        val bip75Instance = Bip75Factory.getInstance(trustStoreLocation = trustStoreUrl, developmentMode = true)

        assert(bip75Instance is Bip75Netki)
        assert(File("$trustStoreUrl/$certificateName").exists())
    }
}
