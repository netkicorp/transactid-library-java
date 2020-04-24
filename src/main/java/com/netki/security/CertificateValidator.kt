package com.netki.security

import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.model.CertificateChain
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CERTIFICATE_CHAINS_NOT_FOUND
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR
import com.netki.util.FilesUtil
import java.io.FileInputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.SignatureException
import java.security.cert.*

/**
 * Class with methods to validate things related with certificates.
 */
private const val CERT_EXTENSION = ".cer"
private const val CERT_FOLDER = "certificates"

object CertificateValidator {

    /**
     * Method to validate if a chain of certificates is valid.
     *
     * @param clientCertificatesPem certificate to validate.
     * @return true if the chain is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     */
    @Throws(
        InvalidCertificateException::class,
        InvalidCertificateChainException::class
    )
    fun validateCertificateChain(clientCertificatesPem: String): Boolean {
        val certificates = getCertificates()
        val certificateChains = convertToCertificateChains(certificates)
        return validateCertificateChain(clientCertificatesPem, certificateChains)
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
    fun validateCertificateChain(clientCertificatesPem: String, certificateChains: List<CertificateChain>): Boolean {

        val certificates = CryptoModule.certificatesPemToObject(clientCertificatesPem)
        val clientCertificate = CryptoModule.getClientCertificate(certificates)
        val intermediateCertificates = CryptoModule.getIntermediateCertificates(certificates)

        validateCertificatesInput(clientCertificate, intermediateCertificates, certificateChains)

        certificateChains.forEach { certificateChain ->
            if (isCertPathValid(certificates, certificateChain)) {
                return true
            }
        }
        return false
    }

    private fun validateCertificatesInput(
        clientCertificate: X509Certificate,
        intermediateCertificates: List<X509Certificate>,
        certificateChains: List<CertificateChain>
    ) {
        require(clientCertificate.isClientCertificate()) {
            throw InvalidCertificateException(
                CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR.format(
                    clientCertificate,
                    "Client"
                )
            )
        }

        intermediateCertificates.forEach { intermediate ->
            require(intermediate.isIntermediateCertificate()) {
                throw InvalidCertificateException(
                    CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR.format(
                        intermediate.issuerDN,
                        "Intermediate"
                    )
                )
            }
        }

        certificateChains.forEach { certificateChain ->
            certificateChain.intermediateCertificates.forEach { intermediate ->
                require(intermediate.isIntermediateCertificate()) {
                    throw InvalidCertificateException(
                        CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR.format(
                            intermediate.issuerDN,
                            "Intermediate"
                        )
                    )
                }
            }
            require(certificateChain.rootCertificate.isRootCertificate()) {
                throw InvalidCertificateException(
                    CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR.format(
                        certificateChain.rootCertificate.issuerDN,
                        "Root"
                    )
                )
            }
        }
    }

    private fun isCertPathValid(
        certificates: List<X509Certificate>,
        certificateChains: CertificateChain
    ): Boolean {
        val selector = X509CertSelector()
        selector.certificate = CryptoModule.getClientCertificate(certificates)

        val trustAnchors = HashSet<TrustAnchor>()
        trustAnchors.add(TrustAnchor(certificateChains.rootCertificate, null))

        val pkixParameters = PKIXBuilderParameters(trustAnchors, selector)
        pkixParameters.isRevocationEnabled = false

        certificateChains.intermediateCertificates.addAll(certificates)
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

/**
 * Determine if a X509Certificate is root certificate.
 */
fun X509Certificate.isRootCertificate() =
    this.isSelfSigned() && this.keyUsage != null && this.keyUsage[5] && this.basicConstraints != -1

/**
 * Determine if a X509Certificate is intermediate certificate.
 */
fun X509Certificate.isIntermediateCertificate() =
    !this.isSelfSigned() && this.keyUsage != null && this.keyUsage[5] && this.basicConstraints != -1

/**
 * Determine if a X509Certificate is client certificate.
 */
fun X509Certificate.isClientCertificate() =
    !this.isSelfSigned() && (this.keyUsage == null || !this.keyUsage[5]) && this.basicConstraints == -1
