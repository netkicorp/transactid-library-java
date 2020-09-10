package com.netki.keymanagement.repo.data

import com.google.gson.annotations.SerializedName
import com.netki.model.Attestation

internal data class AttestationField(
    @SerializedName("attestation_field")
    val attestationField: Attestation
)
