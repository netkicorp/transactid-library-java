package com.netki.model

/**
 * Representation of Output message.
 */
data class Output @JvmOverloads constructor(

    /**
     * Number of satoshis (0.00000001 BTC) to be paid.
     */
    val amount: Long = 0,

    /**
     * A "TxOut" script where payment should be sent.
     * This will normally be one of the standard Bitcoin transaction scripts (e.g. pubkey OP_CHECKSIG).
     */
    val script: String,

    /**
     * Currency of the address.
     */
    val currency: AddressCurrency,

    /**
     * Detailed information of this address.
     * This field is only to return data fetched from the address information provider.
     * This does not need to be filled when the object is being created.
     */
    var addressInformation: AddressInformation? = null
)
