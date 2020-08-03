package com.netki.security

import com.netki.exceptions.InvalidCertificateException
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.PEMWriter
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.*
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
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
     * Generate a keypair.
     *
     * @return key pair generated.
     */
    fun generateKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        return kpg.generateKeyPair()
    }

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
     * Extract client certificate from Certificates in PEM format.
     *
     * @param certificatesPem string.
     * @return Client certificate.
     */
    fun certificatePemToClientCertificate(certificatesPem: String): X509Certificate {
        val certificates = certificatesPemToObject(certificatesPem)
        return getClientCertificate(certificates)
    }

    /**
     * Convert certificates in PEM format to Object.
     *
     * @param certificatesPem string.
     * @return List of certificates.
     */
    @Suppress("UNCHECKED_CAST")
    fun certificatesPemToObject(certificatesPem: String): List<X509Certificate> {
        val cf = CertificateFactory.getInstance("X.509")
        return cf.generateCertificates(ByteArrayInputStream(certificatesPem.toByteArray(Charsets.UTF_8))) as List<X509Certificate>
    }

    /**
     * Extract client certificate from a list of certificates.
     *
     * @param certificates including the client certificate.
     * @return Client certificate.
     * @throws InvalidCertificateException if the client certificate is not found
     */
    fun getClientCertificate(certificates: List<X509Certificate>) = try {
        certificates.first { it.isClientCertificate() }
    } catch (exception: NoSuchElementException) {
        throw InvalidCertificateException(CERTIFICATE_VALIDATION_CLIENT_CERTIFICATE_NOT_FOUND)
    }

    /**
     * Extract intermediate certificates from a list of certificates.
     *
     * @param certificates including the intermediate certificates.
     * @return list of intermediate certificates.
     */
    fun getIntermediateCertificates(certificates: List<X509Certificate>) =
        certificates.filter { it.isIntermediateCertificate() }

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

    /**
     * Generate a signed CSR for the provided principal.
     *
     * @param principal with the string for the CN in the CSR.
     * @param keyPair to sign the CSR.
     * @return the CSR generated.
     */
    fun generateCSR(principal: String, keyPair: KeyPair): PKCS10CertificationRequest {
        val signer = JCESigner(keyPair.private, SIGNATURE_ALGORITHM)

        val csrBuilder = JcaPKCS10CertificationRequestBuilder(
            X500Name(principal), keyPair.public
        )
        val extensionsGenerator = ExtensionsGenerator()
        extensionsGenerator.addExtension(
            Extension.basicConstraints, true, BasicConstraints(
                false
            )
        )
        csrBuilder.addAttribute(
            PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
            extensionsGenerator.generate()
        )

        return csrBuilder.build(signer)
    }

    /**
     * Method to create signature configuration for CSR.
     */
    private class JCESigner(privateKey: PrivateKey, signatureAlgorithm: String) : ContentSigner {
        private val algorithm: String = signatureAlgorithm.toLowerCase()
        private var signature: Signature? = null
        private var outputStream: ByteArrayOutputStream? = null

        init {
            try {
                this.outputStream = ByteArrayOutputStream()
                this.signature = Signature.getInstance(signatureAlgorithm)
                this.signature!!.initSign(privateKey)
            } catch (gse: GeneralSecurityException) {
                throw IllegalArgumentException(gse.message)
            }
        }

        override fun getAlgorithmIdentifier(): AlgorithmIdentifier {
            return ALGORITHMS[algorithm] ?: throw IllegalArgumentException("Does not support algorithm: $algorithm")
        }

        override fun getOutputStream(): OutputStream? {
            return outputStream
        }

        override fun getSignature(): ByteArray? {
            return try {
                signature!!.update(outputStream!!.toByteArray())
                signature!!.sign()
            } catch (gse: GeneralSecurityException) {
                gse.printStackTrace()
                null
            }
        }

        companion object {

            private val ALGORITHMS = HashMap<String, AlgorithmIdentifier>()

            init {
                ALGORITHMS["SHA256withRSA".toLowerCase()] = AlgorithmIdentifier(
                    ASN1ObjectIdentifier("1.2.840.113549.1.1.11")
                )
                ALGORITHMS["SHA1withRSA".toLowerCase()] = AlgorithmIdentifier(
                    ASN1ObjectIdentifier("1.2.840.113549.1.1.5")
                )
            }
        }
    }

    /**
     * Transform a CSR object to a PEM string format.
     *
     * @param csr to transform.
     * @return string in PEM format.
     */
    fun csrObjectToPem(csr: PKCS10CertificationRequest): String {
        val pemObject = PemObject("CERTIFICATE REQUEST", csr.encoded)
        val str = StringWriter()
        val pemWriter = PEMWriter(str)
        pemWriter.writeObject(pemObject)
        pemWriter.close()
        str.close()
        return str.toString()
    }
}
