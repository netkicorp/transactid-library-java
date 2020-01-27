package com.netki.bip75.service.impl

import com.netki.bip75.protocol.Protos
import com.netki.bip75.service.Bip75Service
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
        val invoiceRequestProto = Protos.InvoiceRequest.parseFrom(invoiceRequestBinary)
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
        val invoiceRequest = try {
            Protos.InvoiceRequest.parseFrom(invoiceRequestBinary)
        } catch (exception: Exception) {
            return false
        }

        return when (invoiceRequest.pkiType) {
            PkiType.NONE.value -> true
            PkiType.X509SHA256.value -> {
                val certificate = invoiceRequest.pkiData.toStringLocal()
                val signatureString: String = invoiceRequest.signature.toStringLocal()

                val paymentRequestModified = Protos.InvoiceRequest.newBuilder()
                    .mergeFrom(invoiceRequest)
                    .setSignature("".toByteString())
                    .build()

                val hash = CryptoModule.getHash256(paymentRequestModified.toByteArray())
                return CryptoModule.validateSignature(signatureString, hash, certificate)
            }
            else -> throw IllegalArgumentException("Type not supported")
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
        val paymentRequestProto = Protos.PaymentRequest.parseFrom(paymentRequestBinary)
        val paymentDetailsProto = Protos.PaymentDetails.parseFrom(paymentRequestProto.serializedPaymentDetails)
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
        val paymentRequest = try {
            Protos.PaymentRequest.parseFrom(paymentRequestBinary)
        } catch (exception: Exception) {
            return false
        }

        return when (paymentRequest.pkiType) {
            PkiType.NONE.value -> true
            PkiType.X509SHA256.value -> {
                val certificate = paymentRequest.pkiData.toStringLocal()
                val signatureString: String = paymentRequest.signature.toStringLocal()

                val paymentRequestModified = Protos.PaymentRequest.newBuilder()
                    .mergeFrom(paymentRequest)
                    .setSignature("".toByteString())
                    .build()

                val hash = CryptoModule.getHash256(paymentRequestModified.toByteArray())
                return CryptoModule.validateSignature(signatureString, hash, certificate)
            }
            else -> throw IllegalArgumentException("Type not supported")
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
        val paymentProto = Protos.Payment.parseFrom(paymentBinary)

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
        try {
            Protos.Payment.parseFrom(paymentBinary)
        } catch (exception: Exception) {
            return false
        }
        return true
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
        val paymentAckProto = Protos.PaymentACK.parseFrom(paymentAckBinary)
        return PaymentAck(parsePayment(paymentAckProto.payment.toByteArray()), paymentAckProto.memo)
    }

    /**
     * {@inheritDoc}
     */
    override fun isPaymentAckValid(paymentAckBinary: ByteArray): Boolean {
        try {
            Protos.PaymentACK.parseFrom(paymentAckBinary)
        } catch (exception: Exception) {
            return false
        }
        return true
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
