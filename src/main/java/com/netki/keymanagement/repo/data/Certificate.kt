package com.netki.keymanagement.repo.data

import com.google.gson.annotations.SerializedName
import com.netki.model.Attestation

internal data class Certificate(
    @SerializedName("attestation")
    var attestation: Attestation? = null,
    @SerializedName("certificate")
    var certificate: String? = null,
    @SerializedName("created")
    var created: String? = null,
    @SerializedName("csr_signed")
    var csrSigned: Any? = null,
    @SerializedName("csr_unsigned")
    var csrUnsigned: String? = null,
    @SerializedName("id")
    var id: Long? = null,
    @SerializedName("is_active")
    var isActive: Boolean? = null,
    @SerializedName("public_key")
    var publicKey: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("transaction")
    var transaction: String? = null,
    @SerializedName("updated")
    var updated: String? = null
)
