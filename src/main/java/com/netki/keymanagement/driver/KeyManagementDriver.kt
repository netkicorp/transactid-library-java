package com.netki.keymanagement.driver

import com.netki.exceptions.KeyManagementStoreException

internal interface KeyManagementDriver {

    /**
     * Store a X509Certificate.
     *
     * @param certificateId unique id for the certificate.
     * @param certificatePem certificate in Pem format to store.
     * @throws KeyManagementStoreException if there is an error while storing in the driver
     */
    @Throws(KeyManagementStoreException::class)
    fun storeCertificatePem(certificateId: String, certificatePem: String)

    /**
     * Store a Private key.
     *
     * @param privateKeyId unique id for the private key.
     * @param privateKeyPem private key in Pem format to store.
     * @throws KeyManagementStoreException if there is an error while storing in the driver
     */
    @Throws(KeyManagementStoreException::class)
    fun storePrivateKeyPem(privateKeyId: String, privateKeyPem: String)

    /**
     * Fetch a certificate.
     *
     * @param certificateId id of the certificate.
     * @return certificate in PEM format or null if not found.
     */
    fun fetchCertificatePem(certificateId: String): String?

    /**
     * Fetch a private key.
     *
     * @param privateKeyId id of the private key.
     * @return private key in PEM format or null if not found.
     */
    fun fetchPrivateKeyPem(privateKeyId: String): String?
}
