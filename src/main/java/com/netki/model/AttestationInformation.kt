package com.netki.model

/**
 * Represents some data associated to an specific type of attestation.
 */
data class AttestationInformation constructor(

    /**
     * The type of attestation.
     */
    val attestation: Attestation,

    /**
     * The type of IVMS constraints
     */
    val ivmsConstraint: IvmsConstraint,

    /**
     * Data associated to the attestation.
     */
    val data: String
)
