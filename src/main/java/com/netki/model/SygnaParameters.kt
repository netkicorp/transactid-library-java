package com.netki.model

/**
 * Data of the Sygna to be used to create a message.
 */
data class SygnaParameters @JvmOverloads constructor(
    /**
     * Specific ID for Sygna Bridge
     */
    val sygnaTransferId: String? = null,
    /**
     * Originators' encrypted account for Sygna Bridge
     *
     * @see
     * <a href="https://github.com/CoolBitX-Technology/sygna-bridge-util-j">ECIES Encrypting an Decrypting</a>
     *
     * Please encrypt your originators' account to prevent data breach
     */
    val sygnaEncryptedOriginators: String? = null,
    /**
     * Beneficiaries' encrypted account for Sygna Bridge
     *
     * @see
     * <a href="https://github.com/CoolBitX-Technology/sygna-bridge-util-j">ECIES Encrypting an Decrypting</a>
     *
     * Please encrypt your beneficiaries' account to prevent data breach
     */
    val sygnaEncryptedBeneficiaries: String? = null,
    /**
     * Specific Sygna Bridge API key for api.sygna.io/netki
     */
    val sygnaApiKey: String? = null
)
