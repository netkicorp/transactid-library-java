package com.netki.util

import com.google.protobuf.ByteString
import com.google.protobuf.GeneratedMessageV3
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidOwnersException
import com.netki.model.*
import com.netki.security.CertificateValidator
import com.netki.security.CryptoModule
import com.netki.util.ErrorInformation.PARSE_BINARY_MESSAGE_INVALID_INPUT
import java.sql.Timestamp

/**
 * Transform InvoiceRequestParameters to Messages.InvoiceRequest.Builder.
 *
 * @param senderParameters the sender of the message.
 * @return Messages.InvoiceRequest.Builder.
 */
fun InvoiceRequestParameters.toMessageInvoiceRequestBuilderUnsigned(
    senderParameters: SenderParameters
): Messages.InvoiceRequest.Builder = Messages.InvoiceRequest.newBuilder()
    .setAmount(this.amount)
    .setMemo(this.memo)
    .setNotificationUrl(this.notificationUrl)
    .setSenderPkiType(senderParameters.pkiDataParameters.type.value)
    .setSenderPkiData(senderParameters.pkiDataParameters.certificatePem.toByteString())
    .setSenderSignature("".toByteString())

/**
 * Transform Messages.InvoiceRequest to InvoiceRequest object.
 *
 * @return InvoiceRequest.
 */
fun Messages.InvoiceRequest.toInvoiceRequest(): InvoiceRequest {
    val owners = mutableListOf<Owner>()
    this.ownersList.forEach { messageOwner ->
        owners.add(messageOwner.toOwner())
    }
    return InvoiceRequest(
        amount = this.amount,
        memo = this.memo,
        notificationUrl = this.notificationUrl,
        owners = owners,
        senderPkiType = this.senderPkiType.getType(),
        senderPkiData = this.senderPkiData.toStringLocal(),
        senderSignature = this.senderSignature.toStringLocal()
    )
}

/**
 * Transform binary InvoiceRequest to Messages.InvoiceRequest.
 *
 * @return Messages.InvoiceRequest
 * @throws InvalidObjectException if there is an error parsing the object.
 */
fun ByteArray.toMessageInvoiceRequest(): Messages.InvoiceRequest = try {
    Messages.InvoiceRequest.parseFrom(this)
} catch (exception: Exception) {
    exception.printStackTrace()
    throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format("invoiceRequest", exception.message))
}

/**
 * Transform Messages.PaymentRequest to PaymentRequest object.
 *
 * @return PaymentRequest.
 */
fun Messages.PaymentRequest.toPaymentRequest(): PaymentRequest {
    val paymentDetails = this.serializedPaymentDetails.toMessagePaymentDetails()

    val owners = mutableListOf<Owner>()
    this.ownersList.forEach { messageOwner ->
        owners.add(messageOwner.toOwner())
    }
    val outputs = mutableListOf<Output>()
    paymentDetails.outputsList.forEach { messageOutput ->
        outputs.add(messageOutput.toOutput())
    }

    return PaymentRequest(
        paymentDetailsVersion = this.paymentDetailsVersion,
        paymentParameters = PaymentParameters(
            network = paymentDetails.network,
            outputs = outputs,
            time = Timestamp(paymentDetails.time),
            expires = Timestamp(paymentDetails.expires),
            memo = paymentDetails.memo,
            paymentUrl = paymentDetails.paymentUrl,
            merchantData = paymentDetails.merchantData.toStringLocal()
        ),
        owners = owners,
        senderPkiType = this.senderPkiType.getType(),
        senderPkiData = this.senderPkiData.toStringLocal(),
        senderSignature = this.senderSignature.toStringLocal()
    )
}

/**
 * Transform Messages.PaymentDetails to Messages.PaymentRequest.Builder.
 *
 * @param senderParameters the sender of the message.
 * @param paymentParametersVersion
 * @return Messages.PaymentRequest.Builder.
 */
fun Messages.PaymentDetails.toPaymentRequest(
    senderParameters: SenderParameters,
    paymentParametersVersion: Int
): Messages.PaymentRequest.Builder = Messages.PaymentRequest.newBuilder()
    .setPaymentDetailsVersion(paymentParametersVersion)
    .setSerializedPaymentDetails(this.toByteString())
    .setSenderPkiType(senderParameters.pkiDataParameters.type.value)
    .setSenderPkiData(senderParameters.pkiDataParameters.certificatePem.toByteString())
    .setSenderSignature("".toByteString())

/**
 * Transform PaymentParameters object to Messages.PaymentDetails object.
 *
 * @return Messages.PaymentDetails.
 */
fun PaymentParameters.toMessagePaymentDetails(): Messages.PaymentDetails {
    val messagePaymentDetailsBuilder = Messages.PaymentDetails.newBuilder()
        .setNetwork(this.network)
        .setTime(this.time.time)
        .setExpires(this.expires?.time ?: 0)
        .setMemo(this.memo)
        .setPaymentUrl(this.paymentUrl)
        .setMerchantData(this.merchantData?.toByteString())

    this.outputs.forEach { output ->
        messagePaymentDetailsBuilder.addOutputs(output.toMessageOutput())
    }

    return messagePaymentDetailsBuilder.build()
}

/**
 * Transform binary PaymentRequest to Messages.PaymentRequest.
 *
 * @return Messages.PaymentRequest
 * @throws InvalidObjectException if there is an error parsing the object.
 */
fun ByteArray.toMessagePaymentRequest(): Messages.PaymentRequest = try {
    Messages.PaymentRequest.parseFrom(this)
} catch (exception: Exception) {
    exception.printStackTrace()
    throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format("paymentRequest", exception.message))
}

/**
 * Transform binary PaymentDetails to Messages.PaymentDetails.
 *
 * @return Messages.PaymentDetails
 * @throws InvalidObjectException if there is an error parsing the object.
 */
fun ByteString.toMessagePaymentDetails(): Messages.PaymentDetails = try {
    Messages.PaymentDetails.parseFrom(this)
} catch (exception: Exception) {
    exception.printStackTrace()
    throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format("paymentDetails", exception.message))
}

/**
 * Transform Payment object to Messages.Payment object.
 *
 * @return Messages.Payment.
 */
fun Payment.toMessagePayment(): Messages.Payment {
    val messagePaymentBuilder = Messages.Payment.newBuilder()
        .setMerchantData(this.merchantData?.toByteString())
        .setMemo(this.memo)

    this.transactions.forEach { transaction ->
        messagePaymentBuilder.addTransactions(transaction.toByteString())
    }

    this.outputs.forEach { output ->
        messagePaymentBuilder.addRefundTo(output.toMessageOutput())
    }

    return messagePaymentBuilder.build()
}

/**
 * Transform Messages.Payment to Payment object.
 *
 * @return Payment.
 */
fun Messages.Payment.toPayment(): Payment {
    val transactionList = mutableListOf<ByteArray>()
    for (transaction in this.transactionsList) {
        transactionList.add(transaction.toByteArray())
    }

    val outputs = mutableListOf<Output>()
    for (protoOutput in this.refundToList) {
        outputs.add(protoOutput.toOutput())
    }
    return Payment(
        merchantData = this.merchantData.toStringLocal(),
        transactions = transactionList,
        outputs = outputs,
        memo = this.memo
    )
}

/**
 * Transform binary Payment to Messages.Payment.
 *
 * @return Messages.Payment
 * @throws InvalidObjectException if there is an error parsing the object.
 */
fun ByteArray.toMessagePayment(): Messages.Payment = try {
    Messages.Payment.parseFrom(this)
} catch (exception: Exception) {
    exception.printStackTrace()
    throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format("payment", exception.message))
}

/**
 * Transform Messages.PaymentACK to PaymentAck object.
 *
 * @return PaymentAck.
 */
fun Messages.PaymentACK.toPaymentAck(): PaymentAck = PaymentAck(this.payment.toPayment(), this.memo)


/**
 * Transform binary PaymentACK to Messages.PaymentACK.
 *
 * @return Messages.PaymentACK
 * @throws InvalidObjectException if there is an error parsing the object.
 */
fun ByteArray.toMessagePaymentAck(): Messages.PaymentACK = try {
    Messages.PaymentACK.parseFrom(this)
} catch (exception: Exception) {
    exception.printStackTrace()
    throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format("paymentAck", exception.message))
}

/**
 * Transform Payment object to Messages.PaymentACK object.
 *
 * @return Messages.PaymentACK.
 */
fun Payment.toMessagePaymentAck(): Messages.PaymentACK = Messages.PaymentACK.newBuilder()
    .setPayment(this.toMessagePayment())
    .setMemo(memo)
    .build()

/**
 * Transform Messages.Output object to Output object.
 *
 * @return Output.
 */
fun Messages.Output.toOutput(): Output = Output(this.amount, this.script.toStringLocal())

/**
 * Transform Output object to Messages.Output object.
 *
 * @return Messages.Output.
 */
fun Output.toMessageOutput(): Messages.Output = Messages.Output.newBuilder()
    .setAmount(this.amount)
    .setScript(this.script.toByteString())
    .build()

/**
 * Transform OwnerParameters object to Messages.Owner object.
 *
 * @return Messages.Owner.
 */
fun OwnerParameters.toMessageOwner(): Messages.Owner {
    val messageOwnerBuilder = Messages.Owner.newBuilder()
        .setPrimaryForTransaction(this.isPrimaryForTransaction)

    this.pkiDataParametersSets.forEachIndexed { index, pkiData ->
        val messageSignature = Messages.Signature.newBuilder()
            .setAttestation(pkiData.attestation)
            .setPkiType(pkiData.type.value)
            .setPkiData(pkiData.certificatePem.toByteString())
            .setSignature("".toByteString())
            .build()

        messageOwnerBuilder.addSignatures(index, messageSignature)
    }

    return messageOwnerBuilder.build()
}

/**
 * Transform OwnerParameters object to Messages.Owner.Builder object.
 *
 * @return Messages.Owner.
 */
fun OwnerParameters.toMessageOwnerBuilderWithoutSignatures(): Messages.Owner.Builder =
    Messages.Owner.newBuilder().setPrimaryForTransaction(this.isPrimaryForTransaction)

/**
 * Transform PkiDataParameters object to Messages.Signature object.
 * If there is a PkiType X509SHA256 this message should be signed.
 *
 * @return Messages.Signature.
 */
fun PkiDataParameters.toMessageSignature(requireSignature: Boolean): Messages.Signature {
    val messageSignatureUnsigned = Messages.Signature.newBuilder()
        .setAttestation(this.attestation)
        .setPkiType(this.type.value)
        .setPkiData(this.certificatePem.toByteString())
        .setSignature("".toByteString())
        .build()

    return when {
        this.type == PkiType.X509SHA256 && requireSignature -> {
            val signature = messageSignatureUnsigned.sign(this.privateKeyPem)
            Messages.Signature.newBuilder()
                .mergeFrom(messageSignatureUnsigned)
                .setSignature(signature.toByteString())
                .build()
        }
        else -> messageSignatureUnsigned
    }
}

/**
 * Validate if the signature of a Messages.Signature is valid.
 *
 * @return true if yes, false otherwise.
 */
fun Messages.Signature.validateMessageSignature(requireSignature: Boolean): Boolean = when {
    this.getMessagePkiType() == PkiType.X509SHA256 && requireSignature -> {
        val unsignedMessage = this.removeSignature()
        val bytesHash = CryptoModule.getHash256(unsignedMessage.toByteArray())
        CryptoModule.validateSignature(
            this.signature.toStringLocal(),
            bytesHash,
            CryptoModule.certificatePemToClientCertificate(this.pkiData.toStringLocal())
        )
    }
    else -> true
}


/**
 * Remove the signature from Messages.Signature object.
 *
 * @return Messages.Signature.
 */
fun Messages.Signature.removeSignature(): Messages.Signature = Messages.Signature.newBuilder()
    .mergeFrom(this)
    .setSignature("".toByteString())
    .build()

/**
 * Transform Messages.Owner to Owner object.
 *
 * @return Owner.
 */
fun Messages.Owner.toOwner(): Owner {
    val pkiDataSets = mutableListOf<PkiData>()
    this.signaturesList.forEach { messageSignature ->
        pkiDataSets.add(messageSignature.toPkiData())
    }
    return Owner(this.primaryForTransaction, pkiDataSets)
}

/**
 * Transform Messages.Signature to PkiData object.
 *
 * @return PkiData.
 */
fun Messages.Signature.toPkiData(): PkiData = PkiData(
    attestation = this.attestation,
    certificatePem = this.pkiData.toStringLocal(),
    type = this.pkiType.getType(),
    signature = this.signature.toStringLocal()
)

/**
 * Sign a GeneratedMessageV3 with the sender information.
 *
 * @return GeneratedMessageV3 signed.
 */
@Throws(IllegalArgumentException::class)
fun GeneratedMessageV3.signMessage(senderParameters: SenderParameters): GeneratedMessageV3 {
    return when (val senderPkiType = this.getMessagePkiType()) {
        PkiType.NONE -> this
        PkiType.X509SHA256 -> when (this) {
            is Messages.InvoiceRequest -> this.signWithSender(senderParameters)
            is Messages.PaymentRequest -> this.signWithSender(senderParameters)
            else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to sign message")
        }
        else -> throw IllegalArgumentException("PkiType: $senderPkiType, not supported")
    }
}

/**
 * Sign a Messages.InvoiceRequest.
 *
 * @return Messages.InvoiceRequest signed.
 */
fun Messages.InvoiceRequest.signWithSender(senderParameters: SenderParameters): Messages.InvoiceRequest {
    val signature = this.sign(senderParameters.pkiDataParameters.privateKeyPem)

    return Messages.InvoiceRequest.newBuilder()
        .mergeFrom(this)
        .setSenderSignature(signature.toByteString())
        .build()
}

/**
 * Sign a Messages.PaymentRequest.
 *
 * @return Messages.PaymentRequest signed.
 */
fun Messages.PaymentRequest.signWithSender(senderParameters: SenderParameters): Messages.PaymentRequest {
    val signature = this.sign(senderParameters.pkiDataParameters.privateKeyPem)

    return Messages.PaymentRequest.newBuilder()
        .mergeFrom(this)
        .setSenderSignature(signature.toByteString())
        .build()
}

/**
 * Sign a GeneratedMessageV3 with all the attestations in a List of Owners.
 *
 * @return Map with signatures per user and attestations.
 */
fun List<OwnerParameters>.signMessage(message: GeneratedMessageV3): OwnerSignatures {
    val ownersSignatures = OwnerSignatures()
    this.forEachIndexed { index, ownerParameters ->
        val signatures = mutableMapOf<String, String>()
        for (pkiData in ownerParameters.pkiDataParametersSets) {
            if (pkiData.attestation != null && pkiData.type != PkiType.NONE) {
                signatures[pkiData.attestation] = message.sign(pkiData.privateKeyPem)
            }
        }
        ownersSignatures[index] = signatures
    }
    return ownersSignatures
}

/**
 * Sign the Hash256 value of a Messages object.
 *
 * @return Signature.
 */
fun GeneratedMessageV3.sign(privateKeyPem: String): String {
    val hash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.signString(hash, privateKeyPem)
}

/**
 * Validate if sender signature of a GeneratedMessageV3 is valid.
 *
 * @return true if yes, false otherwise.
 */
fun GeneratedMessageV3.validateMessageSignature(signature: String): Boolean {
    return when (val senderPkiType = this.getMessagePkiType()) {
        PkiType.NONE -> true
        PkiType.X509SHA256 -> when (this) {
            is Messages.InvoiceRequest -> this.validateSignature(signature)
            is Messages.PaymentRequest -> this.validateSignature(signature)
            else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to validate sender signature")
        }
        else -> throw IllegalArgumentException("PkiType: $senderPkiType, not supported")
    }
}

/**
 * Validate that a signature corresponds to a Messages.InvoiceRequest.
 *
 * @return  true if yes, false otherwise.
 */
fun Messages.InvoiceRequest.validateSignature(signature: String): Boolean {
    val bytesHash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.validateSignature(signature, bytesHash, this.senderPkiData.toStringLocal())
}

/**
 * Validate that a signature corresponds to a Messages.PaymentRequest.
 *
 * @return  true if yes, false otherwise.
 */
fun Messages.PaymentRequest.validateSignature(signature: String): Boolean {
    val bytesHash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.validateSignature(signature, bytesHash, this.senderPkiData.toStringLocal())
}

/**
 * Remove sender signature of a GeneratedMessageV3
 *
 * @return Unsigned message.
 */
fun GeneratedMessageV3.removeMessageSenderSignature(): GeneratedMessageV3 {
    return when (val senderPkiType = this.getMessagePkiType()) {
        PkiType.NONE -> this
        PkiType.X509SHA256 -> when (this) {
            is Messages.InvoiceRequest -> this.removeSenderSignature()
            is Messages.PaymentRequest -> this.removeSenderSignature()
            else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to remove sender signature")
        }
        else -> throw IllegalArgumentException("PkiType: $senderPkiType, not supported")
    }
}

/**
 * Remove sender signature of a Messages.InvoiceRequest
 *
 * @return Unsigned message.
 */
fun Messages.InvoiceRequest.removeSenderSignature(): Messages.InvoiceRequest = Messages.InvoiceRequest.newBuilder()
    .mergeFrom(this)
    .setSenderSignature("".toByteString())
    .build()

/**
 * Remove sender signature of a Messages.PaymentRequest.
 *
 * @return Unsigned message.
 */
fun Messages.PaymentRequest.removeSenderSignature(): Messages.PaymentRequest = Messages.PaymentRequest.newBuilder()
    .mergeFrom(this)
    .setSenderSignature("".toByteString())
    .build()

/**
 * Get all the signatures In a list of Owners including the certificate.
 *
 * @return OwnerSignaturesWithCertificate.
 */
fun List<Messages.Owner>.getSignatures(): List<OwnerSignaturesWithCertificate> {
    val listOwnerSignaturesWithCertificate = mutableListOf<OwnerSignaturesWithCertificate>()
    this.forEach { owner ->
        val ownerSignaturesWithCertificate = OwnerSignaturesWithCertificate()
        owner.signaturesList.forEach { signature ->
            when (signature.pkiType) {
                PkiType.NONE.value -> {
                    // nothing to do here
                }
                else -> {
                    ownerSignaturesWithCertificate[signature.attestation] = Pair(
                        CryptoModule.certificatePemToClientCertificate(signature.pkiData.toStringLocal()),
                        signature.signature.toStringLocal()
                    )
                }
            }
        }
        listOwnerSignaturesWithCertificate.add(ownerSignaturesWithCertificate)
    }
    return listOwnerSignaturesWithCertificate
}

/**
 * Remove all the signatures In a list of Owners including the certificate.
 *
 * @return MutableList<Messages.Owner> without signatures.
 */
fun List<Messages.Owner>.removeOwnersSignatures(): MutableList<Messages.Owner> {
    val ownersWithoutSignature = mutableListOf<Messages.Owner>()
    this.forEachIndexed { index, owner ->
        val ownerWithoutSignaturesBuilder = Messages.Owner.newBuilder()
            .mergeFrom(owner)
        owner.signaturesList.forEachIndexed() { signatureIndex, signature ->
            ownerWithoutSignaturesBuilder.removeSignatures(signatureIndex)
            ownerWithoutSignaturesBuilder.addSignatures(
                signatureIndex, Messages.Signature.newBuilder()
                    .mergeFrom(signature)
                    .setSignature("".toByteString())
                    .build()
            )
        }
        ownersWithoutSignature.add(index, ownerWithoutSignaturesBuilder.build())
    }
    return ownersWithoutSignature
}

/**
 * Validate if a certificate belongs to a valid Certificate chain.
 *
 * @return true if yes, false otherwise.
 */
fun String.validateCertificateChain(pkiType: PkiType): Boolean {
    return when (pkiType) {
        PkiType.NONE -> true
        PkiType.X509SHA256 -> {
            CertificateValidator.validateCertificateChain(this)
        }
    }
}

/**
 * Validate that a List<Owners> is valid
 * Is valid, when it has one single primaryOwner
 *
 * @throws InvalidOwnersException if is not a valid list
 */
fun List<OwnerParameters>.validate() {
    val numberOfPrimaryOwners = this.filter { it.isPrimaryForTransaction }.size

    check(numberOfPrimaryOwners != 0) {
        throw InvalidOwnersException(ErrorInformation.OWNERS_VALIDATION_NO_PRIMARY_OWNER)
    }

    check(numberOfPrimaryOwners <= 1) {
        throw InvalidOwnersException(ErrorInformation.OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS)
    }
}

/**
 * Get sender's pkiData of a GeneratedMessageV3.
 *
 * @return PkiData.
 */
@Throws(IllegalArgumentException::class)
fun GeneratedMessageV3.getMessagePkiType(): PkiType = when (this) {
    is Messages.InvoiceRequest -> this.senderPkiType.getType()
    is Messages.PaymentRequest -> this.senderPkiType.getType()
    is Messages.Signature -> this.pkiType.getType()
    else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to get Sender PkiType")
}

/**
 * Transform an string to its correspondent PkiType
 *
 * @return PkiType
 */
fun String.getType(): PkiType = requireNotNull(PkiType.values().find {
    it.value == this
}) {
    "No PkiType found for: ${this.javaClass}"
}

/**
 * Get owners's pkiData of a signature.
 *
 * @return PkiData.
 */
@Throws(IllegalArgumentException::class)
fun Messages.Signature.getSignaturePkiType(): PkiType = requireNotNull(PkiType.values().find {
    it.value == this.pkiType
}) {
    "No PkiType found for: ${this.javaClass}"
}
