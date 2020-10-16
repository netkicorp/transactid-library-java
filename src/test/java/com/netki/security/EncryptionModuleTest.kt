package com.netki.security

import com.netki.exceptions.EncryptionException
import com.netki.security.CryptoModule.objectToPrivateKeyPem
import com.netki.security.CryptoModule.objectToPublicKeyPem
import com.netki.util.TestData.Keys.generateKeyPairECDSA
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EncryptionModuleTest {

    @Test
    fun `Test encryption successfully`() {
        val keyPairSender = generateKeyPairECDSA()
        val keyPairReceiver = generateKeyPairECDSA()
        val valueToEncrypt = "Encrypt string"
        val encryption = EncryptionModule.encrypt(
            valueToEncrypt,
            objectToPublicKeyPem(keyPairReceiver.public),
            objectToPublicKeyPem(keyPairSender.public),
            objectToPrivateKeyPem(keyPairSender.private)
        )
        val decrypted = EncryptionModule.decrypt(
            encryption,
            objectToPrivateKeyPem(keyPairReceiver.private),
            objectToPublicKeyPem(keyPairSender.public)
        )
        assertEquals(valueToEncrypt, decrypted)
    }

    @Test
    fun `Test encryption not same key`() {
        val keyPairSender = generateKeyPairECDSA()
        val keyPairReceiver = generateKeyPairECDSA()
        val keyPairRandom = generateKeyPairECDSA()
        val valueToEncrypt = "Encrypt string"
        val encryption = EncryptionModule.encrypt(
            valueToEncrypt,
            objectToPublicKeyPem(keyPairReceiver.public),
            objectToPublicKeyPem(keyPairSender.public),
            objectToPrivateKeyPem(keyPairSender.private)
        )
        Assertions.assertThrows(EncryptionException::class.java) {
            EncryptionModule.decrypt(
                encryption,
                objectToPrivateKeyPem(keyPairRandom.private),
                objectToPublicKeyPem(keyPairRandom.public)
            )
        }
    }
}
