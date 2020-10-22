package com.netki.security

import com.netki.exceptions.EncryptionException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val UPPER_HEX_DIGITS =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
private const val LENGTH_PAD = 64

internal object EncryptionModule {

    @Throws(EncryptionException::class)
    fun encrypt(
        msg: String,
        publicKeyReceiverPem: String,
        publicKeySenderPem: String,
        privateKeySenderPem: String
    ): String {
        try {
            val publicKeyReceiver = CryptoModule.publicKeyPemToObject(publicKeyReceiverPem) as ECPublicKey
            val publicKeySender = CryptoModule.publicKeyPemToObject(publicKeySenderPem) as ECPublicKey
            val privateKeySender = CryptoModule.privateKeyPemToObject(privateKeySenderPem)

            val publicKey = "04" +
                    leftPadWithZeroes(publicKeySender.w.affineX.toString(16)) +
                    leftPadWithZeroes(publicKeySender.w.affineY.toString(16))

            val kf = KeyFactory.getInstance("EC")
            val ka = KeyAgreement.getInstance("ECDH")
            ka.init(kf.generatePrivate(PKCS8EncodedKeySpec(privateKeySender.encoded)))
            ka.doPhase(publicKeyReceiver, true)

            val sharedSecret = ka.generateSecret()
            val hash = MessageDigest.getInstance("SHA-512")
            hash.update(sharedSecret)

            val derivedKey = hash.digest()
            val encryptKey = ByteArray(derivedKey.size / 2)
            val macKey = ByteArray(derivedKey.size / 2)
            System.arraycopy(derivedKey, 0, encryptKey, 0, encryptKey.size)
            System.arraycopy(derivedKey, encryptKey.size, macKey, 0, macKey.size)

            val plaintext = msg.toByteArray(StandardCharsets.UTF_8)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val iv = ByteBuffer.allocate(16).putInt(0).array()
            val ivSpec = IvParameterSpec(iv)
            val encryptSpec = SecretKeySpec(encryptKey, "AES")

            cipher.init(Cipher.ENCRYPT_MODE, encryptSpec, ivSpec)

            val cipherText = cipher.doFinal(plaintext)
            val secretKey: SecretKey = SecretKeySpec(macKey, "HmacSHA1")
            val mac = Mac.getInstance(secretKey.algorithm)
            mac.init(secretKey)
            mac.update(iv)
            mac.update(decode(publicKey))
            mac.update(cipherText)

            val macResult = mac.doFinal()
            return publicKey + encode(macResult) + encode(cipherText)
        } catch (exception: Exception) {
            println(exception)
            throw EncryptionException(exception.message, exception)
        }
    }

    @Throws(EncryptionException::class)
    fun decrypt(encryptedMsg: String, privateKeyReceiverPem: String, publicKeySenderPem: String): String {
        try {
            val privateKeyReceiver = CryptoModule.privateKeyPemToObject(privateKeyReceiverPem)
            val publicKeySender = CryptoModule.publicKeyPemToObject(publicKeySenderPem) as ECPublicKey

            val parameters = AlgorithmParameters.getInstance("EC")
            parameters.init(ECGenParameterSpec("secp256k1"))

            val iv = ByteBuffer.allocate(16).putInt(0).array()
            val ivSpec = IvParameterSpec(iv)

            val ephemX = encryptedMsg.substring(2, 66)
            val ephemY = encryptedMsg.substring(66, 130)
            val macResult = encryptedMsg.substring(130, 130 + 40).toUpperCase()
            val cipherText = encryptedMsg.substring(130 + 40, encryptedMsg.length)

            val ka = KeyAgreement.getInstance("ECDH")
            ka.init(privateKeyReceiver)
            ka.doPhase(publicKeySender, true)

            val sharedSecret = ka.generateSecret()
            val hash = MessageDigest.getInstance("SHA-512")
            hash.update(sharedSecret)

            val derivedKey = hash.digest()
            val decryptKey = ByteArray(derivedKey.size / 2)
            val macKey = ByteArray(derivedKey.size / 2)
            System.arraycopy(derivedKey, 0, decryptKey, 0, decryptKey.size)
            System.arraycopy(derivedKey, decryptKey.size, macKey, 0, macKey.size)

            val secretKey: SecretKey = SecretKeySpec(macKey, "HmacSHA1")
            val mac = Mac.getInstance(secretKey.algorithm)
            mac.init(secretKey)
            mac.update(iv)

            val ephemPublicKey = StringBuilder("04")
            ephemPublicKey.append(ephemX)
            ephemPublicKey.append(ephemY)
            mac.update(decode(ephemPublicKey.toString()))
            mac.update(decode(cipherText))

            val bigMac = encode(mac.doFinal()).toUpperCase()
            if (macResult != bigMac) {
                throw Exception("Mac invalid")
            }

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val decryptSpec = SecretKeySpec(decryptKey, "AES")
            cipher.init(Cipher.DECRYPT_MODE, decryptSpec, ivSpec)

            val plaintext = cipher.doFinal(decode(cipherText))
            return String(plaintext, StandardCharsets.UTF_8)
        } catch (exception: Exception) {
            println(exception)
            throw EncryptionException(exception.message, exception)
        }
    }

    private fun leftPadWithZeroes(originalString: String): String {
        var paddedString = originalString
        while (paddedString.length < LENGTH_PAD) {
            paddedString = "0$paddedString"
        }
        return paddedString
    }

    private fun decode(hex: String): ByteArray {
        require(hex.length % 2 == 0) {
            throw EncryptionException("A hex string must contain an even number of characters: $hex")
        }
        val out = ByteArray(hex.length / 2)
        var i = 0
        while (i < hex.length) {
            val high = Character.digit(hex[i], 16)
            val low = Character.digit(hex[i + 1], 16)
            require(!(high == -1 || low == -1)) {
                throw EncryptionException("A hex string can only contain the characters 0-9, A-F, a-f: $hex")
            }
            out[i / 2] = (high * 16 + low).toByte()
            i += 2
        }
        return out
    }

    private fun encode(bytes: ByteArray): String {
        val stringBuilder = StringBuilder(bytes.size * 2)
        for (cur in bytes) {
            stringBuilder.append(UPPER_HEX_DIGITS[cur.toInt() shr 4 and 0xF])
            stringBuilder.append(UPPER_HEX_DIGITS[cur.toInt() and 0xF])
        }
        return stringBuilder.toString()
    }
}
