package com.netki.util

import com.netki.model.InvoiceRequestParameters
import com.netki.model.Output
import com.netki.model.PaymentAck
import com.netki.model.PaymentDetails
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.cert.X509ExtensionUtils
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.util.*

object TestData {

    object Keys {
        const val HASH_ALGORITHM = "SHA256withRSA"
        fun generateKeyPair(): KeyPair {
            Security.addProvider(BouncyCastleProvider())
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
            keyPairGenerator.initialize(2048, SecureRandom())
            return keyPairGenerator.generateKeyPair()
        }

        fun generateCertificate(keyPair: KeyPair, hashAlgorithm: String, cn: String): Certificate {
            val now = Instant.now()
            val notBefore = Date.from(now)
            val notAfter = Date.from(now.plus(Duration.ofDays(1)))

            val contentSigner = JcaContentSignerBuilder(hashAlgorithm).build(keyPair.private)
            val x500Name = X500Name("CN=$cn")
            val certificateBuilder = JcaX509v3CertificateBuilder(
                x500Name,
                BigInteger.valueOf(now.toEpochMilli()),
                notBefore,
                notAfter,
                x500Name,
                keyPair.public
            )
                .addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(keyPair.public))
                .addExtension(Extension.authorityKeyIdentifier, false, createAuthorityKeyId(keyPair.public))
                .addExtension(Extension.basicConstraints, true, BasicConstraints(true))

            return JcaX509CertificateConverter().setProvider(BouncyCastleProvider())
                .getCertificate(certificateBuilder.build(contentSigner))
        }

        private fun createSubjectKeyId(publicKey: PublicKey): SubjectKeyIdentifier {
            val publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.encoded)
            val digCalc = BcDigestCalculatorProvider().get(AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1))

            return X509ExtensionUtils(digCalc).createSubjectKeyIdentifier(publicKeyInfo)
        }

        private fun createAuthorityKeyId(publicKey: PublicKey): AuthorityKeyIdentifier {
            val publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
            val digCalc = BcDigestCalculatorProvider().get(AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

            return X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo);
        }

    }

    object Hash {
        const val STRING_TEST = "This is just a random string to hash"
        const val STRING_TEST_HASH = "c1496e82236fe848dd64bb36aed3d25cd1aa4e72f9a5dbb803bd63545c0c1ef3"
        const val SHA_256_HASH_LENGTH = 64
    }

    object Signature {
        const val STRING_TEST = "This is just a random string to sign"
    }

    object InvoiceRequest {
        val INVOICE_REQUEST_DATA = InvoiceRequestParameters(
            amount = 1000,
            memo = "memo",
            notificationUrl = "notificationUrl"
        )
    }

    object PaymentRequest {
        val OUTPUTS = listOf(
            Output(1000, "Script 1"),
            Output(2000, "Script 2")
        )
        val PAYMENT_DETAILS = PaymentDetails(
            network = "main",
            outputs = OUTPUTS,
            time = Timestamp(System.currentTimeMillis()),
            expires = Timestamp(System.currentTimeMillis()),
            memo = "memo",
            paymentUrl = "www.payment.url/test",
            merchantData = "merchant data"
        )
    }

    object Payment {
        val MEMO = "memo"
        val PAYMENT = com.netki.model.Payment(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = arrayListOf(
                Output(100, "Script"),
                Output(200, "Script")
            ),
            memo = MEMO
        )

        val PAYMENT_ACK = PaymentAck(
            payment = PAYMENT,
            memo = MEMO
        )
    }

}
