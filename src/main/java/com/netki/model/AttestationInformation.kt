package com.netki.model

/**
 * Represents some data associated to an specific type of attestation.
 */
data class AttestationInformation(

    /**
     * The type of attestation.
     */
    val attestation: Attestation,

    /**
     * Data associated to the attestation.
     */
    val data: String
)
