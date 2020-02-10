package com.netki.bip75.service.impl

import com.google.protobuf.ByteString
import com.netki.bip75.protocol.Protos
import com.netki.bip75.service.Bip75Service
import com.netki.exceptions.InvalidObjectException
import com.netki.exceptions.InvalidSignatureException
import com.netki.model.*
import com.netki.security.CryptoModule
import com.netki.util.toByteString
import com.netki.util.toStringLocal
import java.sql.Timestamp

/**
 * {@inheritDoc}
 */
class Bip75ServiceNetki() : Bip75Service {

    /**
     * {@inheritDoc}
     */
    override fun createInvoiceRequest(
        invoiceRequestParameters: InvoiceRequestParameters,
        keyPairPem: KeyPairPem
    ): ByteArray {
        val certificate = CryptoModule.certificatePemToObject(keyPairPem.certificatePem)
        val invoiceRequest = Protos.InvoiceRequest.newBuilder()
            .setSenderPublicKey(CryptoModule.objectToPublicKeyPem(certificate.publicKey).toByteString())
            .setAmount(invoiceRequestParameters.amount)
            .setPkiType(keyPairPem.type.value)
            .setPkiData(keyPairPem.certificatePem.toByteString())
            .setMemo(invoiceRequestParameters.memo)
            .setNotificationUrl(invoiceRequestParameters.notificationUrl)
            .setSignature("".toByteString())
            .build()

        return when (keyPairPem.type) {
            PkiType.NONE -> invoiceRequest.toByteArray()
            PkiType.X509SHA256 -> {
                val hash = CryptoModule.getHash256(invoiceRequest.toByteArray())
                val signature = CryptoModule.signString(hash, keyPairPem.privateKeyPem)

                Protos.InvoiceRequest.newBuilder()
                    .mergeFrom(invoiceRequest)
                    .setSignature(signature.toByteString())
                    .build()
                    .toByteArray()
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun parseInvoiceRequest(invoiceRequestBinary: ByteArray): InvoiceRequest {
        val invoiceRequestProto = parseInvoiceRequestBinary(invoiceRequestBinary)
        return InvoiceRequest(
            senderPublicKey = invoiceRequestProto.senderPublicKey.toStringLocal(),
            amount = invoiceRequestProto.amount,
            pkiType = invoiceRequestProto.pkiType,
            pkiData = invoiceRequestProto.pkiData?.toStringLocal(),
            memo = invoiceRequestProto.memo,
            notificationUrl = invoiceRequestProto.notificationUrl,
            signature = invoiceRequestProto.signature?.toStringLocal()
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun isInvoiceRequestValid(invoiceRequestBinary: ByteArray): Boolean {
        val invoiceRequestProto = parseInvoiceRequestBinary(invoiceRequestBinary)

        return when (invoiceRequestProto.pkiType) {
            PkiType.NONE.value -> true
            PkiType.X509SHA256.value -> {
                val certificate = invoiceRequestProto.pkiData.toStringLocal()
                val signatureString: String = invoiceRequestProto.signature.toStringLocal()

                val paymentRequestModified = Protos.InvoiceRequest.newBuilder()
                    .mergeFrom(invoiceRequestProto)
                    .setSignature("".toByteString())
                    .build()

                val hash = CryptoModule.getHash256(paymentRequestModified.toByteArray())
                return try {
                    if (CryptoModule.validateSignature(signatureString, hash, certificate)) {
                        true
                    } else {
                        throw InvalidSignatureException("Invalid signature for invoiceRequest")
                    }
                } catch (exception: Exception) {
                    throw InvalidSignatureException("Invalid signature for invoiceRequest")
                }
            }
            else -> throw IllegalArgumentException("Type: ${invoiceRequestProto.pkiType}, not supported")
        }
    }

    /**
     * Parse binary InvoiceRequest to Protos.InvoiceRequest
     */
    private fun parseInvoiceRequestBinary(invoiceRequestBinary: ByteArray): Protos.InvoiceRequest {
        return try {
            Protos.InvoiceRequest.parseFrom(invoiceRequestBinary)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw InvalidObjectException("Invalid object for invoiceRequest, exception: ${exception.message}")
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentRequest(
        paymentDetails: PaymentDetails,
        keyPairPem: KeyPairPem,
        paymentDetailsVersion: Int
    ): ByteArray {
        val paymentDetailsBuilder = Protos.PaymentDetails.newBuilder()
            .setNetwork(paymentDetails.network)
            .setTime(paymentDetails.time.time)
            .setExpires(paymentDetails.expires?.time ?: 0)
            .setMemo(paymentDetails.memo)
            .setPaymentUrl(paymentDetails.paymentUrl)
            .setMerchantData(paymentDetails.merchantData?.toByteString())

        for (output in paymentDetails.outputs) {
            paymentDetailsBuilder.addOutputs(outputToProtoOutput(output))
        }

        val paymentDetailsProto = paymentDetailsBuilder.build()

        val paymentRequest = Protos.PaymentRequest.newBuilder()
            .setPaymentDetailsVersion(paymentDetailsVersion)
            .setPkiType(keyPairPem.type.value)
            .setPkiData(keyPairPem.certificatePem.toByteString())
            .setSerializedPaymentDetails(paymentDetailsProto.toByteString())
            .setSignature("".toByteString())
            .build()

        return when (keyPairPem.type) {
            PkiType.NONE -> paymentRequest.toByteArray()
            PkiType.X509SHA256 -> {
                val hash = CryptoModule.getHash256(paymentRequest.toByteArray())
                val signature = CryptoModule.signString(hash, keyPairPem.privateKeyPem)

                Protos.PaymentRequest.newBuilder()
                    .mergeFrom(paymentRequest)
                    .setSignature(signature.toByteString())
                    .build()
                    .toByteArray()
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentRequest(paymentRequestBinary: ByteArray): PaymentRequest {
        val paymentRequestProto = parsePaymentRequestBinary(paymentRequestBinary)
        val paymentDetailsProto = parsePaymentDetailsBinary(paymentRequestProto.serializedPaymentDetails)
        val outputs = mutableListOf<Output>()
        for (outputProto in paymentDetailsProto.outputsList) {
            outputs.add(Output(outputProto.amount, outputProto.script.toStringLocal()))
        }

        return PaymentRequest(
            paymentDetailsVersion = paymentRequestProto.paymentDetailsVersion,
            pkiType = paymentRequestProto.pkiType,
            pkiData = paymentRequestProto.pkiData.toStringLocal(),
            paymentDetails = PaymentDetails(
                network = paymentDetailsProto.network,
                outputs = outputs,
                time = Timestamp(paymentDetailsProto.time),
                expires = Timestamp(paymentDetailsProto.expires),
                memo = paymentDetailsProto.memo,
                paymentUrl = paymentDetailsProto.paymentUrl,
                merchantData = paymentDetailsProto.merchantData.toStringLocal()
            ),
            signature = paymentRequestProto.signature.toStringLocal()
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentRequestValid(paymentRequestBinary: ByteArray): Boolean {
        val paymentRequestProto = parsePaymentRequestBinary(paymentRequestBinary)

        return when (paymentRequestProto.pkiType) {
            PkiType.NONE.value -> true
            PkiType.X509SHA256.value -> {
                val certificate = paymentRequestProto.pkiData.toStringLocal()
                val signatureString: String = paymentRequestProto.signature.toStringLocal()

                val paymentRequestModified = Protos.PaymentRequest.newBuilder()
                    .mergeFrom(paymentRequestProto)
                    .setSignature("".toByteString())
                    .build()

                val hash = CryptoModule.getHash256(paymentRequestModified.toByteArray())
                return try {
                    if (CryptoModule.validateSignature(signatureString, hash, certificate)) {
                        true
                    } else {
                        throw InvalidSignatureException("Invalid signature for paymentRequest")
                    }
                } catch (exception: Exception) {
                    throw InvalidSignatureException("Invalid signature for paymentRequest")
                }
            }
            else -> throw IllegalArgumentException("Type: ${paymentRequestProto.pkiType}, not supported")
        }
    }

    /**
     * Parse binary PaymentRequest to Protos.PaymentRequest
     */
    private fun parsePaymentRequestBinary(paymentRequestBinary: ByteArray): Protos.PaymentRequest {
        return try {
            Protos.PaymentRequest.parseFrom(paymentRequestBinary)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw InvalidObjectException("Invalid object for paymentRequest, exception: ${exception.message}")
        }
    }

    /**
     * Parse binary PaymentDetails to Protos.PaymentDetails
     */
    private fun parsePaymentDetailsBinary(serializedPaymentDetails: ByteString): Protos.PaymentDetails {
        return try {
            Protos.PaymentDetails.parseFrom(serializedPaymentDetails)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw InvalidObjectException("Invalid object for paymentDetails, exception: ${exception.message}")
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun createPayment(payment: Payment): ByteArray = createPaymentProto(payment).toByteArray()

    /**
     * {@inheritDoc}
     */
    override fun parsePayment(paymentBinary: ByteArray): Payment {
        val paymentProto = parsePaymentBinary(paymentBinary)

        val transactionList = mutableListOf<ByteArray>()
        for (transaction in paymentProto.transactionsList) {
            transactionList.add(transaction.toByteArray())
        }

        val outputs = mutableListOf<Output>()
        for (protoOutput in paymentProto.refundToList) {
            outputs.add(protoOutputToOutput(protoOutput))
        }
        return Payment(
            merchantData = paymentProto.merchantData.toStringLocal(),
            transactions = transactionList,
            outputs = outputs,
            memo = paymentProto.memo
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentValid(paymentBinary: ByteArray): Boolean {
        parsePaymentBinary(paymentBinary)
        return true
    }

    /**
     * Parse binary Payment to Protos.Payment
     */
    private fun parsePaymentBinary(paymentBinary: ByteArray): Protos.Payment {
        return try {
            Protos.Payment.parseFrom(paymentBinary)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw InvalidObjectException("Invalid object for payment, exception: ${exception.message}")
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun createPaymentAck(payment: Payment, memo: String): ByteArray = Protos.PaymentACK.newBuilder()
        .setPayment(createPaymentProto(payment))
        .setMemo(memo)
        .build()
        .toByteArray()

    /**
     * {@inheritDoc}
     */
    override fun parsePaymentAck(paymentAckBinary: ByteArray): PaymentAck {
        val paymentAckProto = parsePaymentAckBinary(paymentAckBinary)
        return PaymentAck(parsePayment(paymentAckProto.payment.toByteArray()), paymentAckProto.memo)
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean {
        parsePaymentAckBinary(paymentAckBinary)
        return true
    }

    /**
     * Parse binary PaymentAck to Protos.PaymentAck
     */
    private fun parsePaymentAckBinary(paymentAckBinary: ByteArray): Protos.PaymentACK {
        return try {
            Protos.PaymentACK.parseFrom(paymentAckBinary)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw InvalidObjectException("Invalid object for paymentAck, exception: ${exception.message}")
        }
    }

    /**
     * Transform Payment object to ProtoBuffer object.
     *
     * @param payment to transform.
     * @return Protos.Payment.
     */
    private fun createPaymentProto(payment: Payment): Protos.Payment {
        val paymentBuilder = Protos.Payment.newBuilder()
            .setMerchantData(payment.merchantData?.toByteString())
            .setMemo(payment.memo)

        for (transaction in payment.transactions) {
            paymentBuilder.addTransactions(transaction.toByteString())
        }

        for (output in payment.outputs) {
            paymentBuilder.addRefundTo(outputToProtoOutput(output))
        }

        return paymentBuilder.build()
    }

    /**
     * Transform Output object to ProtoBuffer object.
     *
     * @param output to transform.
     * @return Protos.Output.
     */
    private fun outputToProtoOutput(output: Output) = Protos.Output.newBuilder()
        .setAmount(output.amount)
        .setScript(output.script.toByteString())
        .build()

    /**
     * Transform ProtoBuffer object to Output object.
     *
     * @param protoOutput to transform.
     * @return Output.
     */
    private fun protoOutputToOutput(protoOutput: Protos.Output) =
        Output(protoOutput.amount, protoOutput.script.toStringLocal())
}
