package com.netki.model

import java.security.cert.X509Certificate

/**
 * Object with signatures per certificate per user.
 */
class OwnerSignaturesWithCertificate : HashMap<Attestation, Pair<X509Certificate, String>>()
