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
    senderParameters: SenderParameters,
    attestationsRequested: List<Attestation>
): Messages.InvoiceRequest.Builder {
    val invoiceRequestBuilder = Messages.InvoiceRequest.newBuilder()
        .setAmount(this.amount)
        .setMemo(this.memo)
        .setNotificationUrl(this.notificationUrl)
        .setSenderPkiType(senderParameters.pkiDataParameters.type.value)
        .setSenderPkiData(senderParameters.pkiDataParameters.certificatePem.toByteString())
        .setSenderSignature("".toByteString())

    this.outputs.forEach { output ->
        invoiceRequestBuilder.addOutputs(output.toMessageOutput())
    }

    attestationsRequested.forEach {
        invoiceRequestBuilder.addAttestationsRequested(it.toAttestationType())
    }

    return invoiceRequestBuilder
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

    val outputs = mutableListOf<Output>()
    this.outputsList.forEach { messageOutput ->
        outputs.add(messageOutput.toOutput())
    }

    val attestationsRequested = mutableListOf<Attestation>()
    this.attestationsRequestedList.forEach { attestationType ->
        attestationsRequested.add(attestationType.toAttestation())
    }

    return InvoiceRequest(
        amount = this.amount,
        memo = this.memo,
        notificationUrl = this.notificationUrl,
        owners = owners,
        outputs = outputs,
        attestationsRequested = attestationsRequested,
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

    val attestationsRequested = mutableListOf<Attestation>()
    this.attestationsRequestedList.forEach { attestationType ->
        attestationsRequested.add(attestationType.toAttestation())
    }

    return PaymentRequest(
        paymentDetailsVersion = this.paymentDetailsVersion,
        paymentRequestParameters = PaymentRequestParameters(
            network = paymentDetails.network,
            outputs = outputs,
            time = Timestamp(paymentDetails.time),
            expires = Timestamp(paymentDetails.expires),
            memo = paymentDetails.memo,
            paymentUrl = paymentDetails.paymentUrl,
            merchantData = paymentDetails.merchantData.toStringLocal()
        ),
        owners = owners,
        attestationsRequested = attestationsRequested,
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
    paymentParametersVersion: Int,
    attestationsRequested: List<Attestation>
): Messages.PaymentRequest.Builder {
    val paymentRequestBuilder = Messages.PaymentRequest.newBuilder()
        .setPaymentDetailsVersion(paymentParametersVersion)
        .setSerializedPaymentDetails(this.toByteString())
        .setSenderPkiType(senderParameters.pkiDataParameters.type.value)
        .setSenderPkiData(senderParameters.pkiDataParameters.certificatePem.toByteString())
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
fun PaymentRequestParameters.toMessagePaymentDetails(): Messages.PaymentDetails {
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
 * Transform PaymentParameters object to Messages.Payment.Builder object.
 *
 * @return Messages.Payment.Builder.
 */
fun PaymentParameters.toMessagePaymentBuilder(): Messages.Payment.Builder {
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

    this.owners.forEach { owner ->
        messagePaymentBuilder.addOwners(owner.toMessageOwner())
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
    for (messageTransaction in this.transactionsList) {
        transactionList.add(messageTransaction.toByteArray())
    }

    val outputs = mutableListOf<Output>()
    for (messageOutput in this.refundToList) {
        outputs.add(messageOutput.toOutput())
    }

    val owners = mutableListOf<Owner>()
    for (messageOwner in this.ownersList) {
        owners.add(messageOwner.toOwner())
    }

    return Payment(
        merchantData = this.merchantData.toStringLocal(),
        transactions = transactionList,
        outputs = outputs,
        memo = this.memo,
        owners = owners
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
fun Messages.Output.toOutput(): Output = Output(this.amount, this.script.toStringLocal(), this.currency.toAddressCurrency())

/**
 * Transform Output object to Messages.Output object.
 *
 * @return Messages.Output.
 */
fun Output.toMessageOutput(): Messages.Output = Messages.Output.newBuilder()
    .setAmount(this.amount)
    .setScript(this.script.toByteString())
    .setCurrency(this.currency.toCurrencyType())
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
        val messageAttestation = Messages.Attestation.newBuilder()
            .setAttestation(pkiData.attestation?.toAttestationType())
            .setPkiType(pkiData.type.value)
            .setPkiData(pkiData.certificatePem.toByteString())
            .setSignature("".toByteString())
            .build()

        messageOwnerBuilder.addAttestations(index, messageAttestation)
    }

    return messageOwnerBuilder.build()
}

/**
 * Transform OwnerParameters object to Messages.Owner.Builder object.
 *
 * @return Messages.Owner.
 */
fun OwnerParameters.toMessageOwnerBuilderWithoutAttestations(): Messages.Owner.Builder =
    Messages.Owner.newBuilder().setPrimaryForTransaction(this.isPrimaryForTransaction)

/**
 * Transform PkiDataParameters object to Messages.Attestation object.
 * If there is a PkiType X509SHA256 this message should be signed.
 *
 * @return Messages.Attestation.
 */
fun PkiDataParameters.toMessageAttestation(requireSignature: Boolean): Messages.Attestation {
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
fun Messages.Attestation.validateMessageSignature(requireSignature: Boolean): Boolean = when {
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
fun Messages.Attestation.removeSignature(): Messages.Attestation = Messages.Attestation.newBuilder()
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
    this.attestationsList.forEach { messageAttestation ->
        pkiDataSets.add(messageAttestation.toPkiData())
    }
    return Owner(this.primaryForTransaction, pkiDataSets)
}

/**
 * Transform Owner to Messages.Owner object.
 *
 * @return Messages.Owner.
 */
fun Owner.toMessageOwner(): Messages.Owner {
    val messageOwner = Messages.Owner.newBuilder()

    messageOwner.primaryForTransaction = this.isPrimaryForTransaction
    this.pkiDataSet.forEach { pkiDataSet ->
        val attestation = Messages.Attestation.newBuilder()
            .setAttestation(pkiDataSet.attestation?.toAttestationType())
            .setPkiData(pkiDataSet.certificatePem.toByteString())
            .setPkiType(pkiDataSet.type.value)
            .setSignature(pkiDataSet.signature?.toByteString())
            .build()
        messageOwner.addAttestations(attestation)
    }

    return messageOwner.build()
}

/**
 * Transform Messages.Attestation to PkiData object.
 *
 * @return PkiData.
 */
fun Messages.Attestation.toPkiData(): PkiData = PkiData(
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
 * Remove sender signature of a GeneratedMessageV3.
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
 * Remove all the signatures In a list of Owners including the certificate.
 *
 * @return MutableList<Messages.Owner> without signatures.
 */
fun List<Messages.Owner>.removeOwnersSignatures(): MutableList<Messages.Owner> {
    val ownersWithoutSignature = mutableListOf<Messages.Owner>()
    this.forEachIndexed { index, owner ->
        val ownerWithoutSignaturesBuilder = Messages.Owner.newBuilder()
            .mergeFrom(owner)
        owner.attestationsList.forEachIndexed() { attestationIndex, attestation ->
            ownerWithoutSignaturesBuilder.removeAttestations(attestationIndex)
            ownerWithoutSignaturesBuilder.addAttestations(
                attestationIndex, Messages.Attestation.newBuilder()
                    .mergeFrom(attestation)
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
 * Validate that a List<Owners> is valid.
 * Is valid, when it has one single primaryOwner.
 *
 * @throws InvalidOwnersException if is not a valid list.
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
    is Messages.Attestation -> this.pkiType.getType()
    else -> throw IllegalArgumentException("Message: ${this.javaClass}, not supported to get Sender PkiType")
}

/**
 * Transform an string to its correspondent PkiType.
 *
 * @return PkiType.
 */
fun String.getType(): PkiType = requireNotNull(PkiType.values().find {
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
fun Messages.Attestation.getAttestationPkiType(): PkiType = requireNotNull(PkiType.values().find {
    it.value == this.pkiType
}) {
    "No PkiType found for: ${this.javaClass}"
}

/**
 * Transform Attestation to Messages.AttestationType.
 */
fun Attestation.toAttestationType(): Messages.AttestationType {
    return when (this) {
        Attestation.ADDRESS_1 -> Messages.AttestationType.ADDRESS_1
        Attestation.ADDRESS_2 -> Messages.AttestationType.ADDRESS_2
        Attestation.BENEFICIARY_PERSON_FIRST_NAME -> Messages.AttestationType.BENEFICIARY_PERSON_FIRST_NAME
        Attestation.BENEFICIARY_PERSON_LAST_NAME -> Messages.AttestationType.BENEFICIARY_PERSON_LAST_NAME
        Attestation.BIRTH_DATE -> Messages.AttestationType.BIRTH_DATE
        Attestation.BIRTH_PLACE -> Messages.AttestationType.BIRTH_PLACE
        Attestation.COUNTRY_OF_RESIDENCE -> Messages.AttestationType.COUNTRY_OF_RESIDENCE
        Attestation.CUSTOMER_IDENTIFICATION -> Messages.AttestationType.CUSTOMER_IDENTIFICATION
        Attestation.ISSUING_COUNTRY -> Messages.AttestationType.ISSUING_COUNTRY
        Attestation.LEGAL_PERSON_NAME -> Messages.AttestationType.LEGAL_PERSON_NAME
        Attestation.NATIONAL_IDENTIFIER -> Messages.AttestationType.NATIONAL_IDENTIFIER
        Attestation.NATIONAL_IDENTIFIER_NUMBER -> Messages.AttestationType.NATIONAL_IDENTIFIER_NUMBER
        Attestation.NATURAL_PERSON_FIRST_NAME -> Messages.AttestationType.NATURAL_PERSON_FIRST_NAME
        Attestation.NATURAL_PERSON_LAST_NAME -> Messages.AttestationType.NATURAL_PERSON_LAST_NAME
        Attestation.ACCOUNT_NUMBER -> Messages.AttestationType.ACCOUNT_NUMBER
        Attestation.REGISTRATION_AUTHORITY -> Messages.AttestationType.REGISTRATION_AUTHORITY
    }
}

/**
 * Transform Messages.AttestationType to Attestation.
 */
fun Messages.AttestationType.toAttestation(): Attestation {
    return when (this) {
        Messages.AttestationType.ADDRESS_1 -> Attestation.ADDRESS_1
        Messages.AttestationType.ADDRESS_2 -> Attestation.ADDRESS_2
        Messages.AttestationType.BENEFICIARY_PERSON_FIRST_NAME -> Attestation.BENEFICIARY_PERSON_FIRST_NAME
        Messages.AttestationType.BENEFICIARY_PERSON_LAST_NAME -> Attestation.BENEFICIARY_PERSON_LAST_NAME
        Messages.AttestationType.BIRTH_DATE -> Attestation.BIRTH_DATE
        Messages.AttestationType.BIRTH_PLACE -> Attestation.BIRTH_PLACE
        Messages.AttestationType.COUNTRY_OF_RESIDENCE -> Attestation.COUNTRY_OF_RESIDENCE
        Messages.AttestationType.CUSTOMER_IDENTIFICATION -> Attestation.CUSTOMER_IDENTIFICATION
        Messages.AttestationType.ISSUING_COUNTRY -> Attestation.ISSUING_COUNTRY
        Messages.AttestationType.LEGAL_PERSON_NAME -> Attestation.LEGAL_PERSON_NAME
        Messages.AttestationType.NATIONAL_IDENTIFIER -> Attestation.NATIONAL_IDENTIFIER
        Messages.AttestationType.NATIONAL_IDENTIFIER_NUMBER -> Attestation.NATIONAL_IDENTIFIER_NUMBER
        Messages.AttestationType.NATURAL_PERSON_FIRST_NAME -> Attestation.NATURAL_PERSON_FIRST_NAME
        Messages.AttestationType.NATURAL_PERSON_LAST_NAME -> Attestation.NATURAL_PERSON_LAST_NAME
        Messages.AttestationType.ACCOUNT_NUMBER -> Attestation.ACCOUNT_NUMBER
        Messages.AttestationType.REGISTRATION_AUTHORITY -> Attestation.REGISTRATION_AUTHORITY
    }
}

/**
 * Transform AddressCurrency to Messages.CurrencyType.
 */
fun AddressCurrency.toCurrencyType(): Messages.CurrencyType {
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
fun Messages.CurrencyType.toAddressCurrency(): AddressCurrency {
    return when (this) {
        Messages.CurrencyType.BITCOIN -> AddressCurrency.BITCOIN
        Messages.CurrencyType.ETHEREUM -> AddressCurrency.ETHEREUM
        Messages.CurrencyType.LITECOIN -> AddressCurrency.LITECOIN
        Messages.CurrencyType.BITCOIN_CASH -> AddressCurrency.BITCOIN_CASH
    }
}
