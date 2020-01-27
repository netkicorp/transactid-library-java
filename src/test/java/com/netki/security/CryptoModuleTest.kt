package com.netki.security

import com.netki.util.TestData
import com.netki.util.TestData.Hash.SHA_256_HASH_LENGTH
import com.netki.util.TestData.Keys.HASH_ALGORITHM
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CryptoModuleTest {

    private lateinit var privateKey: PrivateKey
    private lateinit var certificate: Certificate
    private lateinit var publicKey: PublicKey

    @BeforeAll
    fun setUp() {
        val keyPair = TestData.Keys.generateKeyPair()
        privateKey = keyPair.private
        certificate = TestData.Keys.generateCertificate(keyPair, HASH_ALGORITHM, "test")
        publicKey = certificate.publicKey
    }

    @Test
    fun `Sign string and validate with PrivateKeyPem`() {
        val privateKeyPem = CryptoModule.objectToPrivateKeyPem(privateKey)
        val signature = CryptoModule.signString(TestData.Signature.STRING_TEST, privateKeyPem)
        val certificatePem = CryptoModule.objectToCertificatePem(certificate)
        assert(CryptoModule.validateSignature(signature, TestData.Signature.STRING_TEST, certificatePem))
    }

    @Test
    fun `Sign string and validate with PrivateKeyObject`() {
        val signature = CryptoModule.signString(TestData.Signature.STRING_TEST, privateKey)
        assert(CryptoModule.validateSignature(signature, TestData.Signature.STRING_TEST, certificate))
    }

    @Test
    fun `Hashing bytes successfully Algorithm SHA-256`() {
        val hash = CryptoModule.getHash256(TestData.Hash.STRING_TEST.toByteArray(Charsets.UTF_8))
        assert(hash.toByteArray().size == SHA_256_HASH_LENGTH)
        assert(hash == TestData.Hash.STRING_TEST_HASH)
    }

    @Test
    fun `Hashing string successfully Algorithm SHA-256`() {
        val hash = CryptoModule.getHash256(TestData.Hash.STRING_TEST)
        assert(hash.length == SHA_256_HASH_LENGTH)
        assert(hash == TestData.Hash.STRING_TEST_HASH)
    }

    @Test
    fun `Hashing string unsuccessfully Algorithm SHA-256`() {
        val hash = CryptoModule.getHash256("random string")
        assert(hash != TestData.Hash.STRING_TEST_HASH)
    }

    @Test
    fun `Transform valid PrivateKey in PEM format to object`() {
        val privateKeyPem = CryptoModule.objectToPrivateKeyPem(privateKey)
        CryptoModule.privateKeyPemToObject(privateKeyPem)
    }

    @Test
    fun `Transform invalid PrivateKey in PEM format to object`() {
        Assertions.assertThrows(Exception::class.java) {
            CryptoModule.privateKeyPemToObject("invalid_private_key")
        }
    }

    @Test
    fun `Transform valid PrivateKey object to PEM format`() {
        CryptoModule.objectToPrivateKeyPem(privateKey)
    }

    @Test
    fun `Transform valid Certificate in PEM format to object`() {
        val certificatePem = CryptoModule.objectToCertificatePem(certificate)
        CryptoModule.certificatePemToObject(certificatePem)
    }

    @Test
    fun `Transform invalid Certificate in PEM format to object`() {
        Assertions.assertThrows(Exception::class.java) {
            CryptoModule.certificatePemToObject("invalid_certificate")
        }
    }

    @Test
    fun `Transform valid Certificate object to PEM format`() {
        CryptoModule.objectToCertificatePem(certificate)
    }

    @Test
    fun `Transform valid PublicKey in PEM format to object`() {
        val publicKeyPem = CryptoModule.objectToPublicKeyPem(publicKey)
        CryptoModule.publicKeyPemToObject(publicKeyPem)
    }

    @Test
    fun `Transform invalid PublicKey in PEM format to object`() {
        Assertions.assertThrows(Exception::class.java) {
            CryptoModule.publicKeyPemToObject("invalid_public_key")
        }
    }

    @Test
    fun `Transform valid PublicKey object to PEM format`() {
        CryptoModule.objectToPublicKeyPem(publicKey)
    }
}
