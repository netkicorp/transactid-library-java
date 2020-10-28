package com.netki.model

/**
 * Contains a certificate associated to an specific attestation.
 */
data class AttestationCertificate constructor(

    /**
     * The type of attestation.
     */
    val attestation: Attestation,

    /**
     * Certificate associated to the attestation.
     * The certificate is a X509 certificate in PEM format.
     */
    val certificatePem: String,

    /**
     * Private Key associated to the certificate.
     * The private key is a RSA-2048 in PEM format.
     */
    val privateKeyPem: String
)
