package com.netki.keymanagement.repo.data

internal data class CertificateAttestationResponse(
    val count: Int,
    val certificates: List<Certificate>
)
