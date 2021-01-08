package com.netki.util

import com.google.protobuf.ByteString
import com.google.protobuf.GeneratedMessageV3
import com.netki.bip75.protocol.Messages
import com.netki.exceptions.EncryptionException
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidOwnersException
import com.netki.model.*
import com.netki.security.CryptoModule
import com.netki.security.EncryptionModule
import com.netki.util.ErrorInformation.ENCRYPTION_INVALID_ERROR
import com.netki.util.ErrorInformation.PARSE_BINARY_MESSAGE_INVALID_INPUT
import java.sql.Timestamp
import java.util.*

/**
 * Transform InvoiceRequestParameters to Messages.InvoiceRequest.Builder.
 *
 * @param senderParameters the sender of the message.
 * @return Messages.InvoiceRequest.Builder.
 */
internal fun InvoiceRequestParameters.toMessageInvoiceRequestBuilderUnsigned(
    senderParameters: SenderParameters,
    attestationsRequested: List<Attestation>,
    recipientParameters: RecipientParameters?
): Messages.InvoiceRequest.Builder {
    val invoiceRequestBuilder = Messages.InvoiceRequest.newBuilder()
        .setAmount(this.amount ?: 0)
        .setMemo(this.memo)
        .setNotificationUrl(this.notificationUrl)
        .setSenderPkiType(senderParameters.pkiDataParameters?.type?.value)
        .setSenderPkiData(senderParameters.pkiDataParameters?.certificatePem?.toByteString())
        .setSenderSignature("".toByteString())
        .setSenderEvCert(senderParameters.evCertificatePem?.toByteString() ?: "".toByteString())

    this.originatorsAddresses.forEach { output ->
        invoiceRequestBuilder.addOriginatorsAddresses(output.toMessageOutput())
    }

    attestationsRequested.forEach {
        invoiceRequestBuilder.addAttestationsRequested(it.toAttestationType())
    }

    recipientParameters?.let {
        invoiceRequestBuilder.recipientChainAddress = recipientParameters.chainAddress ?: ""
        invoiceRequestBuilder.recipientVaspName = recipientParameters.vaspName
    }

    return invoiceRequestBuilder
}

/**
 * Transform Messages.InvoiceRequest to InvoiceRequest object.
 *
 * @return InvoiceRequest.
 */
internal fun Messages.InvoiceRequest.toInvoiceRequest(protocolMessageMetadata: ProtocolMessageMetadata): InvoiceRequest {
    val beneficiaries = mutableListOf<Beneficiary>()
    this.beneficiariesList.forEach { messageBeneficiary ->
        beneficiaries.add(messageBeneficiary.toBeneficiary())
    }

    val originators = mutableListOf<Originator>()
    this.originatorsList.forEach { messageOriginator ->
        originators.add(messageOriginator.toOriginator())
    }

    val originatorsAddresses = mutableListOf<Output>()
    this.originatorsAddressesList.forEach { messageOutput ->
        originatorsAddresses.add(messageOutput.toOutput())
    }

    val attestationsRequested = mutableListOf<Attestation>()
    this.attestationsRequestedList.forEach { attestationType ->
        attestationsRequested.add(attestationType.toAttestation())
    }

    return InvoiceRequest(
        amount = this.amount,
        memo = this.memo,
        notificationUrl = this.notificationUrl,
        originators = originators,
        beneficiaries = beneficiaries,
        originatorsAddresses = originatorsAddresses,
        attestationsRequested = attestationsRequested,
        senderPkiType = this.senderPkiType.getType(),
        senderPkiData = this.senderPkiData.toStringLocal(),
        senderSignature = this.senderSignature.toStringLocal(),
        senderEvCert = this.senderEvCert.toStringLocal(),
        recipientVaspName = this.recipientVaspName,
        recipientChainAddress = this.recipientChainAddress,
        protocolMessageMetadata = protocolMessageMetadata
    )
}

/**
 * Transform binary InvoiceRequest to Messages.InvoiceRequest.
 *
 * @return Messages.InvoiceRequest
 * @throws InvalidObjectException if there is an error parsing the object.
 */
internal fun ByteArray.toMessageInvoiceRequest(): Messages.InvoiceRequest = try {
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
internal fun Messages.PaymentRequest.toPaymentRequest(protocolMessageMetadata: ProtocolMessageMetadata): PaymentRequest {
    val paymentDetails = this.serializedPaymentDetails.toMessagePaymentDetails()

    val beneficiaries = mutableListOf<Beneficiary>()
    this.beneficiariesList.forEach { messageBeneficiary ->
        beneficiaries.add(messageBeneficiary.toBeneficiary())
    }

    val beneficiariesAddresses = mutableListOf<Output>()
    paymentDetails.beneficiariesAddressesList.forEach { messageOutput ->
        beneficiariesAddresses.add(messageOutput.toOutput())
    }

    val attestationsRequested = mutableListOf<Attestation>()
    this.attestationsRequestedList.forEach { attestationType ->
        attestationsRequested.add(attestationType.toAttestation())
    }

    return PaymentRequest(
        paymentDetailsVersion = this.paymentDetailsVersion,
        network = paymentDetails.network,
        beneficiariesAddresses = beneficiariesAddresses,
        time = Timestamp(paymentDetails.time),
        expires = Timestamp(paymentDetails.expires),
        memo = paymentDetails.memo,
        paymentUrl = paymentDetails.paymentUrl,
        merchantData = paymentDetails.merchantData.toStringLocal(),
        beneficiaries = beneficiaries,
        attestationsRequested = attestationsRequested,
        senderPkiType = this.senderPkiType.getType(),
        senderPkiData = this.senderPkiData.toStringLocal(),
        senderSignature = this.senderSignature.toStringLocal(),
        protocolMessageMetadata = protocolMessageMetadata
    )
}

/**
 * Transform Messages.PaymentDetails to Messages.PaymentRequest.Builder.
 *
 * @param senderParameters the sender of the message.
 * @param paymentParametersVersion
 * @return Messages.PaymentRequest.Builder.
 */
internal fun Messages.PaymentDetails.toPaymentRequest(
    senderParameters: SenderParameters,
    paymentParametersVersion: Int,
    attestationsRequested: List<Attestation>
): Messages.PaymentRequest.Builder {
    val paymentRequestBuilder = Messages.PaymentRequest.newBuilder()
        .setPaymentDetailsVersion(paymentParametersVersion)
        .setSerializedPaymentDetails(this.toByteString())
        .setSenderPkiType(senderParameters.pkiDataParameters?.type?.value)
        .setSenderPkiData(senderParameters.pkiDataParameters?.certificatePem?.toByteString())
        .setSenderSignature("".toByteString())

    attestationsRequested.forEach {
        paymentRequestBuilder.addAttestationsRequested(it.toAttestationType())
    }

    return paymentRequestBuilder
}

/**
 * Transform PaymentParameters object to Messages.PaymentDetails object.
 *
 * @return Messages.PaymentDetails.
 */
internal fun PaymentRequestParameters.toMessagePaymentDetails(): Messages.PaymentDetails {
    val messagePaymentDetailsBuilder = Messages.PaymentDetails.newBuilder()
        .setNetwork(this.network)
        .setTime(this.time.time)
        .setExpires(this.expires?.time ?: 0)
        .setMemo(this.memo)
        .setPaymentUrl(this.paymentUrl)
        .setMerchantData(this.merchantData?.toByteString())

    this.beneficiariesAddresses.forEach { output ->
        messagePaymentDetailsBuilder.addBeneficiariesAddresses(output.toMessageOutput())
    }

    return messagePaymentDetailsBuilder.build()
}

/**
 * Transform binary PaymentRequest to Messages.PaymentRequest.
 *
 * @return Messages.PaymentRequest
 * @throws InvalidObjectException if there is an error parsing the object.
 */
internal fun ByteArray.toMessagePaymentRequest(): Messages.PaymentRequest = try {
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
internal fun ByteString.toMessagePaymentDetails(): Messages.PaymentDetails = try {
    Messages.PaymentDetails.parseFrom(this)
} catch (exception: Exception) {
    exception.printStackTrace()
    throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format("paymentDetails", exception.message))
}

/**
 * Transform PaymentParameters object to Messages.Payment.Builder object.
 *
 * @return Messages.Payment.Builder.
 */
internal fun PaymentParameters.toMessagePaymentBuilder(): Messages.Payment.Builder {
    val messagePaymentBuilder = Messages.Payment.newBuilder()
        .setMerchantData(this.merchantData?.toByteString())
        .setMemo(this.memo)

    this.transactions.forEach { transaction ->
        messagePaymentBuilder.addTransactions(transaction.toByteString())
    }

    this.outputs.forEach { output ->
        messagePaymentBuilder.addRefundTo(output.toMessageOutput())
    }

    return messagePaymentBuilder
}

/**
 * Transform Payment object to Messages.Payment object.
 *
 * @return Messages.Payment.
 */
internal fun Payment.toMessagePayment(): Messages.Payment {
    val messagePaymentBuilder = Messages.Payment.newBuilder()
        .setMerchantData(this.merchantData?.toByteString())
        .setMemo(this.memo)

    this.transactions.forEach { transaction ->
        messagePaymentBuilder.addTransactions(transaction.toByteString())
    }

    this.outputs.forEach { output ->
        messagePaymentBuilder.addRefundTo(output.toMessageOutput())
    }

    this.beneficiaries.forEach { beneficiary ->
        messagePaymentBuilder.addBeneficiaries(beneficiary.toMessageBeneficiary())
    }

    this.originators.forEach { originator ->
        messagePaymentBuilder.addOriginators(originator.toMessageOriginator())
    }

    return messagePaymentBuilder.build()
}

/**
 * Transform Messages.Payment to Payment object.
 *
 * @return Payment.
 */
internal fun Messages.Payment.toPayment(protocolMessageMetadata: ProtocolMessageMetadata? = null): Payment {
    val transactionList = mutableListOf<ByteArray>()
    for (messageTransaction in this.transactionsList) {
        transactionList.add(messageTransaction.toByteArray())
    }

    val outputs = mutableListOf<Output>()
    for (messageOutput in this.refundToList) {
        outputs.add(messageOutput.toOutput())
    }

    val beneficiaries = mutableListOf<Beneficiary>()
    for (messageBeneficiary in this.beneficiariesList) {
        beneficiaries.add(messageBeneficiary.toBeneficiary())
    }

    val originators = mutableListOf<Originator>()
    for (messageOriginator in this.originatorsList) {
        originators.add(messageOriginator.toOriginator())
    }

    return Payment(
        merchantData = this.merchantData.toStringLocal(),
        transactions = transactionList,
        outputs = outputs,
        memo = this.memo,
        beneficiaries = beneficiaries,
        originators = originators,
        protocolMessageMetadata = protocolMessageMetadata
    )
}

/**
 * Transform binary Payment to Messages.Payment.
 *
 * @return Messages.Payment
 * @throws InvalidObjectException if there is an error parsing the object.
 */
internal fun ByteArray.toMessagePayment(): Messages.Payment = try {
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
internal fun Messages.PaymentACK.toPaymentAck(protocolMessageMetadata: ProtocolMessageMetadata): PaymentAck =
    PaymentAck(this.payment.toPayment(), this.memo, protocolMessageMetadata)

/**
 * Transform binary PaymentACK to Messages.PaymentACK.
 *
 * @return Messages.PaymentACK
 * @throws InvalidObjectException if there is an error parsing the object.
 */
internal fun ByteArray.toMessagePaymentAck(): Messages.PaymentACK = try {
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
internal fun Payment.toMessagePaymentAck(memo: String?): Messages.PaymentACK = Messages.PaymentACK.newBuilder()
    .setPayment(this.toMessagePayment())
    .setMemo(memo)
    .build()

/**
 * Transform Messages.Output object to Output object.
 *
 * @return Output.
 */
internal fun Messages.Output.toOutput(): Output =
    Output(this.amount, this.script.toStringLocal(), this.currency.toAddressCurrency())

/**
 * Transform Output object to Messages.Output object.
 *
 * @return Messages.Output.
 */
internal fun Output.toMessageOutput(): Messages.Output = Messages.Output.newBuilder()
    .setAmount(this.amount)
    .setScript(this.script.toByteString())
    .setCurrency(this.currency.toCurrencyType())
    .build()

/**
 * Transform BeneficiaryParameters object to Messages.Beneficiary object.
 *
 * @return Messages.Beneficiary.
 */
internal fun BeneficiaryParameters.toMessageBeneficiary(): Messages.Beneficiary {
    val messageBeneficiaryBuilder = Messages.Beneficiary.newBuilder()
        .setPrimaryForTransaction(this.isPrimaryForTransaction)

    this.pkiDataParametersSets.forEachIndexed { index, pkiData ->
        val messageAttestation = Messages.Attestation.newBuilder()
            .setAttestation(pkiData.attestation?.toAttestationType())
            .setPkiType(pkiData.type.value)
            .setPkiData(pkiData.certificatePem.toByteString())
            .setSignature("".toByteString())
            .build()

        messageBeneficiaryBuilder.addAttestations(index, messageAttestation)
    }

    return messageBeneficiaryBuilder.build()
}

/**
 * Transform OriginatorParameters object to Messages.Originator object.
 *
 * @return Messages.Originator.
 */
internal fun OriginatorParameters.toMessageOriginator(): Messages.Originator {
    val messageOriginatorBuilder = Messages.Originator.newBuilder()
        .setPrimaryForTransaction(this.isPrimaryForTransaction)

    this.pkiDataParametersSets.forEachIndexed { index, pkiData ->
        val messageAttestation = Messages.Attestation.newBuilder()
            .setAttestation(pkiData.attestation?.toAttestationType())
            .setPkiType(pkiData.type.value)
            .setPkiData(pkiData.certificatePem.toByteString())
            .setSignature("".toByteString())
            .build()

        messageOriginatorBuilder.addAttestations(index, messageAttestation)
    }

    return messageOriginatorBuilder.build()
}

/**
 * Transform BeneficiaryParameters object to Messages.Beneficiary.Builder object.
 *
 * @return Messages.Beneficiary.
 */
internal fun BeneficiaryParameters.toMessageBeneficiaryBuilderWithoutAttestations(): Messages.Beneficiary.Builder =
    Messages.Beneficiary.newBuilder().setPrimaryForTransaction(this.isPrimaryForTransaction)

/**
 * Transform OriginatorParameters object to Messages.Originator.Builder object.
 *
 * @return Messages.Originator.
 */
internal fun OriginatorParameters.toMessageOriginatorBuilderWithoutAttestations(): Messages.Originator.Builder =
    Messages.Originator.newBuilder().setPrimaryForTransaction(this.isPrimaryForTransaction)

/**
 * Transform PkiDataParameters object to Messages.Attestation object.
 * If there is a PkiType X509SHA256 this message should be signed.
 *
 * @return Messages.Attestation.
 */
internal fun PkiDataParameters.toMessageAttestation(requireSignature: Boolean): Messages.Attestation {
    val messageAttestationUnsignedBuilder = Messages.Attestation.newBuilder()
        .setPkiType(this.type.value)
        .setPkiData(this.certificatePem.toByteString())
        .setSignature("".toByteString())

    this.attestation?.let {
        messageAttestationUnsignedBuilder.setAttestation(it.toAttestationType())
    }

    val messageAttestationUnsigned = messageAttestationUnsignedBuilder.build()

    return when {
        this.type == PkiType.X509SHA256 && requireSignature -> {
            val signature = messageAttestationUnsigned.sign(this.privateKeyPem)
            Messages.Attestation.newBuilder()
                .mergeFrom(messageAttestationUnsigned)
                .setSignature(signature.toByteString())
                .build()
        }
        else -> messageAttestationUnsigned
    }
}

/**
 * Validate if the signature of a Messages.Attestation is valid.
 *
 * @return true if yes, false otherwise.
 */
internal fun Messages.Attestation.validateMessageSignature(requireSignature: Boolean): Boolean = when {
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
 * Remove the signature from Messages.Attestation object.
 *
 * @return Messages.Attestation.
 */
internal fun Messages.Attestation.removeSignature(): Messages.Attestation = Messages.Attestation.newBuilder()
    .mergeFrom(this)
    .setSignature("".toByteString())
    .build()

/**
 * Transform Messages.Beneficiary to Beneficiary object.
 *
 * @return Beneficiary.
 */
internal fun Messages.Beneficiary.toBeneficiary(): Beneficiary {
    val pkiDataSets = mutableListOf<PkiData>()
    this.attestationsList.forEach { messageAttestation ->
        pkiDataSets.add(messageAttestation.toPkiData())
    }
    return Beneficiary(this.primaryForTransaction, pkiDataSets)
}

/**
 * Transform Messages.Originator to Originator object.
 *
 * @return Originator.
 */
internal fun Messages.Originator.toOriginator(): Originator {
    val pkiDataSets = mutableListOf<PkiData>()
    this.attestationsList.forEach { messageAttestation ->
        pkiDataSets.add(messageAttestation.toPkiData())
    }
    return Originator(this.primaryForTransaction, pkiDataSets)
}

/**
 * Transform Beneficiary to Messages.Beneficiary object.
 *
 * @return Messages.Beneficiary.
 */
internal fun Beneficiary.toMessageBeneficiary(): Messages.Beneficiary {
    val messageBeneficiary = Messages.Beneficiary.newBuilder()

    messageBeneficiary.primaryForTransaction = this.isPrimaryForTransaction
    this.pkiDataSet.forEach { pkiDataSet ->
        val attestation = Messages.Attestation.newBuilder()
            .setAttestation(pkiDataSet.attestation?.toAttestationType())
            .setPkiData(pkiDataSet.certificatePem.toByteString())
            .setPkiType(pkiDataSet.type.value)
            .setSignature(pkiDataSet.signature?.toByteString())
            .build()
        messageBeneficiary.addAttestations(attestation)
    }

    return messageBeneficiary.build()
}

/**
 * Transform Owner to Messages.Owner object.
 *
 * @return Messages.Owner.
 */
internal fun Originator.toMessageOriginator(): Messages.Originator {
    val messageOriginator = Messages.Originator.newBuilder()

    messageOriginator.primaryForTransaction = this.isPrimaryForTransaction
    this.pkiDataSet.forEach { pkiDataSet ->
        val attestation = Messages.Attestation.newBuilder()
            .setAttestation(pkiDataSet.attestation?.toAttestationType())
            .setPkiData(pkiDataSet.certificatePem.toByteString())
            .setPkiType(pkiDataSet.type.value)
            .setSignature(pkiDataSet.signature?.toByteString())
            .build()
        messageOriginator.addAttestations(attestation)
    }

    return messageOriginator.build()
}

/**
 * Transform Messages.Attestation to PkiData object.
 *
 * @return PkiData.
 */
internal fun Messages.Attestation.toPkiData(): PkiData = PkiData(
    attestation = this.attestation.toAttestation(),
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
internal fun GeneratedMessageV3.signMessage(senderParameters: SenderParameters): GeneratedMessageV3 {
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
internal fun Messages.InvoiceRequest.signWithSender(senderParameters: SenderParameters): Messages.InvoiceRequest {
    val signature = this.sign(senderParameters.pkiDataParameters?.privateKeyPem!!)

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
internal fun Messages.PaymentRequest.signWithSender(senderParameters: SenderParameters): Messages.PaymentRequest {
    val signature = this.sign(senderParameters.pkiDataParameters?.privateKeyPem!!)

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
internal fun List<OwnerParameters>.signMessage(message: GeneratedMessageV3): OwnerSignatures {
    val ownersSignatures = OwnerSignatures()
    this.forEachIndexed { index, ownerParameters ->
        val signatures = mutableMapOf<Attestation, String>()
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
internal fun GeneratedMessageV3.sign(privateKeyPem: String): String {
    val hash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.signString(hash, privateKeyPem)
}

/**
 * Validate if sender signature of a GeneratedMessageV3 is valid.
 *
 * @return true if yes, false otherwise.
 */
internal fun GeneratedMessageV3.validateMessageSignature(signature: String): Boolean {
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
internal fun Messages.InvoiceRequest.validateSignature(signature: String): Boolean {
    val bytesHash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.validateSignature(signature, bytesHash, this.senderPkiData.toStringLocal())
}

/**
 * Validate that a signature corresponds to a Messages.PaymentRequest.
 *
 * @return  true if yes, false otherwise.
 */
internal fun Messages.PaymentRequest.validateSignature(signature: String): Boolean {
    val bytesHash = CryptoModule.getHash256(this.toByteArray())
    return CryptoModule.validateSignature(signature, bytesHash, this.senderPkiData.toStringLocal())
}

/**
 * Remove sender signature of a GeneratedMessageV3.
 *
 * @return Unsigned message.
 */
internal fun GeneratedMessageV3.removeMessageSenderSignature(): GeneratedMessageV3 {
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
internal fun Messages.InvoiceRequest.removeSenderSignature(): Messages.InvoiceRequest =
    Messages.InvoiceRequest.newBuilder()
        .mergeFrom(this)
        .setSenderSignature("".toByteString())
        .build()

/**
 * Remove sender signature of a Messages.PaymentRequest.
 *
 * @return Unsigned message.
 */
internal fun Messages.PaymentRequest.removeSenderSignature(): Messages.PaymentRequest =
    Messages.PaymentRequest.newBuilder()
        .mergeFrom(this)
        .setSenderSignature("".toByteString())
        .build()

/**
 * Get all the signatures In a list of Beneficiaries including the certificate.
 *
 * @return OwnerSignaturesWithCertificate.
 */
internal fun List<Messages.Beneficiary>.getSignaturesBeneficiary(): List<OwnerSignaturesWithCertificate> {
    val listOwnerSignaturesWithCertificate = mutableListOf<OwnerSignaturesWithCertificate>()
    this.forEach { owner ->
        val ownerSignaturesWithCertificate = OwnerSignaturesWithCertificate()
        owner.attestationsList.forEach { attestation ->
            when (attestation.pkiType) {
                PkiType.NONE.value -> {
                    // nothing to do here
                }
                else -> {
                    ownerSignaturesWithCertificate[attestation.attestation.toAttestation()] = Pair(
                        CryptoModule.certificatePemToClientCertificate(attestation.pkiData.toStringLocal()),
                        attestation.signature.toStringLocal()
                    )
                }
            }
        }
        listOwnerSignaturesWithCertificate.add(ownerSignaturesWithCertificate)
    }
    return listOwnerSignaturesWithCertificate
}

/**
 * Get all the signatures In a list of Owners including the certificate.
 *
 * @return OwnerSignaturesWithCertificate.
 */
internal fun List<Messages.Originator>.getSignaturesOriginator(): List<OwnerSignaturesWithCertificate> {
    val listOwnerSignaturesWithCertificate = mutableListOf<OwnerSignaturesWithCertificate>()
    this.forEach { owner ->
        val ownerSignaturesWithCertificate = OwnerSignaturesWithCertificate()
        owner.attestationsList.forEach { attestation ->
            when (attestation.pkiType) {
                PkiType.NONE.value -> {
                    // nothing to do here
                }
                else -> {
                    ownerSignaturesWithCertificate[attestation.attestation.toAttestation()] = Pair(
                        CryptoModule.certificatePemToClientCertificate(attestation.pkiData.toStringLocal()),
                        attestation.signature.toStringLocal()
                    )
                }
            }
        }
        listOwnerSignaturesWithCertificate.add(ownerSignaturesWithCertificate)
    }
    return listOwnerSignaturesWithCertificate
}

/**
 * Remove all the signatures in a list of Beneficiaries.
 *
 * @return MutableList<Messages.Beneficiary> without signatures.
 */
internal fun List<Messages.Beneficiary>.removeBeneficiarySignatures(): MutableList<Messages.Beneficiary> {
    val beneficiaryWithoutSignature = mutableListOf<Messages.Beneficiary>()
    this.forEachIndexed { index, owner ->
        val ownerWithoutSignaturesBuilder = Messages.Beneficiary.newBuilder()
            .mergeFrom(owner)
        owner.attestationsList.forEachIndexed { attestationIndex, attestation ->
            ownerWithoutSignaturesBuilder.removeAttestations(attestationIndex)
            ownerWithoutSignaturesBuilder.addAttestations(
                attestationIndex, Messages.Attestation.newBuilder()
                    .mergeFrom(attestation)
                    .setSignature("".toByteString())
                    .build()
            )
        }
        beneficiaryWithoutSignature.add(index, ownerWithoutSignaturesBuilder.build())
    }
    return beneficiaryWithoutSignature
}

/**
 * Remove all the signatures in a list of Originators.
 *
 * @return MutableList<Messages.Originator> without signatures.
 */
internal fun List<Messages.Originator>.removeOriginatorSignatures(): MutableList<Messages.Originator> {
    val originatorWithoutSignature = mutableListOf<Messages.Originator>()
    this.forEachIndexed { index, owner ->
        val ownerWithoutSignaturesBuilder = Messages.Originator.newBuilder()
            .mergeFrom(owner)
        owner.attestationsList.forEachIndexed { attestationIndex, attestation ->
            ownerWithoutSignaturesBuilder.removeAttestations(attestationIndex)
            ownerWithoutSignaturesBuilder.addAttestations(
                attestationIndex, Messages.Attestation.newBuilder()
                    .mergeFrom(attestation)
                    .setSignature("".toByteString())
                    .build()
            )
        }
        originatorWithoutSignature.add(index, ownerWithoutSignaturesBuilder.build())
    }
    return originatorWithoutSignature
}

/**
 * Validate that a List<Owners> is valid.
 * Is valid, when it has one single primaryOwner.
 *
 * @throws InvalidOwnersException if is not a valid list.
 */
internal fun List<OwnerParameters>.validate(required: Boolean, ownerType: OwnerType) {
    if (required && this.isEmpty()) {
        throw InvalidOwnersException(
            String.format(
                ErrorInformation.OWNERS_VALIDATION_EMPTY_ERROR,
                ownerType.description
            )
        )
    } else if (!required && this.isEmpty()) {
        return
    }

    val numberOfPrimaryOwners = this.filter { it.isPrimaryForTransaction }.size

    check(numberOfPrimaryOwners != 0) {
        throw InvalidOwnersException(
            String.format(
                ErrorInformation.OWNERS_VALIDATION_NO_PRIMARY_OWNER,
                ownerType.description
            )
        )
    }

    check(numberOfPrimaryOwners <= 1) {
        throw InvalidOwnersException(
            String.format(
                ErrorInformation.OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS,
                ownerType.description
            )
        )
    }
}

/**
 * Get sender's pkiData of a GeneratedMessageV3.
 *
 * @return PkiData.
 */
@Throws(IllegalArgumentException::class)
internal fun GeneratedMessageV3.getMessagePkiType(): PkiType = when (this) {
    is Messages.InvoiceRequest -> this.senderPkiType.getType()
    is Messages.PaymentRequest -> this.senderPkiType.getType()
    is Messages.Attestation -> this.pkiType.getType()
    else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to get Sender PkiType")
}

/**
 * Transform an string to its correspondent PkiType.
 *
 * @return PkiType.
 */
internal fun String.getType(): PkiType = requireNotNull(PkiType.values().find {
    it.value == this
}) {
    "No PkiType found for: ${this.javaClass}"
}

/**
 * Get owners's pkiData of an attestation.
 *
 * @return PkiData.
 */
@Throws(IllegalArgumentException::class)
internal fun Messages.Attestation.getAttestationPkiType(): PkiType = requireNotNull(PkiType.values().find {
    it.value == this.pkiType
}) {
    "No PkiType found for: ${this.javaClass}"
}

/**
 * Transform Attestation to Messages.AttestationType.
 */
internal fun Attestation.toAttestationType(): Messages.AttestationType {
    return when (this) {
        Attestation.LEGAL_PERSON_PRIMARY_NAME -> Messages.AttestationType.LEGAL_PERSON_PRIMARY_NAME
        Attestation.LEGAL_PERSON_SECONDARY_NAME -> Messages.AttestationType.LEGAL_PERSON_SECONDARY_NAME
        Attestation.ADDRESS_DEPARTMENT -> Messages.AttestationType.ADDRESS_DEPARTMENT
        Attestation.ADDRESS_SUB_DEPARTMENT -> Messages.AttestationType.ADDRESS_SUB_DEPARTMENT
        Attestation.ADDRESS_STREET_NAME -> Messages.AttestationType.ADDRESS_STREET_NAME
        Attestation.ADDRESS_BUILDING_NUMBER -> Messages.AttestationType.ADDRESS_BUILDING_NUMBER
        Attestation.ADDRESS_BUILDING_NAME -> Messages.AttestationType.ADDRESS_BUILDING_NAME
        Attestation.ADDRESS_FLOOR -> Messages.AttestationType.ADDRESS_FLOOR
        Attestation.ADDRESS_POSTBOX -> Messages.AttestationType.ADDRESS_POSTBOX
        Attestation.ADDRESS_ROOM -> Messages.AttestationType.ADDRESS_ROOM
        Attestation.ADDRESS_POSTCODE -> Messages.AttestationType.ADDRESS_POSTCODE
        Attestation.ADDRESS_TOWN_NAME -> Messages.AttestationType.ADDRESS_TOWN_NAME
        Attestation.ADDRESS_TOWN_LOCATION_NAME -> Messages.AttestationType.ADDRESS_TOWN_LOCATION_NAME
        Attestation.ADDRESS_DISTRICT_NAME -> Messages.AttestationType.ADDRESS_DISTRICT_NAME
        Attestation.ADDRESS_COUNTRY_SUB_DIVISION -> Messages.AttestationType.ADDRESS_COUNTRY_SUB_DIVISION
        Attestation.ADDRESS_ADDRESS_LINE -> Messages.AttestationType.ADDRESS_ADDRESS_LINE
        Attestation.ADDRESS_COUNTRY -> Messages.AttestationType.ADDRESS_COUNTRY
        Attestation.NATURAL_PERSON_FIRST_NAME -> Messages.AttestationType.NATURAL_PERSON_FIRST_NAME
        Attestation.NATURAL_PERSON_LAST_NAME -> Messages.AttestationType.NATURAL_PERSON_LAST_NAME
        Attestation.BENEFICIARY_PERSON_FIRST_NAME -> Messages.AttestationType.BENEFICIARY_PERSON_FIRST_NAME
        Attestation.BENEFICIARY_PERSON_LAST_NAME -> Messages.AttestationType.BENEFICIARY_PERSON_LAST_NAME
        Attestation.BIRTH_DATE -> Messages.AttestationType.BIRTH_DATE
        Attestation.BIRTH_PLACE -> Messages.AttestationType.BIRTH_PLACE
        Attestation.COUNTRY_OF_RESIDENCE -> Messages.AttestationType.COUNTRY_OF_RESIDENCE
        Attestation.COUNTRY_OF_ISSUE -> Messages.AttestationType.COUNTRY_OF_ISSUE
        Attestation.COUNTRY_OF_REGISTRATION -> Messages.AttestationType.COUNTRY_OF_REGISTRATION
        Attestation.NATIONAL_IDENTIFIER -> Messages.AttestationType.NATIONAL_IDENTIFIER
        Attestation.ACCOUNT_NUMBER -> Messages.AttestationType.ACCOUNT_NUMBER
        Attestation.CUSTOMER_IDENTIFICATION -> Messages.AttestationType.CUSTOMER_IDENTIFICATION
        Attestation.REGISTRATION_AUTHORITY -> Messages.AttestationType.REGISTRATION_AUTHORITY
    }
}

/**
 * Transform Messages.AttestationType to Attestation.
 */
internal fun Messages.AttestationType.toAttestation(): Attestation {
    return when (this) {
        Messages.AttestationType.LEGAL_PERSON_PRIMARY_NAME -> Attestation.LEGAL_PERSON_PRIMARY_NAME
        Messages.AttestationType.LEGAL_PERSON_SECONDARY_NAME -> Attestation.LEGAL_PERSON_SECONDARY_NAME
        Messages.AttestationType.ADDRESS_DEPARTMENT -> Attestation.ADDRESS_DEPARTMENT
        Messages.AttestationType.ADDRESS_SUB_DEPARTMENT -> Attestation.ADDRESS_SUB_DEPARTMENT
        Messages.AttestationType.ADDRESS_STREET_NAME -> Attestation.ADDRESS_STREET_NAME
        Messages.AttestationType.ADDRESS_BUILDING_NUMBER -> Attestation.ADDRESS_BUILDING_NUMBER
        Messages.AttestationType.ADDRESS_BUILDING_NAME -> Attestation.ADDRESS_BUILDING_NAME
        Messages.AttestationType.ADDRESS_FLOOR -> Attestation.ADDRESS_FLOOR
        Messages.AttestationType.ADDRESS_POSTBOX -> Attestation.ADDRESS_POSTBOX
        Messages.AttestationType.ADDRESS_ROOM -> Attestation.ADDRESS_ROOM
        Messages.AttestationType.ADDRESS_POSTCODE -> Attestation.ADDRESS_POSTCODE
        Messages.AttestationType.ADDRESS_TOWN_NAME -> Attestation.ADDRESS_TOWN_NAME
        Messages.AttestationType.ADDRESS_TOWN_LOCATION_NAME -> Attestation.ADDRESS_TOWN_LOCATION_NAME
        Messages.AttestationType.ADDRESS_DISTRICT_NAME -> Attestation.ADDRESS_DISTRICT_NAME
        Messages.AttestationType.ADDRESS_COUNTRY_SUB_DIVISION -> Attestation.ADDRESS_COUNTRY_SUB_DIVISION
        Messages.AttestationType.ADDRESS_ADDRESS_LINE -> Attestation.ADDRESS_ADDRESS_LINE
        Messages.AttestationType.ADDRESS_COUNTRY -> Attestation.ADDRESS_COUNTRY
        Messages.AttestationType.NATURAL_PERSON_FIRST_NAME -> Attestation.NATURAL_PERSON_FIRST_NAME
        Messages.AttestationType.NATURAL_PERSON_LAST_NAME -> Attestation.NATURAL_PERSON_LAST_NAME
        Messages.AttestationType.BENEFICIARY_PERSON_FIRST_NAME -> Attestation.BENEFICIARY_PERSON_FIRST_NAME
        Messages.AttestationType.BENEFICIARY_PERSON_LAST_NAME -> Attestation.BENEFICIARY_PERSON_LAST_NAME
        Messages.AttestationType.BIRTH_DATE -> Attestation.BIRTH_DATE
        Messages.AttestationType.BIRTH_PLACE -> Attestation.BIRTH_PLACE
        Messages.AttestationType.COUNTRY_OF_RESIDENCE -> Attestation.COUNTRY_OF_RESIDENCE
        Messages.AttestationType.COUNTRY_OF_ISSUE -> Attestation.COUNTRY_OF_ISSUE
        Messages.AttestationType.COUNTRY_OF_REGISTRATION -> Attestation.COUNTRY_OF_REGISTRATION
        Messages.AttestationType.NATIONAL_IDENTIFIER -> Attestation.NATIONAL_IDENTIFIER
        Messages.AttestationType.ACCOUNT_NUMBER -> Attestation.ACCOUNT_NUMBER
        Messages.AttestationType.CUSTOMER_IDENTIFICATION -> Attestation.CUSTOMER_IDENTIFICATION
        Messages.AttestationType.REGISTRATION_AUTHORITY -> Attestation.REGISTRATION_AUTHORITY
    }
}

/**
 * Transform AddressCurrency to Messages.CurrencyType.
 */
internal fun AddressCurrency.toCurrencyType(): Messages.CurrencyType {
    return when (this) {
        AddressCurrency.BITCOIN -> Messages.CurrencyType.BITCOIN
        AddressCurrency.ETHEREUM -> Messages.CurrencyType.ETHEREUM
        AddressCurrency.LITECOIN -> Messages.CurrencyType.LITECOIN
        AddressCurrency.BITCOIN_CASH -> Messages.CurrencyType.BITCOIN_CASH
    }
}

/**
 * Transform Messages.CurrencyType to AddressCurrency.
 */
internal fun Messages.CurrencyType.toAddressCurrency(): AddressCurrency {
    return when (this) {
        Messages.CurrencyType.BITCOIN -> AddressCurrency.BITCOIN
        Messages.CurrencyType.ETHEREUM -> AddressCurrency.ETHEREUM
        Messages.CurrencyType.LITECOIN -> AddressCurrency.LITECOIN
        Messages.CurrencyType.BITCOIN_CASH -> AddressCurrency.BITCOIN_CASH
    }
}

/**
 * Transform a message in ByteArray to ProtocolMessage
 */
internal fun ByteArray.toProtocolMessage(
    messageType: MessageType,
    messageInformation: MessageInformation,
    senderParameters: SenderParameters? = null,
    recipientParameters: RecipientParameters? = null
) = when (messageInformation.encryptMessage) {
    true -> this.toProtocolMessageEncrypted(messageType, messageInformation, senderParameters, recipientParameters)
    false -> this.toProtocolMessageUnencrypted(messageType, messageInformation)
}

/**
 * Transform a message in ByteArray to Messages.ProtocolMessage
 */
internal fun ByteArray.toProtocolMessageUnencrypted(
    messageType: MessageType,
    messageInformation: MessageInformation
) = Messages.ProtocolMessage.newBuilder()
    .setVersion(1)
    .setStatusCode(messageInformation.statusCode.code)
    .setMessageType(
        when (messageType) {
            MessageType.INVOICE_REQUEST -> Messages.ProtocolMessageType.INVOICE_REQUEST
            MessageType.PAYMENT_REQUEST -> Messages.ProtocolMessageType.PAYMENT_REQUEST
            MessageType.PAYMENT -> Messages.ProtocolMessageType.PAYMENT
            MessageType.PAYMENT_ACK -> Messages.ProtocolMessageType.PAYMENT_ACK
            else -> Messages.ProtocolMessageType.UNKNOWN_MESSAGE_TYPE
        }
    )
    .setSerializedMessage(this.toByteString())
    .setStatusMessage(messageInformation.statusMessage)
    .setIdentifier(CryptoModule.generateIdentifier(this).toByteString())
    .build()
    .toByteArray()

/**
 * Transform a message in ByteArray to Messages.EncryptedProtocolMessage
 */
internal fun ByteArray.toProtocolMessageEncrypted(
    messageType: MessageType,
    messageInformation: MessageInformation,
    senderParameters: SenderParameters? = null,
    recipientParameters: RecipientParameters? = null
): ByteArray {

    check(recipientParameters?.encryptionParameters?.publicKeyPem != null) {
        throw EncryptionException(ErrorInformation.ENCRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    check(
        senderParameters?.encryptionParameters?.publicKeyPem != null &&
                senderParameters.encryptionParameters.privateKeyPem != null
    ) {
        throw EncryptionException(ErrorInformation.ENCRYPTION_MISSING_SENDER_KEYS_ERROR)
    }

    check(CryptoModule.isECDSAKey(senderParameters?.encryptionParameters?.privateKeyPem)) {
        throw EncryptionException(ErrorInformation.ENCRYPTION_INCORRECT_KEY_FORMAT_ERROR)
    }

    val encryptedMessage = EncryptionModule.encrypt(
        Base64.getEncoder().encodeToString(this),
        recipientParameters?.encryptionParameters?.publicKeyPem!!,
        senderParameters?.encryptionParameters?.publicKeyPem!!,
        senderParameters.encryptionParameters.privateKeyPem
    )

    val encryptedMessageUnsigned = Messages.EncryptedProtocolMessage.newBuilder()
        .setVersion(1)
        .setStatusCode(messageInformation.statusCode.code)
        .setMessageType(
            when (messageType) {
                MessageType.INVOICE_REQUEST -> Messages.ProtocolMessageType.INVOICE_REQUEST
                MessageType.PAYMENT_REQUEST -> Messages.ProtocolMessageType.PAYMENT_REQUEST
                MessageType.PAYMENT -> Messages.ProtocolMessageType.PAYMENT
                MessageType.PAYMENT_ACK -> Messages.ProtocolMessageType.PAYMENT_ACK
                else -> Messages.ProtocolMessageType.UNKNOWN_MESSAGE_TYPE
            }
        )
        .setStatusMessage(messageInformation.statusMessage)
        .setIdentifier(CryptoModule.generateIdentifier(this).toByteString())
        .setReceiverPublicKey(recipientParameters.encryptionParameters.publicKeyPem.toByteString())
        .setSenderPublicKey(senderParameters.encryptionParameters.publicKeyPem.toByteString())
        .setNonce(System.currentTimeMillis() / 1000)
        .setEncryptedMessage(encryptedMessage.toByteString())
        .setSignature("".toByteString())
        .build()

    val hash = CryptoModule.getHash256(encryptedMessageUnsigned.toByteArray())
    val signature = CryptoModule.signStringECDSA(hash, senderParameters.encryptionParameters.privateKeyPem)

    return Messages.EncryptedProtocolMessage.newBuilder()
        .mergeFrom(encryptedMessageUnsigned)
        .setSignature(signature.toByteString())
        .build()
        .toByteArray()
}

/**
 * Validate if sender signature of a EncryptedProtocolMessage is valid.
 *
 * @return true if yes, false otherwise.
 */
internal fun ByteArray.validateMessageEncryptionSignature(): Boolean {
    val signature = Messages.EncryptedProtocolMessage.parseFrom(this).signature.toStringLocal()
    val encryptedProtocolMessage = Messages.EncryptedProtocolMessage.newBuilder()
        .mergeFrom(this)
        .setSignature("".toByteString())
        .build()

    val bytesHash = CryptoModule.getHash256(encryptedProtocolMessage.toByteArray())
    return CryptoModule.validateSignatureECDSA(
        signature,
        bytesHash,
        encryptedProtocolMessage.senderPublicKey.toStringLocal()
    )
}

/**
 * Method to extract serialized message from Messages.ProtocolMessage
 */
internal fun ByteArray.getSerializedMessage(isEncrypted: Boolean, recipientParameters: RecipientParameters? = null) =
    when (isEncrypted) {
        true -> this.getSerializedMessageEncryptedProtocolMessage(recipientParameters)
        false -> this.getSerializedProtocolMessage()
    }

/**
 * Method to extract serialized message from Messages.ProtocolMessage
 */
internal fun ByteArray.getSerializedProtocolMessage(): ByteArray {
    try {
        val protocolMessageMessages = Messages.ProtocolMessage.parseFrom(this)
        return protocolMessageMessages.serializedMessage.toByteArray()
    } catch (exception: Exception) {
        exception.printStackTrace()
        throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format(exception.message))
    }
}

/**
 * Method to extract serialized message from Messages.EncryptedProtocolMessage
 */
internal fun ByteArray.getSerializedMessageEncryptedProtocolMessage(recipientParameters: RecipientParameters?): ByteArray {
    check(recipientParameters?.encryptionParameters?.privateKeyPem != null) {
        throw EncryptionException(ErrorInformation.DECRYPTION_MISSING_RECIPIENT_KEYS_ERROR)
    }

    val protocolMessageMessages = try {
        Messages.EncryptedProtocolMessage.parseFrom(this)
    } catch (exception: Exception) {
        exception.printStackTrace()
        throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format(exception.message))
    }
    try {
        val decryptedMessage = EncryptionModule.decrypt(
            protocolMessageMessages.encryptedMessage.toStringLocal(),
            recipientParameters?.encryptionParameters!!.privateKeyPem!!,
            protocolMessageMessages.senderPublicKey.toStringLocal()
        )
        return Base64.getDecoder().decode(decryptedMessage)
    } catch (exception: Exception) {
        exception.printStackTrace()
        throw EncryptionException(ENCRYPTION_INVALID_ERROR.format(exception.message), exception)
    }
}

/**
 * Method to extract the ProtocolMessageMetadata from a Messages.ProtocolMessage
 */
internal fun ByteArray.extractProtocolMessageMetadata(): ProtocolMessageMetadata {
    try {
        val protocolMessageMessages = Messages.EncryptedProtocolMessage.parseFrom(this)
        return ProtocolMessageMetadata(
            protocolMessageMessages.version,
            StatusCode.getByCode(protocolMessageMessages.statusCode)!!,
            when (protocolMessageMessages.messageType) {
                Messages.ProtocolMessageType.INVOICE_REQUEST -> MessageType.INVOICE_REQUEST
                Messages.ProtocolMessageType.PAYMENT_REQUEST -> MessageType.PAYMENT_REQUEST
                Messages.ProtocolMessageType.PAYMENT -> MessageType.PAYMENT
                Messages.ProtocolMessageType.PAYMENT_ACK -> MessageType.PAYMENT_ACK
                else -> MessageType.UNKNOWN_MESSAGE_TYPE
            },
            protocolMessageMessages.statusMessage,
            protocolMessageMessages.identifier.toStringLocal(),
            true,
            protocolMessageMessages.encryptedMessage.toStringLocal(),
            protocolMessageMessages.receiverPublicKey.toStringLocal(),
            protocolMessageMessages.senderPublicKey.toStringLocal(),
            protocolMessageMessages.nonce,
            protocolMessageMessages.signature.toStringLocal()
        )
    } catch (exception: Exception) {
        // nothing to do here
    }

    try {
        val protocolMessageMessages = Messages.ProtocolMessage.parseFrom(this)
        return ProtocolMessageMetadata(
            protocolMessageMessages.version,
            StatusCode.getByCode(protocolMessageMessages.statusCode)!!,
            when (protocolMessageMessages.messageType) {
                Messages.ProtocolMessageType.INVOICE_REQUEST -> MessageType.INVOICE_REQUEST
                Messages.ProtocolMessageType.PAYMENT_REQUEST -> MessageType.PAYMENT_REQUEST
                Messages.ProtocolMessageType.PAYMENT -> MessageType.PAYMENT
                Messages.ProtocolMessageType.PAYMENT_ACK -> MessageType.PAYMENT_ACK
                else -> MessageType.UNKNOWN_MESSAGE_TYPE
            },
            protocolMessageMessages.statusMessage,
            protocolMessageMessages.identifier.toStringLocal(),
            false
        )
    } catch (exception: Exception) {
        exception.printStackTrace()
        throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format(exception.message))
    }
}

/**
 * Method to change the status to ProtocolMessageMetadata.
 */
internal fun ByteArray.changeStatus(statusCode: StatusCode, statusMessage: String): ByteArray {
    try {
        val protocolMessageMessages = Messages.EncryptedProtocolMessage.parseFrom(this)
        return Messages.EncryptedProtocolMessage.newBuilder()
            .mergeFrom(protocolMessageMessages)
            .setStatusCode(statusCode.code)
            .setStatusMessage(statusMessage)
            .build()
            .toByteArray()

    } catch (exception: Exception) {
        // nothing to do here
    }

    try {
        val protocolMessageMessages = Messages.ProtocolMessage.parseFrom(this)
        return Messages.ProtocolMessage.newBuilder()
            .mergeFrom(protocolMessageMessages)
            .setStatusCode(statusCode.code)
            .setStatusMessage(statusMessage)
            .build()
            .toByteArray()
    } catch (exception: Exception) {
        exception.printStackTrace()
        throw InvalidObjectException(PARSE_BINARY_MESSAGE_INVALID_INPUT.format(exception.message))
    }
}
