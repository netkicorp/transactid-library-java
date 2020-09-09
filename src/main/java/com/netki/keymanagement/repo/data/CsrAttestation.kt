package com.netki.keymanagement.repo.data

import com.google.gson.annotations.SerializedName
import com.netki.model.Attestation

internal data class CsrAttestation(
    @SerializedName("csr")
    val csr: String,

    @SerializedName("attestation_field")
    val attestationField: Attestation,

    @SerializedName("public_key")
    val publicKey: String
)
