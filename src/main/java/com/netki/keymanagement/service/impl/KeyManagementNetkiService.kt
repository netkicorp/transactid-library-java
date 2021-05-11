package com.netki.keymanagement.service.impl

import com.netki.exceptions.*
import com.netki.exceptions.ExceptionInformation.CERTIFICATE_INFORMATION_STRING_NOT_CORRECT_ERROR_PROVIDER
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE_NOT_FOUND
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY_NOT_FOUND
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_ERROR_STORING_CERTIFICATE
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_ERROR_STORING_PRIVATE_KEY
import com.netki.exceptions.ExceptionInformation.KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION
import com.netki.extensions.isAlphaNumeric
import com.netki.keygeneration.main.KeyGeneration
import com.netki.keygeneration.util.toPrincipal
import com.netki.keymanagement.driver.KeyManagementDriver
import com.netki.keymanagement.service.KeyManagementService
import com.netki.model.AttestationCertificate
import com.netki.model.AttestationInformation
import com.netki.security.*
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.*

internal class KeyManagementNetkiService(
    private val keyGeneration: KeyGeneration,
    private val driver: KeyManagementDriver
) : KeyManagementService {

    /**
     * {@inheritDoc}
     */
    override fun generateCertificates(attestationsInformation: List<AttestationInformation>): List<AttestationCertificate> {
        validateAttestationData(attestationsInformation)

        val keyPair = Keys.generateKeyPair()

        val attestationsCertificate = attestationsInformation.map {
            AttestationCertificate(
                attestation = it.attestation,
                csr = Certificate.csrObjectToPem(
                    Certificate.generateCSR(it.attestation.toPrincipal(it.data, it.ivmsConstraint), keyPair)
                ),
                publicKeyPem = keyPair.public.toPemFormat()
            )
        }

        val certificates = keyGeneration.generateCertificates(attestationsCertificate)

        return if (certificates.count() == 0) {
            emptyList()
        } else {
            certificates.map {
                AttestationCertificate(
                    it.attestation,
                    it.certificatePem!!,
                    keyPair.private.toPemFormat()
                )
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun storeCertificatePem(certificatePem: String): String {
        try {
            certificatePem.toCertificate()
        } catch (exception: Exception) {
            throw InvalidCertificateException(KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION.format(exception.message))
        }
        return storeCertificateOnDriver(certificatePem)
    }

    /**
     * {@inheritDoc}
     */
    override fun storeCertificate(certificate: X509Certificate): String {
        val certificatePem = try {
            certificate.toPemFormat()
        } catch (exception: Exception) {
            throw InvalidCertificateException(KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION.format(exception.message))
        }
        return storeCertificateOnDriver(certificatePem)
    }

    private fun storeCertificateOnDriver(certificatePem: String): String {
        val id = generateUniqueId()
        return try {
            driver.storeCertificatePem(id, certificatePem)
            id
        } catch (exception: Exception) {
            throw KeyManagementStoreException(KEY_MANAGEMENT_ERROR_STORING_CERTIFICATE.format(exception.message))
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun storePrivateKeyPem(privateKeyPem: String): String {
        try {
            privateKeyPem.toPrivateKey()
        } catch (exception: Exception) {
            throw InvalidPrivateKeyException(KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION.format(exception.message))
        }
        return storePrivateKeyOnDriver(privateKeyPem)
    }

    /**
     * {@inheritDoc}
     */
    override fun storePrivateKey(privateKey: PrivateKey): String {
        val privateKeyPem = try {
            privateKey.toPemFormat()
        } catch (exception: Exception) {
            throw InvalidPrivateKeyException(KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION.format(exception.message))
        }
        return storePrivateKeyOnDriver(privateKeyPem)
    }

    private fun storePrivateKeyOnDriver(privateKeyPem: String): String {
        val id = generateUniqueId()
        return try {
            driver.storePrivateKeyPem(id, privateKeyPem)
            id
        } catch (exception: Exception) {
            throw KeyManagementStoreException(KEY_MANAGEMENT_ERROR_STORING_PRIVATE_KEY.format(exception.message))
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun fetchCertificatePem(certificateId: String): String {
        val certificatePem = try {
            driver.fetchCertificatePem(certificateId)
        } catch (exception: Exception) {
            throw KeyManagementFetchException(KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE.format(exception.message))
        }

        return certificatePem?.let {
            try {
                it.toCertificate()
                it
            } catch (exception: Exception) {
                throw InvalidCertificateException(KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION.format(exception.message))
            }
        } ?: throw ObjectNotFoundException(KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE_NOT_FOUND.format(certificateId))
    }

    /**
     * {@inheritDoc}
     */
    override fun fetchCertificate(certificateId: String): X509Certificate {
        val certificatePem = try {
            driver.fetchCertificatePem(certificateId)
        } catch (exception: Exception) {
            throw KeyManagementFetchException(KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE.format(exception.message))
        }
        return certificatePem?.let {
            try {
                it.toCertificate() as X509Certificate
            } catch (exception: Exception) {
                throw InvalidCertificateException(KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION.format(exception.message))
            }
        } ?: throw ObjectNotFoundException(KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE_NOT_FOUND.format(certificateId))
    }

    /**
     * {@inheritDoc}
     */
    override fun fetchPrivateKeyPem(privateKeyId: String): String {
        val privateKeyPem = try {
            driver.fetchPrivateKeyPem(privateKeyId)
        } catch (exception: Exception) {
            throw KeyManagementFetchException(KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY.format(exception.message))
        }

        return privateKeyPem?.let {
            try {
                it.toPrivateKey()
                it
            } catch (exception: Exception) {
                throw InvalidPrivateKeyException(KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION.format(exception.message))
            }
        } ?: throw ObjectNotFoundException(KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY_NOT_FOUND.format(privateKeyId))
    }

    /**
     * {@inheritDoc}
     */
    override fun fetchPrivateKey(privateKeyId: String): PrivateKey {
        val privateKeyPem = try {
            driver.fetchPrivateKeyPem(privateKeyId)
        } catch (exception: Exception) {
            throw KeyManagementFetchException(KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY.format(exception.message))
        }
        return privateKeyPem?.let {
            try {
                it.toPrivateKey()
            } catch (exception: Exception) {
                throw InvalidPrivateKeyException(KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION.format(exception.message))
            }
        } ?: throw ObjectNotFoundException(KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY_NOT_FOUND.format(privateKeyId))
    }

    private fun generateUniqueId() = UUID.randomUUID().toString()

    private fun validateAttestationData(attestationsInformation: List<AttestationInformation>) {
        attestationsInformation.forEach { information ->
            if (!information.data.isAlphaNumeric()) {
                throw CertificateProviderException(
                    String.format(
                        CERTIFICATE_INFORMATION_STRING_NOT_CORRECT_ERROR_PROVIDER,
                        information.data,
                        information.attestation
                    )
                )
            }
        }
    }
}
