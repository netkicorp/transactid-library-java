package com.netki.keymanagement.service

import com.netki.exceptions.*
import java.security.PrivateKey
import java.security.cert.X509Certificate

interface KeyManagementService {

    /**
     * Store a X509Certificate.
     *
     * @param certificatePem certificate in Pem format to store.
     * @return id to fetch the certificate in the future.
     * @throws InvalidCertificateException if the provided certificate is not a valid X509 certificate.
     * @throws KeyManagementStoreException if there is an error while storing the certificate.
     */
    @Throws(
        InvalidCertificateException::class,
        KeyManagementStoreException::class
    )
    fun storeCertificatePem(certificatePem: String): String

    /**
     * Store a X509Certificate.
     *
     * @param certificate X509Certificate to store.
     * @return id to fetch the certificate in the future.
     * @throws InvalidCertificateException if the provided certificate is not a valid X509 certificate.
     * @throws KeyManagementStoreException if there is an error while storing the certificate.
     */
    @Throws(
        InvalidCertificateException::class,
        KeyManagementStoreException::class
    )
    fun storeCertificate(certificate: X509Certificate): String

    /**
     * Store a Private key.
     *
     * @param privateKeyPem private key in Pem format to store.
     * @return id to fetch the private key in the future.
     * @throws InvalidPrivateKeyException if the provided key is not a valid private key.
     * @throws KeyManagementStoreException if there is an error while storing the private key.
     */
    @Throws(
        InvalidPrivateKeyException::class,
        KeyManagementStoreException::class
    )
    fun storePrivateKeyPem(privateKeyPem: String): String

    /**
     * Store a Private key.
     *
     * @param privateKey private key in Pem format to store.
     * @return PrivateKey object.
     * @throws InvalidPrivateKeyException if the provided key is not a valid private key.
     * @throws KeyManagementStoreException if there is an error while storing the private key.
     */
    @Throws(
        InvalidPrivateKeyException::class,
        KeyManagementStoreException::class
    )
    fun storePrivateKey(privateKey: PrivateKey): String

    /**
     * Fetch a certificate.
     *
     * @param certificateId id of the certificate.
     * @return certificate in PEM format.
     * @throws ObjectNotFoundException if there is not a certificate associated to the provided certificateId.
     * @throws InvalidCertificateException if the fetched object is not a valid X509 certificate.
     * @throws KeyManagementFetchException if there is an error while fetching the certificate.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidCertificateException::class,
        KeyManagementFetchException::class
    )
    fun fetchCertificatePem(certificateId: String): String

    /**
     * Fetch a certificate.
     *
     * @param certificateId id of the certificate.
     * @return X509Certificate object.
     * @throws ObjectNotFoundException if there is not a certificate associated to the provided certificateId.
     * @throws InvalidCertificateException if the fetched object is not a valid X509 certificate.
     * @throws KeyManagementFetchException if there is an error while fetching the certificate.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidCertificateException::class,
        KeyManagementFetchException::class
    )
    fun fetchCertificate(certificateId: String): X509Certificate

    /**
     * Fetch a private key.
     *
     * @param privateKeyId id of the private key.
     * @return private key in PEM format.
     * @throws ObjectNotFoundException if there is not a private key associated to the provided privateKeyId.
     * @throws InvalidPrivateKeyException if the fetched object is not a valid private key.
     * @throws KeyManagementFetchException if there is an error while fetching the private key.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidPrivateKeyException::class,
        KeyManagementFetchException::class
    )
    fun fetchPrivateKeyPem(privateKeyId: String): String

    /**
     * Fetch a private key.
     *
     * @param privateKeyId id of the private key.
     * @return PrivateKey object.
     * @throws ObjectNotFoundException if there is not a private key associated to the provided privateKeyId.
     * @throws InvalidPrivateKeyException if the fetched object is not a valid private key.
     * @throws KeyManagementFetchException if there is an error while fetching the private key.
     */
    @Throws(
        ObjectNotFoundException::class,
        InvalidPrivateKeyException::class,
        KeyManagementFetchException::class
    )
    fun fetchPrivateKey(privateKeyId: String): PrivateKey
}
