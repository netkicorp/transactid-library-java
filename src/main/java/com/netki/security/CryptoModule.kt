package com.netki.security

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringReader
import java.io.StringWriter
import java.security.*
import java.security.cert.Certificate
import java.util.Base64.getDecoder
import java.util.Base64.getEncoder

/**
 * Module to execute crypto operations.
 */
object CryptoModule {

    /**
     * Algorithm to create digital signature.
     */
    private const val SIGNATURE_ALGORITHM = "SHA256withRSA"

    /**
     * Algorithm to create hash.
     */
    private const val DIGEST_ALGORITHM = "SHA-256"

    /**
     * Sign string with private key provided.
     *
     * @param stringToSign plain string to sign.
     * @param privateKeyPem in PEM format to sign.
     * @return signature.
     */
    fun signString(stringToSign: String, privateKeyPem: String) =
        signString(stringToSign, privateKeyPemToObject(privateKeyPem))

    /**
     * Sign string with private key provided.
     *
     * @param stringToSign plain string to sign.
     * @param privateKey to sign.
     * @return signature.
     */
    fun signString(stringToSign: String, privateKey: PrivateKey): String {
        val signature: ByteArray = Signature.getInstance(SIGNATURE_ALGORITHM).run {
            initSign(privateKey)
            update(stringToSign.toByteArray())
            sign()
        }
        return getEncoder().encodeToString(signature)
    }

    /**
     * Validate if a signature is valid.
     *
     * @param signature to validate.
     * @param data that was signed.
     * @param certificatePem in PEM format to validate the signature.
     * @return true if is valid, false otherwise.
     */
    fun validateSignature(signature: String, data: String, certificatePem: String) =
        validateSignature(signature, data, certificatePemToObject(certificatePem))

    /**
     * Validate if a signature is valid.
     *
     * @param signature to validate.
     * @param data that was signed.
     * @param certificate to validate the signature.
     * @return true if is valid, false otherwise.
     */
    fun validateSignature(signature: String, data: String, certificate: Certificate): Boolean {
        val signBytes = getDecoder().decode(signature.toByteArray(Charsets.UTF_8))
        return Signature.getInstance(SIGNATURE_ALGORITHM).run {
            initVerify(certificate)
            update(data.toByteArray())
            verify(signBytes)
        }
    }

    /**
     * Hash string with SHA-256 algorithm.
     *
     * @param stringToHash plain string to be hashed.
     * @return hash string.
     */
    fun getHash256(stringToHash: String) = getHash256(stringToHash.toByteArray(Charsets.UTF_8))

    /**
     * Hash string with SHA-256 algorithm.
     *
     * @param bytesToHash byteArray to be hashed.
     * @return hash string.
     */
    fun getHash256(bytesToHash: ByteArray): String {
        val messageDigest: MessageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM)
        messageDigest.update(bytesToHash)
        return bytesToHex(messageDigest.digest())
    }

    /**
     * Transform bytes to Hex String.
     *
     * @param hash bytes.
     * @return hex string.
     */
    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuffer()
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

    /**
     * Transform PrivateKey in PEM format to object.
     *
     * @param privateKeyPem string.
     * @return PrivateKey.
     */
    fun privateKeyPemToObject(privateKeyPem: String) = stringPemToObject(privateKeyPem) as PrivateKey

    /**
     * Transform PublicKey in PEM format to object.
     *
     * @param publicKeyPem string.
     * @return PublicKey.
     */
    fun publicKeyPemToObject(publicKeyPem: String) = stringPemToObject(publicKeyPem) as PublicKey

    /**
     * Transform Certificate in PEM format to Object.
     *
     * @param certificatePem string.
     * @return Certificate.
     */
    fun certificatePemToObject(certificatePem: String) = stringPemToObject(certificatePem) as Certificate

    /**
     * Transform String in PEM format to Object.
     *
     * @param stringToParse in PEM format representing one of PrivateKey / PublicKey / Certificate.
     * @return Object.
     */
    private fun stringPemToObject(stringToParse: String): Any {
        Security.addProvider(BouncyCastleProvider())

        val pemParser = PEMParser(StringReader(stringToParse))
        return when (val pemObject = pemParser.readObject()) {
            is X509CertificateHolder -> JcaX509CertificateConverter().getCertificate(pemObject)
            is PrivateKeyInfo -> JcaPEMKeyConverter().getPrivateKey(pemObject)
            is SubjectPublicKeyInfo -> JcaPEMKeyConverter().getPublicKey(pemObject)
            else -> throw IllegalArgumentException("String not supported")
        }
    }

    /**
     * Transform PrivateKey to String in PEM format.
     *
     * @param privateKey to transform.
     * @return String in PEM format.
     */
    fun objectToPrivateKeyPem(privateKey: PrivateKey) = objectToPemString(privateKey)

    /**
     * Transform PublicKey to String in PEM format.
     *
     * @param publicKey to transform.
     * @return String in PEM format.
     */
    fun objectToPublicKeyPem(publicKey: PublicKey) = objectToPemString(publicKey)

    /**
     * Transform Certificate to String in PEM format.
     *
     * @param certificate to transform.
     * @return String in PEM format.
     */
    fun objectToCertificatePem(certificate: Certificate) = objectToPemString(certificate)

    /**
     * Transform Object to String in PEM format.
     *
     * @param objectToParse one of PrivateKey / PublicKey / Certificate.
     * @return String in PEM format.
     */
    private fun objectToPemString(objectToParse: Any): String {
        val stringWriter = StringWriter()
        val pemWriter = PemWriter(stringWriter)
        when (objectToParse) {
            is PrivateKey -> pemWriter.writeObject(PemObject("PRIVATE KEY", objectToParse.encoded))
            is PublicKey -> pemWriter.writeObject(PemObject("PUBLIC KEY", objectToParse.encoded))
            is Certificate -> pemWriter.writeObject(PemObject("CERTIFICATE", objectToParse.encoded))
        }
        pemWriter.flush()
        pemWriter.close()
        return stringWriter.toString()
    }
}
