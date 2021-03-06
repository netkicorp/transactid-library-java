package com.netki

import com.netki.exceptions.*
import com.netki.keymanagement.config.KeyManagementFactory
import com.netki.keymanagement.main.KeyManagement
import com.netki.model.AttestationCertificate
import com.netki.model.AttestationInformation
import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * Access the key management system.
 */
class TidKms(private val keyManagement: KeyManagement) {

    companion object {
        /**
         * Method to get an instance of this class.
         * All the parameters are optional depending the functions that want to be used.
         *
         * @param authorizationCertificateProviderKey to connect to the certificate provider.
         * @param authorizationSecureStorageKey to connect to the secure storage.
         * @param addressSecureStorage to connect to the secure storage.
         * @return instance of TidKms.
         */
        @JvmStatic
        @JvmOverloads
        fun getInstance(
            authorizationCertificateProviderKey: String = "",
            authorizationSecureStorageKey: String = "",
            addressSecureStorage: String = "",
            authorizationCertificateProviderUrl: String = ""
        ): TidKms {
            val keyManagement = KeyManagementFactory.getInstance(
                authorizationCertificateProviderKey,
                authorizationSecureStorageKey,
                addressSecureStorage,
                authorizationCertificateProviderUrl
            )
            return TidKms(keyManagement)
        }
    }

    /**
     * Generate a certificate for each one of the attestations provided.
     *
     * @param attestationsInformation list of attestations with their corresponding data.
     * @return list of certificate per attestation.
     * @throws CertificateProviderException if there is an error creating the certificates.
     * @throws CertificateProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
     */
    @Throws(CertificateProviderException::class, CertificateProviderUnauthorizedException::class)
    fun generateCertificates(attestationsInformation: List<AttestationInformation>): List<AttestationCertificate> =
        keyManagement.generateCertificates(attestationsInformation)

    /**
     * Store a X509Certificate in PEM format.
     *
     * @param certificatePem certificate in PEM format to store.
     * @return id to fetch the certificate in the future.
     * @throws InvalidCertificateException if the provided certificate is not a valid X509Certificate.
     * @throws KeyManagementStoreException if there is an error while storing the certificate.
     */
    @Throws(
        InvalidCertificateException::class,
        KeyManagementStoreException::class
    )
    fun storeCertificatePem(certificatePem: String): String = keyManagement.storeCertificatePem(certificatePem)

    /**
     * Store a X509Certificate java object.
     *
     * @param certificate X509Certificate to store.
     * @return id to fetch the certificate in the future.
     * @throws InvalidCertificateException if the provided certificate is not a valid X509Certificate.
     * @throws KeyManagementStoreException if there is an error while storing the certificate.
     */
    @Throws(
        InvalidCertificateException::class,
        KeyManagementStoreException::class
    )
    fun storeCertificate(certificate: X509Certificate): String = keyManagement.storeCertificate(certificate)

    /**
     * Store a private key in PEM format.
     *
     * @param privateKeyPem private key in PEM format to store.
     * @return id to fetch the private key in the future.
     * @throws InvalidPrivateKeyException if the provided key is not a valid private key.
     * @throws KeyManagementStoreException if there is an error while storing the private key.
     */
    @Throws(
        InvalidPrivateKeyException::class,
        KeyManagementStoreException::class
    )
    fun storePrivateKeyPem(privateKeyPem: String): String = keyManagement.storePrivateKeyPem(privateKeyPem)

    /**
     * Store a PrivateKey java object.
     *
     * @param privateKey PrivateKey to store.
     * @return PrivateKey object.
     * @throws InvalidPrivateKeyException if the provided key is not a valid PrivateKey.
     * @throws KeyManagementStoreException if there is an error while storing the PrivateKey.
     */
    @Throws(
        InvalidPrivateKeyException::class,
        KeyManagementStoreException::class
    )
    fun storePrivateKey(privateKey: PrivateKey): String = keyManagement.storePrivateKey(privateKey)

    /**
     * Fetch a X509Certificate in PEM format.
     *
     * @param certificateId id of the certificate.
     * @return X509Certificate in PEM format.
     * @throws ObjectNotFoundException if there is not a certificate associated to the provided certificateId.
     * @throws InvalidCertificateException if the fetched object is not a valid X509Certificate.
     * @throws KeyManagementFetchException if there is an error while fetching the certificate.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidCertificateException::class,
        KeyManagementFetchException::class
    )
    fun fetchCertificatePem(certificateId: String): String = keyManagement.fetchCertificatePem(certificateId)

    /**
     * Fetch a X509Certificate java object.
     *
     * @param certificateId id of the certificate.
     * @return X509Certificate object.
     * @throws ObjectNotFoundException if there is not a certificate associated to the provided certificateId.
     * @throws InvalidCertificateException if the fetched object is not a valid X509Certificate.
     * @throws KeyManagementFetchException if there is an error while fetching the certificate.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidCertificateException::class,
        KeyManagementFetchException::class
    )
    fun fetchCertificate(certificateId: String): X509Certificate = keyManagement.fetchCertificate(certificateId)

    /**
     * Fetch a private key in PEM format.
     *
     * @param privateKeyId id of the private key.
     * @return private key in PEM format.
     * @throws ObjectNotFoundException if there is not a private key associated to the provided privateKeyId.
     * @throws InvalidPrivateKeyException if the fetched object is not a valid PrivateKey.
     * @throws KeyManagementFetchException if there is an error while fetching the private key.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidPrivateKeyException::class,
        KeyManagementFetchException::class
    )
    fun fetchPrivateKeyPem(privateKeyId: String): String = keyManagement.fetchPrivateKeyPem(privateKeyId)

    /**
     * Fetch a PrivateKey java object.
     *
     * @param privateKeyId id of the PrivateKey.
     * @return PrivateKey object.
     * @throws ObjectNotFoundException if there is not a PrivateKey associated to the provided privateKeyId.
     * @throws InvalidPrivateKeyException if the fetched object is not a valid PrivateKey.
     * @throws KeyManagementFetchException if there is an error while fetching the private key.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidPrivateKeyException::class,
        KeyManagementFetchException::class
    )
    fun fetchPrivateKey(privateKeyId: String): PrivateKey = keyManagement.fetchPrivateKey(privateKeyId)
}
