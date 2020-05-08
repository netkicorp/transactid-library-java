package com.netki.keymanagement.main.impl

import com.netki.keymanagement.main.KeyManagement
import com.netki.keymanagement.service.KeyManagementService
import java.security.PrivateKey
import java.security.cert.X509Certificate

class KeyManagementNetki(private val keyManagementService: KeyManagementService) : KeyManagement {

    /**
     * {@inheritDoc}
     */
    override fun storeCertificatePem(certificatePem: String) = keyManagementService.storeCertificatePem(certificatePem)

    /**
     * {@inheritDoc}
     */
    override fun storeCertificate(certificate: X509Certificate) = keyManagementService.storeCertificate(certificate)

    /**
     * {@inheritDoc}
     */
    override fun storePrivateKeyPem(privateKeyPem: String) = keyManagementService.storePrivateKeyPem(privateKeyPem)

    /**
     * {@inheritDoc}
     */
    override fun storePrivateKey(privateKey: PrivateKey) = keyManagementService.storePrivateKey(privateKey)

    /**
     * {@inheritDoc}
     */
    override fun fetchCertificatePem(certificateId: String) = keyManagementService.fetchCertificatePem(certificateId)

    /**
     * {@inheritDoc}
     */
    override fun fetchCertificate(certificateId: String) = keyManagementService.fetchCertificate(certificateId)

    /**
     * {@inheritDoc}
     */
    override fun fetchPrivateKeyPem(privateKeyId: String) = keyManagementService.fetchPrivateKeyPem(privateKeyId)

    /**
     * {@inheritDoc}
     */
    override fun fetchPrivateKey(privateKeyId: String) = keyManagementService.fetchPrivateKey(privateKeyId)
}
