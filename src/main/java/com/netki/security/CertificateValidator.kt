package com.netki.security

import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.exceptions.InvalidKeystoreException
import com.netki.util.FilesUtil
import java.io.FileInputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.SignatureException
import java.security.cert.*

/**
 * Class with methods to validate things related with certificates
 */
object CertificateValidator {

    /**
     * Method to validate if a chain of certificates is valid.
     *
     * @param clientCertificate certificate to validate.
     * @return true if the chain is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     * @exception InvalidKeystoreException if there is a problem with the Keystore containing the certificate chain.
     */
    @Throws(
        InvalidKeystoreException::class,
        InvalidCertificateException::class,
        InvalidCertificateChainException::class
    )
    fun validateCertificateChain(clientCertificate: X509Certificate): Boolean {
        val keystore = getKeyStore()
        val certificates = getCertificates(keystore)
        val rootCertificates = certificates.filter { it.isSelfSigned() }
        val intermediateCertificates = certificates.filter { !it.isSelfSigned() }
        return validateCertificateChain(clientCertificate, intermediateCertificates, rootCertificates)
    }

    /**
     * Method to validate if a chain of certificates is valid.
     *
     * @param clientCertificate certificate to validate.
     * @param intermediateCertificates list of all intermediates certificates to validate in the chain.
     * @param rootCertificates list of all root certificates to validate in the chain.
     * @return true if the chain is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     */
    @Throws(
        InvalidCertificateException::class,
        InvalidCertificateChainException::class
    )
    fun validateCertificateChain(
        clientCertificate: X509Certificate,
        intermediateCertificates: List<X509Certificate>,
        rootCertificates: List<X509Certificate>
    ): Boolean {
        validateCertificatesInput(clientCertificate, intermediateCertificates, rootCertificates)

        return isCertPathValid(clientCertificate, intermediateCertificates, rootCertificates)
    }

    private fun validateCertificatesInput(
        clientCertificate: X509Certificate,
        intermediateCertificates: List<X509Certificate>,
        rootCertificates: List<X509Certificate>
    ) {
        require(!clientCertificate.isSelfSigned()) {
            throw InvalidCertificateException("Client certificate: ${clientCertificate.issuerDN} is self signed")
        }

        for (intermediate in intermediateCertificates) {
            require(!intermediate.isSelfSigned()) {
                throw InvalidCertificateException("Intermediate certificate: ${intermediate.issuerDN} is self signed")
            }
        }

        for (root in rootCertificates) {
            require(root.isSelfSigned()) {
                throw InvalidCertificateException("Root certificate: ${root.issuerDN} is not self signed")
            }
        }
    }

    private fun isCertPathValid(
        clientCertificate: X509Certificate,
        intermediateCertificates: List<X509Certificate>,
        rootCertificates: List<X509Certificate>
    ): Boolean {
        val selector = X509CertSelector()
        selector.certificate = clientCertificate

        val trustAnchors = HashSet<TrustAnchor>()
        for (root in rootCertificates) {
            trustAnchors.add(TrustAnchor(root, null))
        }

        val pkixParameters = PKIXBuilderParameters(trustAnchors, selector)
        pkixParameters.isRevocationEnabled = false

        val certificatesList = intermediateCertificates.toMutableList()
        certificatesList.add(clientCertificate)
        val intermediateCertStore: CertStore =
            CertStore.getInstance(
                "Collection",
                CollectionCertStoreParameters(certificatesList),
                "BC"
            )
        pkixParameters.addCertStore(intermediateCertStore)

        val builder = CertPathBuilder.getInstance("PKIX", "BC")

        return try {
            builder.build(pkixParameters) as PKIXCertPathBuilderResult
            true
        } catch (pathBuildException: CertPathBuilderException) {
            false
        } catch (invalidAlgorithmException: InvalidAlgorithmParameterException) {
            false
        }
    }

    private fun getKeyStore(): KeyStore {
        val keyStoreFiles = try {
            FilesUtil.getFilesFromDirectory("keystore").filter { it.name.endsWith("jks") }
        } catch (exception: Exception) {
            throw InvalidKeystoreException(exception.message, exception)
        }

        check(keyStoreFiles.isNotEmpty()) {
            throw InvalidKeystoreException("Keystore not found, make sure to put one in /src/main/resources/keystore folder")
        }

        check(keyStoreFiles.size == 1) {
            throw InvalidKeystoreException("More than one keystore found, make sure you have only one")
        }

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(FileInputStream(keyStoreFiles[0]), null)
        return keyStore
    }

    private fun getCertificates(keyStore: KeyStore): List<X509Certificate> {
        val certificates = mutableListOf<X509Certificate>()
        val aliases = keyStore.aliases()
        while (aliases.hasMoreElements()) {
            val alias = aliases.nextElement()
            val certificate = keyStore.getCertificate(alias)
            check(certificate is X509Certificate) {
                throw InvalidKeystoreException("Looks like the keystore contains more than the chain of certificates")
            }
            certificates.add(keyStore.getCertificate(alias) as X509Certificate)
        }
        return certificates
    }
}

/**
 * Validate if a X509Certificate is self signed or not.
 */
fun X509Certificate.isSelfSigned() = try {
    val key = this.publicKey
    this.verify(key)
    true
} catch (ex: SignatureException) {
    false
} catch (ex: InvalidKeyException) {
    false
}
