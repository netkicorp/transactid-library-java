@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.netki.security

import com.netki.exceptions.InvalidCertificateChainException
import com.netki.exceptions.InvalidCertificateException
import com.netki.model.CertificateChain
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CERTIFICATE_CHAINS_NOT_FOUND
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CERTIFICATE_EXPIRED
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CERTIFICATE_NOT_YET_VALID
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_CERTIFICATE_REVOKED
import com.netki.util.ErrorInformation.CERTIFICATE_VALIDATION_NOT_CORRECT_CERTIFICATE_ERROR
import com.netki.util.FilesUtil
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.DERIA5String
import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.x509.*
import sun.security.util.ObjectIdentifier
import sun.security.x509.CertificatePoliciesExtension
import sun.security.x509.PolicyInformation
import sun.security.x509.X509CertImpl
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
private const val CA_CERT_DEV = "src/main/java/com/netki/ca/certificates/TransactIdCACertDev"
private const val CA_CERT_PROD = "src/main/java/com/netki/ca/certificates/TransactIdCACertProd"
private const val CA_CERT_NAME = "TransactIdCA.cer"

internal class CertificateValidator(
    private val trustStoreLocation: String,
    developmentMode: Boolean = false
) {
    private lateinit var policies: Array<ObjectIdentifier>

    /**
     * Initialize the CertificateValidator
     */
    init {
        val caCertificate = readCaCertificate(developmentMode)
        writeCert(caCertificate)
        policies = arrayOf(
            ObjectIdentifier("2.16.840.1.114171.500.9"),
            ObjectIdentifier("1.2.392.200091.100.721.1"),
            ObjectIdentifier("1.3.6.1.4.1.6334.1.100.1"),
            ObjectIdentifier("2.16.528.1.1001.1.1.1.12.6.1.1.1"),
            ObjectIdentifier("2.16.756.1.89.1.2.1.1"),
            ObjectIdentifier("1.3.6.1.4.1.23223.2"),
            ObjectIdentifier("2.16.840.1.113733.1.7.23.6"),
            ObjectIdentifier("1.3.6.1.4.1.14370.1.6"),
            ObjectIdentifier("2.16.840.1.113733.1.7.48.1"),
            ObjectIdentifier("2.16.840.1.114404.1.1.2.4.1"),
            ObjectIdentifier("2.16.840.1.114404.1.1.2.4.1"),
            ObjectIdentifier("2.16.840.1.114404.1.1.2.4.1"),
            ObjectIdentifier("1.3.6.1.4.1.6449.1.2.1.5.1"),
            ObjectIdentifier("1.3.6.1.4.1.6449.1.2.1.5.1"),
            ObjectIdentifier("1.3.6.1.4.1.6449.1.2.1.5.1"),
            ObjectIdentifier("1.3.6.1.4.1.6449.1.2.1.5.1"),
            ObjectIdentifier("1.3.6.1.4.1.6449.1.2.1.5.1"),
            ObjectIdentifier("2.16.840.1.114413.1.7.23.3"),
            ObjectIdentifier("2.16.840.1.114413.1.7.23.3"),
            ObjectIdentifier("2.16.840.1.114414.1.7.23.3"),
            ObjectIdentifier("2.16.840.1.114414.1.7.23.3"),
            ObjectIdentifier("2.16.840.1.114412.2.1"),
            ObjectIdentifier("1.3.6.1.4.1.8024.0.2.100.1.2"),
            ObjectIdentifier("1.3.6.1.4.1.782.1.2.1.8.1"),
            ObjectIdentifier("2.16.840.1.114028.10.1.2"),
            ObjectIdentifier("1.3.6.1.4.1.4146.1.1"),
            ObjectIdentifier("1.3.6.1.4.1.4146.1.1")
        )
    }

    private fun readCaCertificate(developmentMode: Boolean): String {
        val file = when (developmentMode) {
            true -> File(CA_CERT_DEV)
            false -> File(CA_CERT_PROD)
        }
        return file.readText()
    }

    private fun writeCert(caCertificate: String) {
        File("$trustStoreLocation/$CA_CERT_NAME").writeText(caCertificate)
    }

    /**
     * Method to validate if a certificates is valid.
     *
     * @param clientCertificatesPem certificate to validate, could be a client certificate including its own certificates chain.
     * @return true if the client certificate is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     * @exception InvalidCertificateChainException if there is a problem with the certificates chain.
     */
    @Throws(
        InvalidCertificateException::class,
        InvalidCertificateChainException::class
    )
    fun validateCertificate(clientCertificatesPem: String) =
        validateCertificateChain(clientCertificatesPem) &&
                validateCertificateExpiration(clientCertificatesPem) &&
                validateCertificateRevocation(clientCertificatesPem)

    /**
     * Method to validate if a certificates is valid.
     *
     * @param clientCertificatesPem certificate to validate.
     * @return true if the certificate is valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     */
    @Throws(InvalidCertificateException::class)
    fun validateCertificateExpiration(clientCertificatesPem: String): Boolean {
        val certificates = CryptoModule.certificatesPemToObject(clientCertificatesPem)
        val clientCertificate = CryptoModule.getClientCertificate(certificates)
        try {
            clientCertificate.checkValidity()
        } catch (exception: CertificateNotYetValidException) {
            throw InvalidCertificateException(
                String.format(
                    CERTIFICATE_VALIDATION_CERTIFICATE_NOT_YET_VALID,
                    clientCertificate.notBefore
                )
            )
        } catch (exception: CertificateExpiredException) {
            throw InvalidCertificateException(
                String.format(
                    CERTIFICATE_VALIDATION_CERTIFICATE_EXPIRED,
                    clientCertificate.notAfter
                )
            )
        }
        return true
    }

    /**
     * Method to validate if a certificates is not revoked.
     *
     * @param clientCertificatesPem certificate to validate.
     * @return true if the certificate is not revoked.
     * @exception InvalidCertificateException if the certificate is revoked.
     */
    @Throws(InvalidCertificateException::class)
    fun validateCertificateRevocation(clientCertificatesPem: String): Boolean {
        val certificates = CryptoModule.certificatesPemToObject(clientCertificatesPem)
        val clientCertificate = CryptoModule.getClientCertificate(certificates)
        val distributionPoints = getCrlDistributionPoints(clientCertificate)
        distributionPoints?.forEach { distributionPoint ->
            val crl = downloadCRL(distributionPoint!!)
            if (crl.isRevoked(clientCertificate)) {
                throw InvalidCertificateException(String.format(CERTIFICATE_VALIDATION_CERTIFICATE_REVOKED, crl))
            }
        }
        return true
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
            FilesUtil.getFilesFromDirectory(trustStoreLocation).filter { it.name.endsWith(CERT_EXTENSION) }
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

    /**
     * Extracts all CRL distribution point URLs from the
     * "CRL Distribution Point" extension in a X.509 certificate. If CRL
     * distribution point extension is unavailable, returns an empty list.
     */
    @Throws(CertificateParsingException::class, IOException::class)
    fun getCrlDistributionPoints(clientCertificate: X509Certificate): List<String?>? {
        val crlExt = clientCertificate.getExtensionValue(X509Extensions.CRLDistributionPoints.id) ?: return ArrayList()
        val asn1Stream = ASN1InputStream(ByteArrayInputStream(crlExt))
        val crlOctectString = asn1Stream.readObject() as DEROctetString
        val asn1Stream2 = ASN1InputStream(ByteArrayInputStream(crlOctectString.octets))
        val derObj2 = asn1Stream2.readObject()
        val distPoint = CRLDistPoint.getInstance(derObj2)
        val crlUrls: MutableList<String?> = ArrayList()
        for (distributionPoint in distPoint.distributionPoints) {
            val distributionPointName: DistributionPointName = distributionPoint.distributionPoint
            if (distributionPointName.type == DistributionPointName.FULL_NAME) {
                val genNames: Array<GeneralName> = GeneralNames.getInstance(distributionPointName.name).names
                for (j in genNames.indices) {
                    if (genNames[j].tagNo == GeneralName.uniformResourceIdentifier) {
                        val url = DERIA5String.getInstance(genNames[j].name).string
                        crlUrls.add(url)
                    }
                }
            }
        }
        return crlUrls
    }

    /**
     * Downloads CRL from given URL. Supports http, https, ftp and ldap based
     * URLs.
     */
    private fun downloadCRL(crlUrl: String): X509CRL {
        return if (crlUrl.startsWith("http://") || crlUrl.startsWith("https://") || crlUrl.startsWith("ftp://")) {
            downloadCrlFromWeb(crlUrl);
        } else if (crlUrl.startsWith("ldap://")) {
            downloadCrlFromLdap(crlUrl);
        } else {
            throw InvalidCertificateException("Can not download CRL from certificate distribution point: $crlUrl")
        }
    }

    /**
     * Downloads a CRL from given LDAP url, e.g.
     * ldap://ldap.infonotary.com/dc=identity-ca,dc=infonotary,dc=com
     */
    private fun downloadCrlFromLdap(ldapUrl: String): X509CRL {
        val env = Hashtable<String, String>()
        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
        env[Context.PROVIDER_URL] = ldapUrl

        val ctx = InitialDirContext(env)
        val attributes = ctx.getAttributes("")
        val attribute = attributes.get("certificateRevocationList;binary")
        val test = attribute.get() as ByteArray
        if (test.isNotEmpty()) {
            val inStream = ByteArrayInputStream(test)
            val cf = CertificateFactory.getInstance("X.509")
            return cf.generateCRL(inStream) as X509CRL
        } else {
            throw InvalidCertificateException("Can not download CRL from: $ldapUrl")
        }
    }

    /**
     * Downloads a CRL from given HTTP/HTTPS/FTP URL, e.g.
     * http://crl.infonotary.com/crl/identity-ca.crl
     */
    private fun downloadCrlFromWeb(crlUrl: String): X509CRL {
        val url = URL(crlUrl)
        val crlStream = url.openStream()
        crlStream.use { crl ->
            val certificateFactory = CertificateFactory.getInstance("X.509")
            return certificateFactory.generateCRL(crl) as X509CRL
        }
    }

    /**
     * Method to validate if a certificates is an EV cert.
     *
     * @param clientCertificatesPem certificate to validate.
     * @return true if the certificate is EV.
     */
    fun isEvCertificate(clientCertificatesPem: String): Boolean {
        val certificates = CryptoModule.certificatesPemToObject(clientCertificatesPem)
        val cert = CryptoModule.getClientCertificate(certificates)
        val ext = (cert as X509CertImpl).certificatePoliciesExtension
        ext?.let {
            val policies = it[CertificatePoliciesExtension.POLICIES]
            policies.forEach { policy ->
                if (isEVPolicy(policy)) {
                    return true
                }
            }
        }
        return false
    }


    private fun isEVPolicy(policyInformation: PolicyInformation): Boolean {
        for (oid in policies) {
            if (oid == policyInformation.policyIdentifier.identifier) {
                return true
            }
        }
        return false
    }
}

/**
 * Validate if a X509Certificate is self signed or not.
 */
internal fun X509Certificate.isSelfSigned() = try {
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
internal fun X509Certificate.isRootCertificate() =
    this.isSelfSigned() && this.keyUsage != null && this.keyUsage[5] && this.basicConstraints != -1

/**
 * Determine if a X509Certificate is intermediate certificate.
 */
internal fun X509Certificate.isIntermediateCertificate() =
    !this.isSelfSigned() && this.keyUsage != null && this.keyUsage[5] && this.basicConstraints != -1

/**
 * Determine if a X509Certificate is client certificate.
 */
internal fun X509Certificate.isClientCertificate() =
    !this.isSelfSigned() && (this.keyUsage == null || !this.keyUsage[5]) && this.basicConstraints == -1

