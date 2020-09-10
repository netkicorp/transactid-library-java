package com.netki.keymanagement.service.impl

import com.netki.exceptions.*
import com.netki.keymanagement.driver.KeyManagementDriver
import com.netki.keymanagement.repo.CertificateProvider
import com.netki.keymanagement.repo.data.CsrAttestation
import com.netki.keymanagement.service.KeyManagementService
import com.netki.keymanagement.util.toPrincipal
import com.netki.model.AttestationCertificate
import com.netki.model.AttestationInformation
import com.netki.security.CryptoModule
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_CERTIFICATE_INVALID_EXCEPTION
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_ERROR_FETCHING_CERTIFICATE_NOT_FOUND
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY_NOT_FOUND
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_ERROR_STORING_CERTIFICATE
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_ERROR_STORING_PRIVATE_KEY
import com.netki.util.ErrorInformation.KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.*

internal class KeyManagementNetkiService(
    private val certificateProvider: CertificateProvider,
    private val driver: KeyManagementDriver
) : KeyManagementService {

    /**
     * {@inheritDoc}
     */
    override fun generateCertificates(attestationsInformation: List<AttestationInformation>): List<AttestationCertificate> {
        val transactionId = certificateProvider.requestTransactionId(attestationsInformation.map { it.attestation })

        val keyPair = CryptoModule.generateKeyPair()

        val csrsAttestations = attestationsInformation.map {
            CsrAttestation(
                CryptoModule.csrObjectToPem(
                    CryptoModule.generateCSR(it.attestation.toPrincipal(it.data, it.ivmsConstraints), keyPair)
                ),
                it.attestation,
                CryptoModule.objectToPublicKeyPem(keyPair.public)
            )
        }

        certificateProvider.submitCsrsAttestations(transactionId, csrsAttestations)
        val certificates = certificateProvider.getCertificates(transactionId)

        return if (certificates.count == 0) {
            emptyList()
        } else {
            certificates.certificates.map {
                AttestationCertificate(
                    it.attestation!!,
                    it.certificate!!,
                    CryptoModule.objectToPrivateKeyPem(keyPair.private)
                )
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun storeCertificatePem(certificatePem: String): String {
        try {
            CryptoModule.certificatePemToObject(certificatePem)
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
            CryptoModule.objectToCertificatePem(certificate)
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
            CryptoModule.privateKeyPemToObject(privateKeyPem)
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
            CryptoModule.objectToPrivateKeyPem(privateKey)
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
                CryptoModule.certificatePemToObject(it)
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
                CryptoModule.certificatePemToObject(it) as X509Certificate
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
                CryptoModule.privateKeyPemToObject(it)
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
                CryptoModule.privateKeyPemToObject(it)
            } catch (exception: Exception) {
                throw InvalidPrivateKeyException(KEY_MANAGEMENT_PRIVATE_KEY_INVALID_EXCEPTION.format(exception.message))
            }
        } ?: throw ObjectNotFoundException(KEY_MANAGEMENT_ERROR_FETCHING_PRIVATE_KEY_NOT_FOUND.format(privateKeyId))
    }


    private fun generateUniqueId() = UUID.randomUUID().toString()
}
