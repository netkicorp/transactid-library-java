package com.netki.model

/**
 * Object with signatures per attestation per user.
 */
class OwnerSignatures : HashMap<Int, MutableMap<Attestation, String>>()
