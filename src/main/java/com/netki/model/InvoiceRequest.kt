package com.netki.model

/**
 * Representation of InvoiceRequest message.
 */
data class InvoiceRequest @JvmOverloads constructor(
    /**
     * Integer-number-of-satoshis.
     */
    val amount: Long? = 0,

    /**
     * Human-readable description of invoice request for the receiver.
     */
    val memo: String? = null,

    /**
     * Secure (usually TLS-protected HTTP) location where an EncryptedProtocolMessage SHOULD be sent when ready.
     */
    val notificationUrl: String? = null,

    /**
     * Originators account.
     */
    val originators: List<Originator> = emptyList(),

    /**
     * Beneficiaries account.
     */
    val beneficiaries: List<Beneficiary> = emptyList(),

    /**
     * Where the payment comes from.
     */
    val originatorsAddresses: List<Output> = emptyList(),

    /**
     * List of attestations requested
     */
    val attestationsRequested: List<Attestation> = emptyList(),

    /**
     * Type of sender's pki data.
     */
    val senderPkiType: PkiType? = PkiType.NONE,

    /**
     * Sender's pki data, depends on senderPkiType.
     */
    val senderPkiData: String? = null,

    /**
     * Sender's Signature of the whole message.
     */
    val senderSignature: String? = null,

    /**
     * EV Certificate in PEM format.
     */
    val senderEvCert: String? = null,

    /**
     * Recipient's vasp name
     */
    val recipientVaspName: String? = null,

    /**
     * Recipient's vasp name
     */
    val recipientChainAddress: String? = null,

    /**
     * Metadata for the protocol message.
     */
    val protocolMessageMetadata: ProtocolMessageMetadata
) : ProtocolMessage
