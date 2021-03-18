@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.netki.security

import com.netki.ca.certificates.CaCertificates.TRANSACT_ID_DEV
import com.netki.ca.certificates.CaCertificates.TRANSACT_ID_PROD
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_VALIDATION_CERTIFICATE_CHAINS_NOT_FOUND
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR
import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.model.CertificateChain
import com.netki.util.FilesUtil
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.DERIA5String
import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.x509.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URL
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.SignatureException
import java.security.cert.*
import java.util.*
import javax.naming.Context
import javax.naming.directory.InitialDirContext
import kotlin.collections.HashSet

/**
 * Class with methods to validate things related with certificates.
 */
private const val CERT_EXTENSION = ".cer"
private const val PEM_EXTENSION = ".pem"
private const val CA_CERT_NAME = "TransactIdCA.pem"

internal class CertificateValidator(
    private val trustStoreLocation: String,
    developmentMode: Boolean = false
) {
    /**
     * Initialize the CertificateValidator
     */
    init {
        val caCertificate = readCaCertificate(developmentMode)
        writeCert(caCertificate)
    }

    private fun readCaCertificate(developmentMode: Boolean): String {
        return when (developmentMode) {
            true -> TRANSACT_ID_DEV
            false -> TRANSACT_ID_PROD
        }
    }

    private fun writeCert(caCertificate: String) {
        File("$trustStoreLocation/$CA_CERT_NAME").writeText(caCertificate)
    }

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
     * @param clientCertificatesPem chain of certificates to validate.
     * @param certificateChains list of all certificate chains to use to validate the client certificate.
     * @return true if the chain is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     */
    @Throws(InvalidCertificateException::class)
    fun validateCertificateChain(
        clientCertificatesPem: String,
        certificateChains: List<CertificateChain>
    ): Boolean {

        val certificates = clientCertificatesPem.toCertificates()
        val clientCertificate = certificates.getClientCertificate()
        val intermediateCertificates = certificates.getIntermediateCertificates()

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
        selector.certificate = certificates.getClientCertificate()

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
            FilesUtil.getFilesFromDirectory(trustStoreLocation).filter {
                it.name.endsWith(CERT_EXTENSION) || it.name.endsWith(PEM_EXTENSION)
            }
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
