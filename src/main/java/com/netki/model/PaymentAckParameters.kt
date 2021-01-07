package com.netki.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaymentAckParameters @JvmOverloads constructor(

    /**
     * Data to create the Payment.
     */
    val payment: Payment,

    /**
     * Note that should be displayed to the customer.
     */
    val memo: String? = "",

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
