package com.netki.util

import com.google.protobuf.ByteString
import com.google.protobuf.GeneratedMessageV3
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidOwnersException
import com.netki.model.*
import com.netki.security.CryptoModule
import java.security.cert.X509Certificate
import java.sql.Timestamp

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
 * Transform PkiDataParameters object to Messages.Signature object.
 *
 * @return Messages.Signature.
 */
fun PkiDataParameters.toMessageSignature(signature: ByteString): Messages.Signature = Messages.Signature.newBuilder()
    .setAttestation(this.attestation)
    .setPkiType(this.type.value)
    .setPkiData(this.certificatePem.toByteString())
    .setSignature(signature)
    .build()

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
        senderPkiType = PkiType.valueOf(this.senderPkiType),
        senderPkiData = this.senderPkiData.toStringLocal(),
        senderSignature = this.senderSignature.toStringLocal()
    )
}

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
    type = PkiType.valueOf(this.pkiType),
    signature = this.signature.toStringLocal()
)

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
    throw InvalidObjectException("Invalid object for invoiceRequest, exception: ${exception.message}")
}

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
 * Transform OwnerParameters to Messages.Owner attaching the correspondent signature to each PkiData.
 *
 * @param ownerSignatures signatures created by this user.
 * @return Messages.PaymentDetails.
 */
fun OwnerParameters.toOwnerMessageWithSignature(ownerSignatures: MutableMap<String, String>?): Messages.Owner {
    val messageOwnerBuilder = Messages.Owner.newBuilder()
        .setPrimaryForTransaction(this.isPrimaryForTransaction)
    this.pkiDataParametersSets.forEachIndexed { indexPki, pkiData ->
        val signature = if (pkiData.attestation != null && pkiData.type != PkiType.NONE) {
            ownerSignatures?.get(pkiData.attestation)?.toByteString() ?: "".toByteString()
        } else {
            "".toByteString()
        }
        val messageSignature = pkiData.toMessageSignature(signature)

        messageOwnerBuilder.addSignatures(indexPki, messageSignature)
    }

    return messageOwnerBuilder.build()
}

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
 * Transform binary PaymentRequest to Messages.PaymentRequest.
 *
 * @return Messages.PaymentRequest
 * @throws InvalidObjectException if there is an error parsing the object.
 */
fun ByteArray.toMessagePaymentRequest(): Messages.PaymentRequest = try {
    Messages.PaymentRequest.parseFrom(this)
} catch (exception: Exception) {
    exception.printStackTrace()
    throw InvalidObjectException("Invalid object for paymentRequest, exception: ${exception.message}")
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
    throw InvalidObjectException("Invalid object for paymentDetails, exception: ${exception.message}")
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
        senderPkiType = PkiType.valueOf(this.senderPkiType),
        senderPkiData = this.senderPkiData.toStringLocal(),
        senderSignature = this.senderSignature.toStringLocal()
    )
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
    throw InvalidObjectException("Invalid object for payment, exception: ${exception.message}")
}

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
    throw InvalidObjectException("Invalid object for paymentAck, exception: ${exception.message}")
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
 * Transform Messages.Payment to Payment object.
 *
 * @return Payment.
 */
fun Messages.PaymentACK.toPaymentAck(): PaymentAck = PaymentAck(this.payment.toPayment(), this.memo)

/**
 * Remove sender signature of a GeneratedMessageV3
 *
 * @return Unsigned message.
 */
fun GeneratedMessageV3.removeSenderSignature(): GeneratedMessageV3 {
    return when (val senderPkiType = this.geSenderPkiType()) {
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
 * Remove sender signature of a Messages.InvoiceRequest.
 *
 * @return Unsigned message.
 */
fun Messages.PaymentRequest.removeSenderSignature(): Messages.PaymentRequest = Messages.PaymentRequest.newBuilder()
    .mergeFrom(this)
    .setSenderSignature("".toByteString())
    .build()

/**
 * Validate if sender signature of a GeneratedMessageV3 is valid.
 *
 * @return true if yes, false otherwise.
 */
fun GeneratedMessageV3.validateMessageSignature(signature: String): Boolean {
    return when (val senderPkiType = this.geSenderPkiType()) {
        PkiType.NONE -> true
        PkiType.X509SHA256 -> when (this) {
            is Messages.InvoiceRequest -> this.validateMessageSignature(signature)
            is Messages.PaymentRequest -> this.validateMessageSignature(signature)
            else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to validate sender signature")
        }
        else -> throw IllegalArgumentException("PkiType: $senderPkiType, not supported")
    }
}

/**
 * Get sender's pkiData of a GeneratedMessageV3.
 *
 * @return PkiData.
 */
@Throws(IllegalArgumentException::class)
fun GeneratedMessageV3.geSenderPkiType(): PkiType = PkiType.valueOf(
    when (this) {
        is Messages.InvoiceRequest -> this.senderPkiType
        is Messages.PaymentRequest -> this.senderPkiType
        else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to get Sender PkiType")
    }
)

/**
 * Validate that a signature corresponds to a Messages.InvoiceRequest.
 *
 * @return  true if yes, false otherwise.
 */
fun Messages.InvoiceRequest.validateMessageSignature(signature: String): Boolean {
    val bytesHash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.validateSignature(signature, bytesHash, this.senderPkiData.toStringLocal())
}

/**
 * Validate that a signature corresponds to a Messages.PaymentRequest.
 *
 * @return  true if yes, false otherwise.
 */
fun Messages.PaymentRequest.validateMessageSignature(signature: String): Boolean {
    val bytesHash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.validateSignature(signature, bytesHash, this.senderPkiData.toStringLocal())
}

/**
 * Get all the signatures In a list of Owners including the certificate.
 *
 * @return OwnerSignaturesWithCertificate.
 */
fun List<Messages.Owner>.getSignatures(): OwnerSignaturesWithCertificate {
    val ownerSignaturesWithCertificate = OwnerSignaturesWithCertificate()
    this.forEach { owner ->
        owner.signaturesList.forEach { signature ->
            when (signature.pkiType) {
                PkiType.NONE.value -> {
                    // nothing to do here
                }
                else -> {
                    ownerSignaturesWithCertificate[signature.attestation] = Pair(
                        CryptoModule.certificatePemToObject(signature.pkiData.toStringLocal()) as X509Certificate,
                        signature.signature.toStringLocal()
                    )
                }
            }
        }
    }
    return ownerSignaturesWithCertificate
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
 * Sign a GeneratedMessageV3 with the sender information.
 *
 * @return GeneratedMessageV3 signed.
 */
@Throws(IllegalArgumentException::class)
fun GeneratedMessageV3.signMessage(senderParameters: SenderParameters): GeneratedMessageV3 {
    return when(val senderPkiType = this.geSenderPkiType()) {
        PkiType.NONE -> this
        PkiType.X509SHA256 -> when (this) {
            is Messages.InvoiceRequest -> this.signMessage(senderParameters)
            is Messages.PaymentRequest -> this.signMessage(senderParameters)
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
fun Messages.InvoiceRequest.signMessage(senderParameters: SenderParameters): Messages.InvoiceRequest {
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
fun Messages.PaymentRequest.signMessage(senderParameters: SenderParameters): Messages.PaymentRequest {
    val signature = this.sign(senderParameters.pkiDataParameters.privateKeyPem)

    return Messages.PaymentRequest.newBuilder()
        .mergeFrom(this)
        .setSenderSignature(signature.toByteString())
        .build()
}
