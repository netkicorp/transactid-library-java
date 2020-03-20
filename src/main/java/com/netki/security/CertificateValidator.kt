package com.netki.security

import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.model.CertificateChain
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CERTIFICATE_CHAINS_NOT_FOUND
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_NOT_SELF_SIGNED_ERROR
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_SELF_SIGNED_ERROR
import com.netki.util.FilesUtil
import java.io.FileInputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.SignatureException
import java.security.cert.*

/**
 * Class with methods to validate things related with certificates
 */

private const val CERT_EXTENSION = ".cer"
private const val CERT_FOLDER = "certificates"

object CertificateValidator {

    /**
     * Method to validate if a chain of certificates is valid.
     *
     * @param clientCertificate certificate to validate.
     * @return true if the chain is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     */
    @Throws(
        InvalidCertificateException::class,
        InvalidCertificateChainException::class
    )
    fun validateCertificateChain(clientCertificate: X509Certificate): Boolean {
        val certificates = getCertificates()
        val certificateChains = convertToCertificateChains(certificates)
        return validateCertificateChain(clientCertificate, certificateChains)
    }

    /**
     * Method to validate if a chain of certificates is valid.
     *
     * @param clientCertificate certificate to validate.
     * @param certificateChains list of all certificate chains to use to validate the client certificate.
     * @return true if the chain is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     */
    @Throws(
        InvalidCertificateException::class
    )
    fun validateCertificateChain(
        clientCertificate: X509Certificate, certificateChains: List<CertificateChain>
    ): Boolean {
        certificateChains.forEach { certificateChain ->
            validateCertificatesInput(clientCertificate, certificateChain)
        }

        certificateChains.forEach { certificateChain ->
            if (isCertPathValid(clientCertificate, certificateChain)) {
                return true
            }
        }
        return false
    }

    private fun validateCertificatesInput(
        clientCertificate: X509Certificate,
        certificateChains: CertificateChain
    ) {
        require(!clientCertificate.isSelfSigned()) {
            throw InvalidCertificateException(
                CERTIFICATE_VALIDATION_SELF_SIGNED_ERROR.format(
                    "Client",
                    clientCertificate.issuerDN
                )
            )
        }

        certificateChains.intermediateCertificates.forEach { intermediate ->
            require(!intermediate.isSelfSigned()) {
                throw InvalidCertificateException(
                    CERTIFICATE_VALIDATION_SELF_SIGNED_ERROR.format(
                        "Intermediate",
                        intermediate.issuerDN
                    )
                )
            }
        }


        require(certificateChains.rootCertificate.isSelfSigned()) {
            throw InvalidCertificateException(
                CERTIFICATE_VALIDATION_NOT_SELF_SIGNED_ERROR.format(
                    "Root",
                    certificateChains.rootCertificate.issuerDN
                )
            )
        }
    }

    private fun isCertPathValid(
        clientCertificate: X509Certificate,
        certificateChains: CertificateChain
    ): Boolean {
        val selector = X509CertSelector()
        selector.certificate = clientCertificate

        val trustAnchors = HashSet<TrustAnchor>()
        trustAnchors.add(TrustAnchor(certificateChains.rootCertificate, null))

        val pkixParameters = PKIXBuilderParameters(trustAnchors, selector)
        pkixParameters.isRevocationEnabled = false

        certificateChains.intermediateCertificates.add(clientCertificate)
        val intermediateCertStore: CertStore =
            CertStore.getInstance(
                "Collection",
                CollectionCertStoreParameters(certificateChains.intermediateCertificates),
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

    private fun getCertificates(): List<List<X509Certificate>> {
        val certificatesList = mutableListOf<List<X509Certificate>>()
        val certificateChainFiles = try {
            FilesUtil.getFilesFromDirectory(CERT_FOLDER).filter { it.name.endsWith(CERT_EXTENSION) }
        } catch (exception: Exception) {
            throw InvalidCertificateChainException(exception.message, exception)
        }

        check(certificateChainFiles.isNotEmpty()) {
            throw InvalidCertificateChainException(CERTIFICATE_VALIDATION_CERTIFICATE_CHAINS_NOT_FOUND)
        }

        val cf = CertificateFactory.getInstance("X.509")
        certificateChainFiles.forEach { certFile ->
            certificatesList.add(cf.generateCertificates(FileInputStream(certFile)).map { it as X509Certificate })
        }

        return certificatesList
    }

    private fun convertToCertificateChains(certificates: List<List<X509Certificate>>): List<CertificateChain> {
        val certificateChains = mutableListOf<CertificateChain>()

        certificates.forEach { chain ->
            certificateChains.add(
                CertificateChain(
                    chain.first { it.isSelfSigned() },
                    chain.filter { !it.isSelfSigned() }.toMutableList()
                )
            )
        }

        return certificateChains
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
