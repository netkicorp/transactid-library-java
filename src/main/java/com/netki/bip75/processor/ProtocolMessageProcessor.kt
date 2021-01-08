package com.netki.bip75.processor

import com.netki.exceptions.*
import com.netki.model.ProtocolMessage
import com.netki.model.ProtocolMessageParameters
import com.netki.model.RecipientParameters

interface ProtocolMessageProcessor {

    /**
     * Create a protocol message.
     *
     * @param protocolMessageParameters data to create the protocol message.
     * @return binary object of the protocol message created.
     * @throws InvalidOwnersException if the provided list of owners is not valid.
     * @throws EncryptionException if there is an error while creating the encrypted message.
     */
    @Throws(InvalidOwnersException::class, EncryptionException::class)
    fun create(protocolMessageParameters: ProtocolMessageParameters): ByteArray

    /**
     * Validate if a binary message is valid.
     *
     * @param protocolMessageBinary binary data to validate.
     * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
     * @return true if is valid.
     * @exception InvalidObjectException if the binary is malformed.
     * @exception InvalidSignatureException if the signature in the binary is not valid.
     * @exception InvalidCertificateException if there is a problem with the certificates.
     * @exception InvalidCertificateChainException if the certificate chain is not valid.
     * @exception EncryptionException if there is an error decrypting or validating the encryption.
     */
    @Throws(
        InvalidObjectException::class,
        InvalidSignatureException::class,
        InvalidCertificateException::class,
        InvalidCertificateChainException::class,
        EncryptionException::class
    )
    fun isValid(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters? = null): Boolean

    /**
     * Parse binary ProtocolMessage.
     *
     * @param protocolMessageBinary binary data with the message to parse.
     * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
     * @return ProtocolMessage parsed.
     * @exception InvalidObjectException if the binary is malformed.
     * @exception EncryptionException if there is an error decrypting or validating the encryption.
     */
    @Throws(InvalidObjectException::class, EncryptionException::class)
    fun parse(protocolMessageBinary: ByteArray, recipientParameters: RecipientParameters? = null): ProtocolMessage

    /**
     * Parse binary ProtocolMessage and also get the detailed information of the addresses included in it.
     *
     * @param protocolMessageBinary binary data with the message to parse.
     * @param recipientParameters information of the recipient of the message, the RecipientParameters.EncryptionParameters is mandatory to handle encrypted messages.
     * @return ProtocolMessage parsed with the detailed information for each address.
     * @exception InvalidObjectException if the binary is malformed.
     * @exception AddressProviderErrorException if there is an error fetching the information from the provider.
     * @exception AddressProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
     * @exception EncryptionException if there is an error decrypting or validating the encryption.
     */
    @Throws(
        InvalidObjectException::class,
        AddressProviderErrorException::class,
        AddressProviderUnauthorizedException::class,
        EncryptionException::class
    )
    fun parseWithAddressesInfo(
        protocolMessageBinary: ByteArray,
        recipientParameters: RecipientParameters? = null
    ): ProtocolMessage
}
