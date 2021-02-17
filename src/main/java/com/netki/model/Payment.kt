package com.netki.model

/**
 * Representation of Payment message.
 */
data class Payment @JvmOverloads constructor(

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
     * Originators account.
     */
    val originators: List<Originator> = emptyList(),

    /**
     * Beneficiaries account.
     */
    val beneficiaries: List<Beneficiary> = emptyList(),

    /**
     * Metadata for the protocol message.
     */
    val protocolMessageMetadata: ProtocolMessageMetadata? = null
) : ProtocolMessage
