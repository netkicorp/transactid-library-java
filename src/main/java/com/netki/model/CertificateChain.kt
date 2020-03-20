package com.netki.model

import java.security.cert.X509Certificate

/**
 * Representation of a certificate chain in format X509
 */
data class CertificateChain(
    /**
     * Root certificate corresponding to a CA
     */
    val rootCertificate: X509Certificate,

    /**
     * List with all the certificates in the chain before the client certificate
     */
    val intermediateCertificates: MutableList<X509Certificate>
)
