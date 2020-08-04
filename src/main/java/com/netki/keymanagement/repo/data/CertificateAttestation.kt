package com.netki.keymanagement.repo.data

import com.netki.model.Attestation

data class CertificateAttestation(
    val attestation: Attestation,
    val certificate: Certificate
)
