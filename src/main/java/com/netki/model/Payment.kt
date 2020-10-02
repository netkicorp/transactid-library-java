package com.netki.model

/**
 * Representation of Payment message.
 */
data class Payment(

    /**
     * Copied from PaymentDetails.merchant_data.
     * Merchants may use invoice numbers or any other data they require to match Payments to PaymentRequests.
     */
    val merchantData: String? = null,

    /**
     * One or more valid, signed Bitcoin transactions that fully pay the PaymentRequest.
     */
    val transactions: List<ByteArray> = emptyList(),

    /**
     * One or more outputs where the merchant may return funds, if necessary.
     */
    val outputs: List<Output> = emptyList(),

    /**
     * UTF-8 encoded, plain-text note from the customer to the merchant.
     */
    val memo: String? = null,

    /**
     * Account owners.
     */
    val owners: List<Owner> = emptyList(),

    /**
     * Metadata for the protocol message.
     */
    val protocolMessageMetadata: ProtocolMessageMetadata
)
