package com.netki.model

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Data to create InvoiceRequest message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaymentParameters @JvmOverloads constructor(
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
     * List of originators for this transaction.
     */
    val originatorParameters: List<OriginatorParameters>,

    /**
     * List of beneficiaries for this transaction.
     */
    val beneficiaryParameters: List<BeneficiaryParameters>? = emptyList(),

    /**
     * The sender of the protocol message.
     */
    val senderParameters: SenderParameters? = null,

    /**
     * Information of the recipient of the message.
     */
    val recipientParameters: RecipientParameters? = null,

    /**
     * Status and information of the protocol message status, by default "OK".
     */
    val messageInformation: MessageInformation = MessageInformation()
) : ProtocolMessageParameters
