package com.netki.util

import com.netki.keymanagement.repo.data.AttestationResponse
import com.netki.keymanagement.repo.data.CertificateAttestationResponse
import com.netki.keymanagement.repo.data.CsrAttestation
import com.netki.model.*
import com.netki.security.CryptoModule
import com.netki.util.TestData.Attestations.INVALID_ATTESTATION
import com.netki.util.TestData.Encryption.ENCRYPTION_RECIPIENT
import com.netki.util.TestData.Encryption.ENCRYPTION_SENDER
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_CHAIN_ONE
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_CHAIN_TWO
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_CHAIN_TWO_BUNDLE
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_RANDOM
import com.netki.util.TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE
import com.netki.util.TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_TWO
import com.netki.util.TestData.KeyPairs.EV_CERT
import com.netki.util.TestData.Keys.generateKeyPairECDSA
import com.netki.util.TestData.Payment.Output.OUTPUTS
import com.netki.util.TestData.PkiData.PKI_DATA_ONE_OWNER_X509SHA256
import com.netki.util.TestData.PkiData.PKI_DATA_ONE_OWNER_X509SHA256_BUNDLE_CERTIFICATE
import com.netki.util.TestData.PkiData.PKI_DATA_ONE_OWNER_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.PkiData.PKI_DATA_OWNER_NONE
import com.netki.util.TestData.PkiData.PKI_DATA_SENDER_NONE
import com.netki.util.TestData.PkiData.PKI_DATA_SENDER_X509SHA256
import com.netki.util.TestData.PkiData.PKI_DATA_SENDER_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.PkiData.PKI_DATA_TWO_OWNER_X509SHA256
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.cert.X509ExtensionUtils
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.mockito.Mockito
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.time.Duration
import java.time.Instant
import java.util.*


internal object TestData {

    object Keys {
        const val HASH_ALGORITHM = "SHA256withRSA"
        fun generateKeyPair(): KeyPair {
            Security.addProvider(BouncyCastleProvider())
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
            keyPairGenerator.initialize(2048, SecureRandom())
            return keyPairGenerator.generateKeyPair()
        }

        fun generateKeyPairECDSA(): KeyPair {
            val parameters = AlgorithmParameters.getInstance("EC")
            parameters.init(ECGenParameterSpec("secp256k1"))
            val ecParameterSpec: ECParameterSpec = parameters.getParameterSpec(ECParameterSpec::class.java)
            val keyGen = KeyPairGenerator.getInstance("EC")
            keyGen.initialize(ecParameterSpec, SecureRandom())
            return keyGen.generateKeyPair()
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
            val publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.encoded)
            val digCalc = BcDigestCalculatorProvider().get(AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1))

            return X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo)
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

    object Payment {
        const val MEMO = "memo"
        const val MEMO_PAYMENT_ACK = "memo_payment_ack"

        val PAYMENT = Payment(
            merchantData = "merchant data",
            transactions = arrayListOf(
                "transaction1".toByteArray(),
                "transaction2".toByteArray()
            ),
            outputs = OUTPUTS,
            memo = MEMO,
            beneficiaries = listOf(
                Beneficiary(
                    isPrimaryForTransaction = true,
                    pkiDataSet = listOf(
                        PkiData(
                            attestation = Attestation.ADDRESS_COUNTRY,
                            certificatePem = CLIENT_CERTIFICATE_CHAIN_ONE,
                            type = PkiType.X509SHA256
                        )
                    )
                )
            ),
            originators = listOf(
                Originator(
                    isPrimaryForTransaction = true,
                    pkiDataSet = listOf(
                        PkiData(
                            attestation = Attestation.ADDRESS_COUNTRY,
                            certificatePem = CLIENT_CERTIFICATE_CHAIN_ONE,
                            type = PkiType.X509SHA256
                        )
                    )
                )
            ),
            protocolMessageMetadata = ProtocolMessageMetadata(
                1,
                StatusCode.OK,
                MessageType.PAYMENT,
                "",
                "randomIdentifier"
            )
        )

        object Output {
            val OUTPUTS = listOf(
                Output(1000, "Script 1", AddressCurrency.BITCOIN),
                Output(2000, "Script 2", AddressCurrency.BITCOIN)
            )
        }

        val PAYMENT_ACK = PaymentAck(
            payment = PAYMENT,
            memo = MEMO_PAYMENT_ACK,
            protocolMessageMetadata = ProtocolMessageMetadata(
                1,
                StatusCode.OK,
                MessageType.PAYMENT_ACK,
                "Ok",
                "randomIdentifier"
            )
        )
    }

    object KeyPairs {

        const val ROOT_CERTIFICATE_RANDOM = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDVzCCAj+gAwIBAgIEXkrkpDANBgkqhkiG9w0BAQsFADBeMQswCQYDVQQGEwJV\n" +
                "UzEOMAwGA1UECAwFU3RhdGUxDjAMBgNVBAcMBUxvY2FsMQ4wDAYDVQQKDAVOZXRr\n" +
                "aTEOMAwGA1UECwwFTmV0a2kxDzANBgNVBAMMBlJvb3RDQTAeFw0yMDAyMTcxOTA4\n" +
                "MjBaFw0yMTAyMTYxOTA4MjBaMF4xCzAJBgNVBAYTAlVTMQ4wDAYDVQQIDAVTdGF0\n" +
                "ZTEOMAwGA1UEBwwFTG9jYWwxDjAMBgNVBAoMBU5ldGtpMQ4wDAYDVQQLDAVOZXRr\n" +
                "aTEPMA0GA1UEAwwGUm9vdENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\n" +
                "AQEArXemLIgTbnNsJ+yhaBV34dBXPU+O69IO4WooiizUEk5oPaUyhGCr2zSig8wj\n" +
                "f1EzTmw+VWEOZA8JjAvrh1E7B5EVqwI3XGyox28IUYeFahA48Pk+3Kq5oJpTd3Mj\n" +
                "ebyZj86DfUvVsrKy1RBX7GUQVi8iYUngouMGkcTEcm4N84hBJTAoiasw0L3O5voN\n" +
                "jRzIUmuC7EKimHCEMriQO7Gh2JQJvuxy3EoQ94b5OGCkhI+lYaMWUPRqG7zJyzKL\n" +
                "QW6eFX4R2w40xBHVWbU3BkMLvpeN9Yg1c/lLATMidTnRyLahWd456Jv9NeAyhyTm\n" +
                "JCQacV4H6yKlJj45yFmj3yWTeQIDAQABox0wGzAMBgNVHRMEBTADAQH/MAsGA1Ud\n" +
                "DwQEAwIBBjANBgkqhkiG9w0BAQsFAAOCAQEAImZ3NJdAU1WBR74c5qcXu3C4XUUV\n" +
                "DZSgxRZlGt3+Vx2UvQOntb05V8UkK4nnLpVhU1AgnUQ0I008oNhm320Nu7s5gbHC\n" +
                "pgP6NKmTw9ENOsIZg4yFHOi0Ks35d1/SKaNOMI6zjSwxRTlfceRPpq8Htgpq8ntU\n" +
                "jM8NNErS/U4R1HgMGgMhUdt3i7Gr1vD1VlKWOZM2OhdkMeF2j8LHiTnNNT+cmjtE\n" +
                "NQyGKnsWgDyYDzvIcRkAWb0Tp7sfCXZLh0PAtIYSAbNKPnrqrbT2u/scCA6kgmOO\n" +
                "+J+lLOVZfu2zsBZQYp7DZz0iqkUfM8NC/5VoVTQzKNCJ2Sm2L+PYVBGYvQ==\n" +
                "-----END CERTIFICATE-----"

        const val INTERMEDIATE_CERTIFICATE_RANDOM = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDMjCCAhqgAwIBAgIEXkrqNjANBgkqhkiG9w0BAQsFADBeMQswCQYDVQQGEwJV\n" +
                "UzELMAkGA1UECAwCQ0ExCzAJBgNVBAcMAkxBMQ4wDAYDVQQKDAVuZXRraTEOMAwG\n" +
                "A1UECwwFTmV0a2kxFTATBgNVBAMMDEludGVybWVkaWF0ZTAeFw0yMDAyMTcxOTMy\n" +
                "MDZaFw0yMTAyMTYxOTMyMDZaMFgxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDWDEL\n" +
                "MAkGA1UEBwwCWEwxDTALBgNVBAoMBFRlc3QxDTALBgNVBAsMBFRlc3QxETAPBgNV\n" +
                "BAMMCFVzZXIgb25lMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzuj1\n" +
                "FaGB78Kx53imBvNU5BTDe+TnMiLmBXhuu99gsUKu5CCxcISSV0Z2FWLk2eyDcv53\n" +
                "OrxWGn5g6li7hYJvtS0sjeOSNoxJJW6AImLKVexQ7OXpglYLDUXkNFPL1+POpPxi\n" +
                "u5oo0iuQmBBsFoTljTk+UXdiy8x61GvlCjD7kFyRMbfiaKkH9pC7XBCOS6fxHsFN\n" +
                "Q2dQXvIpsHdt6Lf4QNxnbdW9sbLyHQInfdQS9C5FbhEDRxnLgEMYSzdi1A+Y5wp8\n" +
                "wN7Z7nZ/GYwuwDDGUvlO3yYIzVkxxh3xXpDQwfEzwtFPzVmDRPp/RZ8SmXWFIKJU\n" +
                "WtB7Xie43BjszztEMQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQB2uSOtCsb6f3jT\n" +
                "Eo/rQSFmocOlKHkMAdrDkqtn7dwg81uPaldfrIuQ/wSmNzefb2vd5VrHiFzSRv2/\n" +
                "pongarIEs1eVOnBvzJslPvfnf/T9busi5XSkX6a4UwBu/uZ3MqSRXg9IMZUiK/fY\n" +
                "guFAwocL77YvJ3f/U/QvfD6vZ4EqHPUf28lzBHfaYGxQ/Zq5hMEpf9yX+jQW54ZD\n" +
                "Yt0qvch/5tlL44OUnQmHTmAq6zw4A4InP7m0O+wVVMu00BdCBy3mhys7tQGFTpLr\n" +
                "DUtMM0E+/BBgbDiNRLo0zJVo/yw2AfHre3kxeUSfOCi6nS3xgnUgHIZq8g5+u0Df\n" +
                "0CIIF2F4\n" +
                "-----END CERTIFICATE-----"

        const val CLIENT_CERTIFICATE_RANDOM = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDODCCAiCgAwIBAgIEXkrp+zANBgkqhkiG9w0BAQsFADBeMQswCQYDVQQGEwJV\n" +
                "UzEOMAwGA1UECAwFU3RhdGUxDjAMBgNVBAcMBUxvY2FsMQ4wDAYDVQQKDAVOZXRr\n" +
                "aTEOMAwGA1UECwwFTmV0a2kxDzANBgNVBAMMBlJvb3RDQTAeFw0yMDAyMTcxOTMx\n" +
                "MDdaFw0yMTAyMTYxOTMxMDdaMF4xCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTEL\n" +
                "MAkGA1UEBwwCTEExDjAMBgNVBAoMBW5ldGtpMQ4wDAYDVQQLDAVOZXRraTEVMBMG\n" +
                "A1UEAwwMSW50ZXJtZWRpYXRlMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\n" +
                "AQEAzKJrRrpp87AeHPrBNLe6xJE347KzhQwmNubbgUtdLKkhM1CDVaYBIJH3w5yX\n" +
                "3YFy3QGoiLscfiNGCBT7770IKOE221xUvYZxvbJ7NW244yKmy+qqnMWgNIfnoYj9\n" +
                "ns1W2iWHlJ6PMtpGBx87bYjwOaAWIfO0imF/4pDm6ncqeIkGlUDBqRzbTvlT41SX\n" +
                "oadpKlckgeKo8g6CpRtmXC3ExLL7sr2kByrbnkmVD8Uuny/stnSFFm4MR6j673IA\n" +
                "pWykF3xJCj82NHiky+FiUUqgFkVfsyQNgmslj8rycPKUu4JJPghm21MO7Q5/jvZo\n" +
                "78Q6foIrYqDB7SobCRwROTg7pQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQClgqAR\n" +
                "DcIWmEsOwoUbdgcrhPR/OPOBlRPW69KFZ6WC5nJO6nZ0uN+f+pB75e/g2+p4YrYk\n" +
                "ZMauJyQbj3H9Aff8MN5G/zrHZLEiPeWj2Bub7jnYHjlIPU8r2mmZbhTFmZEqoBLe\n" +
                "1o3maTe9jk1B3uabZQA5MrkZjTG8ZXxALGmvKAmGqqpMvVyN/EEge4bjtwS5cK9E\n" +
                "WeCdur5Pw+N2P9UrPCd4MruOvRUBA3BJYOdFEwBs5C3+qze05n+mnOIhQZlahk+T\n" +
                "gk6jjkVPemLUkvvEoKwfGGbBvS8ypzUNdk38NzHhJQW6RPkq5lXRvlNsW/OBaBcb\n" +
                "YdVfDDGxbfz8wx8m\n" +
                "-----END CERTIFICATE-----"

        const val CLIENT_CERTIFICATE_CHAIN_ONE = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDfTCCAmWgAwIBAgIEXnQAUDANBgkqhkiG9w0BAQsFADCBjzELMAkGA1UEBhMC\n" +
                "T04xGDAWBgNVBAgMD0ludGVybWVkaWF0ZU9uZTEYMBYGA1UEBwwPSW50ZXJtZWRp\n" +
                "YXRlT25lMRgwFgYDVQQKDA9JbnRlcm1lZGlhdGVPbmUxGDAWBgNVBAsMD0ludGVy\n" +
                "bWVkaWF0ZU9uZTEYMBYGA1UEAwwPSW50ZXJtZWRpYXRlT25lMB4XDTIwMDMxOTIz\n" +
                "MjkyMFoXDTIxMDMxOTIzMjkyMFowcTELMAkGA1UEBhMCT04xEjAQBgNVBAgMCUNs\n" +
                "aWVudE9uZTESMBAGA1UEBwwJQ2xpZW50T25lMRIwEAYDVQQKDAlDbGllbnRPbmUx\n" +
                "EjAQBgNVBAsMCUNsaWVudE9uZTESMBAGA1UEAwwJQ2xpZW50T25lMIIBIjANBgkq\n" +
                "hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAugnsJpN9hhZNhy71wwPs0Zg70NAIErZs\n" +
                "ZwG7sJEpYhDRGd6NdgE6k9Zk3pWKrJXwUO0PU8N229vJnvLR9AyKboqiOihSPmIn\n" +
                "ye0eGoz3z53YUeNOAuFH77L2QNxooL1KhuucJBXHJsZ8C17zZTiOYj8fbBEWdhnX\n" +
                "sOhycluqViXodW+Teiyy//0y9hczv8jqEhYVvuMk3pcI1p/LTniQHLDRQkHQvCks\n" +
                "tvjDakezriy6YYtIqPGaaJ1X6dv6VP518oJQfNrHvezwUZyxuAKkOxSoLX10KISt\n" +
                "JqtpR3XyU4pbEEjkHbfTcahkzhoozJ2VNv/lJy7AEEEniAmBaZe8IQIDAQABMA0G\n" +
                "CSqGSIb3DQEBCwUAA4IBAQA3izgEbJtKGeoBB330R3INTw4zqCDsYGN/y9/jFU++\n" +
                "ituiKjQBYinDkIOs2neoyDNDIy7Cml1v5kD5P7jwO1QyaE1fKu+ZvND2trPBX4LA\n" +
                "b5kgibJTE/QM/YNj2sXBi6ZF6v/2eGmZBIX92fAJluqWcDhzHl6uCzsT72mEc9J9\n" +
                "Vop6imfwWoX/121m2he0wBA540xidEWMPbX2E1kzKQiDxCtuOZIGbNBJLRfMcRqJ\n" +
                "uM8fZSL3kJJIqTXRveKFyexmPNUljfvAQfkzGuQC6acMIQmwR/7YBido4gO4Ib2r\n" +
                "jiG80ehyepS2B16NaB0ckigkhZtpjeXYauadsvpH6Fro\n" +
                "-----END CERTIFICATE-----\n"

        const val CLIENT_PRIVATE_KEY_CHAIN_ONE = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC6Cewmk32GFk2H\n" +
                "LvXDA+zRmDvQ0AgStmxnAbuwkSliENEZ3o12ATqT1mTelYqslfBQ7Q9Tw3bb28me\n" +
                "8tH0DIpuiqI6KFI+YifJ7R4ajPfPndhR404C4UfvsvZA3GigvUqG65wkFccmxnwL\n" +
                "XvNlOI5iPx9sERZ2Gdew6HJyW6pWJeh1b5N6LLL//TL2FzO/yOoSFhW+4yTelwjW\n" +
                "n8tOeJAcsNFCQdC8KSy2+MNqR7OuLLphi0io8ZponVfp2/pU/nXyglB82se97PBR\n" +
                "nLG4AqQ7FKgtfXQohK0mq2lHdfJTilsQSOQdt9NxqGTOGijMnZU2/+UnLsAQQSeI\n" +
                "CYFpl7whAgMBAAECggEAFEvs1bCVq0FXp/35lhMhjSRcskVf/Bqm7P4FahgMOcS3\n" +
                "62iaaltr9qEXVClgfb/F/i4+09apawcpkgvP2B5eI/1AAbRQdLnkuWUDOcZTavU/\n" +
                "mn+ADVRissYFk8H4MEE2lk2yNUWi+poBAoSTbWGkNxfH59RdbPkYzRYvFkbl6Iv+\n" +
                "W5EYb1Gho16rLImgoOfD0Q1qBd2AXD3DTNwFrx+M7cxBn+33U4oBlFhIXbhzuxBW\n" +
                "BXcKsm3zm/wggqdomRJt7Vr5NHflXIWM9nP+YxDrNTEB7+ZlvpAqTDl3k7bYRiCW\n" +
                "ZS5g64U2Zu5rC5gs401RwXIHCwWyOgYgaPQeol7tOQKBgQDxGEXxaa5X2bRbq+bw\n" +
                "VFyzWCsuAR6gYw9d3lnrKpNyA0wUHimlyDdlFs27xRcz/7zAVDg39X3LfXuG3LM8\n" +
                "pHuZ1/QeeCVxqAB2L2Mb4kGlmteCb5xh+WpzAInaoCMxsGZFIc7P24YBJn6mdQB9\n" +
                "M099JVjUkF9X8W17meRiYIwfbwKBgQDFikuloUiLPArhV+ic+45qr0aGhdvIO7jz\n" +
                "akw6rw64XfOwmlWiadX0p57LkNLIc+5TpCpQc43xppzAH7VoGarID6ODvll4A1gx\n" +
                "zx/9s2geaoZcxqzPRHHqplGKvpS0SSuDpNXy9tublGOanXFNPNnoR13dEKlELtq+\n" +
                "y9Nb9N4VbwKBgQCsJ6YB/XGVn4nvD6/HGqZbFfE3V2tUIYgegiB5ERzaA8q2btdU\n" +
                "XsRXddIQa2rnIYzZVQoTw0NBI+gp47xE6DquHwtdGnO6VbmGqs29YnF33DpZFHN5\n" +
                "bkz5s3+8Ui7vU0Ojx8FSoTFt7tvu5osj25i+BwYIOtMqC+YepUP0j3ZfFwKBgBuD\n" +
                "b5XaKOh7rGhGfjefMe7aCtChxELXTqNYotVpnHtBWre2R0cfxpUU46EmwrT4sLEl\n" +
                "pF8gORz3P83inLmrGYZT50pqMLvue1I0rxf+7PmPjLdPVLJprhQopiLU+JFDv7PO\n" +
                "OZ5lk6DPwi++zhEb8J3Rktk/gNPmUsFQUlf0exoxAoGBANHXbF04uRA9W7z7wEke\n" +
                "a99zEZ2Kqhx/td8kD+BIIBzCM3sZa3K0X1CkimzPbcKc7CHC/m71zoUuIzPIhInR\n" +
                "+8tKJBCl9JZy8EA6/C7MPUn1/vgHNu5kl953bUP87f/2MAw7thUtIQ8bel/NfxOc\n" +
                "HQXlAbM9ti7vD4xl3iK1uDfQ\n" +
                "-----END PRIVATE KEY-----\n"

        const val CLIENT_CERTIFICATE_CHAIN_TWO = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDfTCCAmWgAwIBAgIEXnQF+TANBgkqhkiG9w0BAQsFADCBjzELMAkGA1UEBhMC\n" +
                "VFcxGDAWBgNVBAgMD0ludGVybWVkaWF0ZVR3bzEYMBYGA1UEBwwPSW50ZXJtZWRp\n" +
                "YXRlVHdvMRgwFgYDVQQKDA9JbnRlcm1lZGlhdGVUd28xGDAWBgNVBAsMD0ludGVy\n" +
                "bWVkaWF0ZVR3bzEYMBYGA1UEAwwPSW50ZXJtZWRpYXRlVHdvMB4XDTIwMDMxOTIz\n" +
                "NTMyOVoXDTIxMDMxOTIzNTMyOVowcTELMAkGA1UEBhMCVFcxEjAQBgNVBAgMCUNs\n" +
                "aWVudFR3bzESMBAGA1UEBwwJQ2xpZW50VHdvMRIwEAYDVQQKDAlDbGllbnRUd28x\n" +
                "EjAQBgNVBAsMCUNsaWVudFR3bzESMBAGA1UEAwwJQ2xpZW50VHdvMIIBIjANBgkq\n" +
                "hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjZS7RaWbKl/daUEPSqOk6JPjnKV84LAf\n" +
                "Sw+EhvdRxO324FgvjhWfWnXMyehPzaM53qahJmtDQTg6qdi1PmikoM2dZLypzYik\n" +
                "xJTvK+Siot/uPny5Chtt7w079ejOoEKem7tZVo7fJwp6BfvObXLt6BRJdXX20Mmy\n" +
                "0zJH6sDGL0EZ/qJgvT3y1jdgnF/vOiqfwouCeCqtzntdEh9ryYrfiDY7bmFm8Imu\n" +
                "2q7d50PuB2o0dq4oFUt6AML6mSgKmS2ihKCKdifvQHzdySsvIMPMTqW4KViFTxiu\n" +
                "V+We412BNHvxUUm5WBozEutzmKpLV2R5+ckvbvsLQ9U1L6DA6nf2jQIDAQABMA0G\n" +
                "CSqGSIb3DQEBCwUAA4IBAQB+Dfgw9N7jXsrnisk+qOH9p82FjMHyc0/R6W6PFdU2\n" +
                "3BXWhvQTZmkDui046odoa/UB7+ia+7q3xWVYjGR898pFSkqXAcy7GMQ6+ueMFo1W\n" +
                "Qdxg8daY5hUGJxvw6XP9POM9nABYdbmm1xyi9wg2vULoGtVfK0VSEDCFmEzcxYzO\n" +
                "Q+Cb6Zr637DKZm1JRPloIORkIewKSc9vBw9W2IEzlgfT3fVAgmBmFlJhfn9OkdDF\n" +
                "RPDatFu5D271ai0cZgp2nJ0OajqCi37czy5fF2KZ/RMg7sj5PwRcnLKgaieYck+Z\n" +
                "JcbgrTguZRQi0GI2V24OiEzuGRLxQIgHuFBHUR7B820f\n" +
                "-----END CERTIFICATE-----\n"

        const val CLIENT_PRIVATE_KEY_CHAIN_TWO = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNlLtFpZsqX91p\n" +
                "QQ9Ko6Tok+OcpXzgsB9LD4SG91HE7fbgWC+OFZ9adczJ6E/NoznepqEma0NBODqp\n" +
                "2LU+aKSgzZ1kvKnNiKTElO8r5KKi3+4+fLkKG23vDTv16M6gQp6bu1lWjt8nCnoF\n" +
                "+85tcu3oFEl1dfbQybLTMkfqwMYvQRn+omC9PfLWN2CcX+86Kp/Ci4J4Kq3Oe10S\n" +
                "H2vJit+INjtuYWbwia7art3nQ+4HajR2rigVS3oAwvqZKAqZLaKEoIp2J+9AfN3J\n" +
                "Ky8gw8xOpbgpWIVPGK5X5Z7jXYE0e/FRSblYGjMS63OYqktXZHn5yS9u+wtD1TUv\n" +
                "oMDqd/aNAgMBAAECggEAIiI0ySyNPXjZyFW5YlEUQcdf5YUHV6NWlBHkbWakleIa\n" +
                "NknEg1Cmt9g1PItv6/+5hLqvGPRcxVVRXWAECE0Rvbv8wYvzszwJn2RZyj8Hz9VF\n" +
                "mtaWhP+KcEsERPvxDvWoyBpxxjrRRZgSxa0I/l2qSlzTvggn7nvmS2EwsgHydfMz\n" +
                "/P1u1ZddbaYPj7U/tORfdXWnwHBbDpMs2mTL/yybrOfkf3d1sYD8qYiZcK0ALULn\n" +
                "wpsgpxJulQKQTuNdkot8G/KvmZCdhHYS/gs+StK1hOmk+8G6LzyE2FaNAWH1u/6D\n" +
                "+oxJMl1ow86fpY29c27Tkkm528YgQiljcdGfAaBq+QKBgQDFh/7reoZgjEQyjqsW\n" +
                "coQaagbLAg7ST+g8/GbBWlhxpRSxCEabi3GpV8UPEv2rg7LVzNTXJ97pM4pUUGz6\n" +
                "iEQgtEvxkiCuxT+Hce99D+HhtgSOdq56cX2n8/7QMM82HVHSgBbyBpOqGjBpz3AY\n" +
                "zyVBtTbGPvkJb3UGciCrn02VmQKBgQC3fRWQv1RgckcOQJGF7mrMM3dNQgqDT4UA\n" +
                "jdnG4ckvjYRgRWrRqm/IUrpBMlRh6xsQ6qr8Ts1spKX/Yp+JJamKxEJP63MUq1GA\n" +
                "ctR3JjumtOqDI97C9NxSpxHfHZKKXHIJ5iXM93BsyMP9oJ5AG18LiBHgOLqdh188\n" +
                "sOsOtyzZFQKBgD3719VipEolmbzXof4wPx3eyXTol2gNZQ3GEiR4SiqXJ7AJrcZf\n" +
                "cnI2NYLubaVldTe7x8ogG8XHw4+DkT7ohaBRk0chmJnfEXlaGlF/K11ddX6S5VtM\n" +
                "w6ZxXTNNLaiIeMV6JjkaMTn+b9S0IDPYxJMi3yZEWndIf0tfgrr4CSt5AoGABHzv\n" +
                "wRmc87r31/ZmWNNLE3GS0nXyEeIpC6lskTvGkv4wJbas9THpCApV+fBENhztDY3f\n" +
                "3soCpkykrsl3w4ADVJyWTqQgrXm/RZgJcFykCuDT958x/KzGktL5Ue7EPdQjCfDy\n" +
                "LcBDpLWIbbS3CjRhL8QFQ+m/TskX4EEnjrWWSD0CgYEApoVwVTAS4Wv4wXSeeBmA\n" +
                "2SgORVnnmrhT5ahd8g1s4tAD295Slnlp486Qhi9fKQpbQOw0TogbSQGalUc319/3\n" +
                "UWlNlu5Eu6TuAdCmY3KJNrnCERq+AJ+t2fakA5CL53EzpDmXDPmDKS7iEaOOXw2M\n" +
                "UM5JZTOfmyf5CwE2lNBZryI=\n" +
                "-----END PRIVATE KEY-----\n"

        const val CLIENT_CERTIFICATE_CHAIN_TWO_BUNDLE = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDkjCCAnqgAwIBAgIEXnQF2jANBgkqhkiG9w0BAQsFADBnMQswCQYDVQQGEwJU\n" +
                "VzEQMA4GA1UECAwHUm9vdFR3bzEQMA4GA1UEBwwHUm9vdFR3bzEQMA4GA1UECgwH\n" +
                "Um9vdFR3bzEQMA4GA1UECwwHUm9vdFR3bzEQMA4GA1UEAwwHUm9vdFR3bzAeFw0y\n" +
                "MDAzMTkyMzUyNThaFw0yMTAzMTkyMzUyNThaMIGPMQswCQYDVQQGEwJUVzEYMBYG\n" +
                "A1UECAwPSW50ZXJtZWRpYXRlVHdvMRgwFgYDVQQHDA9JbnRlcm1lZGlhdGVUd28x\n" +
                "GDAWBgNVBAoMD0ludGVybWVkaWF0ZVR3bzEYMBYGA1UECwwPSW50ZXJtZWRpYXRl\n" +
                "VHdvMRgwFgYDVQQDDA9JbnRlcm1lZGlhdGVUd28wggEiMA0GCSqGSIb3DQEBAQUA\n" +
                "A4IBDwAwggEKAoIBAQDVJEP4dvW0xHOeMB0MRygxXOm4hEVE+A37BC3bxNgiJ1Fs\n" +
                "4s64C04WozcGnbN299tAe8WQtz4pF6f1GO7747YaxPOYRPYF9lmdYml6VGked44H\n" +
                "2/dbREDQzY8ky5oBNzuXe//rrFBRr7TIBycWM8a8f8zqkp+qRNDDMyzGMLRpz1V4\n" +
                "JhnOOZIeGHz18r9VD+EHJqX3XTBLmaM4LpEmu96BzVtfIRaFaHSn3uaki/xTGbs1\n" +
                "FzjST9PBfRdjdOT07ggpVi0SDSeGMZksZaPxVB8pdhlg28tXC6Jg8iiJ9oRZbD27\n" +
                "kJxjSxjMasSKm4y9jGyWZXP8ykk1vUaOceE+I5T5AgMBAAGjHTAbMAwGA1UdEwQF\n" +
                "MAMBAf8wCwYDVR0PBAQDAgEGMA0GCSqGSIb3DQEBCwUAA4IBAQBjEBeablJOLo+8\n" +
                "2f+FCvM4Bm7xGR6rZEkWWva6hW5IUF63V3/a5dOyLbhTlOMYC24DnKL79oofFQHC\n" +
                "Ow2efBBn17wd3YaY7wcXda7fKZSxnzVnJslkPUBap9HwhCE8Betqu1kGZ8/5O9g7\n" +
                "7z79MqA5hIy42RYwK24Ha4TGqWyrzdsc9M+RBox20Gxsl7GY2wYN43GNgL224AYu\n" +
                "RfTeb0eTdQmyrusy4my732tAbodeiCQWwxQb/x8p2TbS7prPv7YKN0HjZjsGett6\n" +
                "Q77cTzYd5oAWSmNC7w9ujS5MwCfb/fYhYV116iiYSps+85TFDT2M2llxjnAZiIe7\n" +
                "+3W2KK5G\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDfTCCAmWgAwIBAgIEXnQF+TANBgkqhkiG9w0BAQsFADCBjzELMAkGA1UEBhMC\n" +
                "VFcxGDAWBgNVBAgMD0ludGVybWVkaWF0ZVR3bzEYMBYGA1UEBwwPSW50ZXJtZWRp\n" +
                "YXRlVHdvMRgwFgYDVQQKDA9JbnRlcm1lZGlhdGVUd28xGDAWBgNVBAsMD0ludGVy\n" +
                "bWVkaWF0ZVR3bzEYMBYGA1UEAwwPSW50ZXJtZWRpYXRlVHdvMB4XDTIwMDMxOTIz\n" +
                "NTMyOVoXDTIxMDMxOTIzNTMyOVowcTELMAkGA1UEBhMCVFcxEjAQBgNVBAgMCUNs\n" +
                "aWVudFR3bzESMBAGA1UEBwwJQ2xpZW50VHdvMRIwEAYDVQQKDAlDbGllbnRUd28x\n" +
                "EjAQBgNVBAsMCUNsaWVudFR3bzESMBAGA1UEAwwJQ2xpZW50VHdvMIIBIjANBgkq\n" +
                "hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjZS7RaWbKl/daUEPSqOk6JPjnKV84LAf\n" +
                "Sw+EhvdRxO324FgvjhWfWnXMyehPzaM53qahJmtDQTg6qdi1PmikoM2dZLypzYik\n" +
                "xJTvK+Siot/uPny5Chtt7w079ejOoEKem7tZVo7fJwp6BfvObXLt6BRJdXX20Mmy\n" +
                "0zJH6sDGL0EZ/qJgvT3y1jdgnF/vOiqfwouCeCqtzntdEh9ryYrfiDY7bmFm8Imu\n" +
                "2q7d50PuB2o0dq4oFUt6AML6mSgKmS2ihKCKdifvQHzdySsvIMPMTqW4KViFTxiu\n" +
                "V+We412BNHvxUUm5WBozEutzmKpLV2R5+ckvbvsLQ9U1L6DA6nf2jQIDAQABMA0G\n" +
                "CSqGSIb3DQEBCwUAA4IBAQB+Dfgw9N7jXsrnisk+qOH9p82FjMHyc0/R6W6PFdU2\n" +
                "3BXWhvQTZmkDui046odoa/UB7+ia+7q3xWVYjGR898pFSkqXAcy7GMQ6+ueMFo1W\n" +
                "Qdxg8daY5hUGJxvw6XP9POM9nABYdbmm1xyi9wg2vULoGtVfK0VSEDCFmEzcxYzO\n" +
                "Q+Cb6Zr637DKZm1JRPloIORkIewKSc9vBw9W2IEzlgfT3fVAgmBmFlJhfn9OkdDF\n" +
                "RPDatFu5D271ai0cZgp2nJ0OajqCi37czy5fF2KZ/RMg7sj5PwRcnLKgaieYck+Z\n" +
                "JcbgrTguZRQi0GI2V24OiEzuGRLxQIgHuFBHUR7B820f\n" +
                "-----END CERTIFICATE-----\n"

        const val CLIENT_CERTIFICATE_CHAIN_THREE_BUNDLE = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDaTCCAlGgAwIBAgIEXqC34zANBgkqhkiG9w0BAQsFADCBijELMAkGA1UEBhMC\n" +
                "TVgxFzAVBgNVBAgMDkludGVybWVkaWF0ZTNiMRcwFQYDVQQHDA5JbnRlcm1lZGlh\n" +
                "dGUzYjEXMBUGA1UECgwOSW50ZXJtZWRpYXRlM2IxFzAVBgNVBAsMDkludGVybWVk\n" +
                "aWF0ZTNiMRcwFQYDVQQDDA5JbnRlcm1lZGlhdGUzYjAeFw0yMDA0MjIyMTMyMTla\n" +
                "Fw0yMTA0MjIyMTMyMTlaMGIxCzAJBgNVBAYTAk1YMQ8wDQYDVQQIDAZGaW5hbDMx\n" +
                "DzANBgNVBAcMBkZpbmFsMzEPMA0GA1UECgwGRmluYWwzMQ8wDQYDVQQLDAZGaW5h\n" +
                "bDMxDzANBgNVBAMMBkZpbmFsMzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
                "ggEBALKUsw2m91fjMUI/kggbnso72SPR7JHbqxd5glMFt0cnnVf39AwKvb2jJl5o\n" +
                "W6xcMBVwEjwjk9qv7pvPctvhM0aZerIpmG3rz1nHsBhC1P5z/DyWa4ETLtO3qJaX\n" +
                "UZaTDeV3F0I+YjETMdAeOQLfGjBnOpowa0ODom7GjdhajB134GtTgdZeOz2B6stR\n" +
                "w9X2e83v8wI3OyaYunUqJ6jeF/8rE7W6vfGAMHdgEImWLQsL31gU0amzlqcjiv6r\n" +
                "nkYRqBsmsLnOaNfIAMsuyYDgsoAdRu3ripYhfRRTEVWjMYshD/05VP21lL05MyiC\n" +
                "F/W4VIW86BYydQOipLRG3VA/sDcCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAS+v+\n" +
                "rRgrkO4295/rj8Q9xROPWErWxH1INal9Z9wR5kGuBlPnpLdCIMUe921PaqaunprA\n" +
                "nRqnxTKT8gNWpFJkpHZh8sJpU1si7JLdckPzWqIWkAPy0a0DM7s0uOlhpr3Xx17O\n" +
                "WpXQVP3RNuz4Bl7FR57/1CE0xTsFJ7/ESB6etyxINyKws8HVCc0A/ZMXZC/WUY0p\n" +
                "i+oi3ESo8azLcBwrR18oK8laYlI/mYoyFCZaSZa3Zy1zCwc/odrjKFF+oUtTclB2\n" +
                "PFJMx1+ZokGokXph+HhwTGu3foz7Of1SOAzEtQhgmNOPMTjzFJX6Z4e3AP5z1CE6\n" +
                "fKJlFK0XMRElxl4VlA==\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDsTCCApmgAwIBAgIEXqC3EjANBgkqhkiG9w0BAQsFADCBijELMAkGA1UEBhMC\n" +
                "bXgxFzAVBgNVBAgMDkludGVybWVkaWF0ZTNhMRcwFQYDVQQHDA5JbnRlcm1lZGlh\n" +
                "dGUzYTEXMBUGA1UECgwOSW50ZXJtZWRpYXRlM2ExFzAVBgNVBAsMDkludGVybWVk\n" +
                "aWF0ZTNhMRcwFQYDVQQDDA5JbnRlcm1lZGlhdGUzYTAeFw0yMDA0MjIyMTI4NTBa\n" +
                "Fw0yMTA0MjIyMTI4NTBaMIGKMQswCQYDVQQGEwJNWDEXMBUGA1UECAwOSW50ZXJt\n" +
                "ZWRpYXRlM2IxFzAVBgNVBAcMDkludGVybWVkaWF0ZTNiMRcwFQYDVQQKDA5JbnRl\n" +
                "cm1lZGlhdGUzYjEXMBUGA1UECwwOSW50ZXJtZWRpYXRlM2IxFzAVBgNVBAMMDklu\n" +
                "dGVybWVkaWF0ZTNiMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtQ79\n" +
                "EL0bre8/AiWMlC014eIml7nXEDJYaQw+rneUJpWvREY56dLvDapmttnQfFBYw2CP\n" +
                "9OLi6tB7EcBG5V5ip26o44UGsjDjGFSDR1SOG1vcNHnbAHBjMOiwIBIIO4SMtdCj\n" +
                "4L8c9I6q2G0kSHTIcbjTIRGRD8KhOpIEYH/49/9ia4Q+ZOPkntkPV2vssxhouVRG\n" +
                "QBhDdq+zgntetQ5MJ7Lh8wLLAjs9TqdPT06lyyqgM7WJKF3xT09QhehwJR4ICzTN\n" +
                "rMVC1X+jJDSr7uh6m0e9mJ45WWuL6kAfCNAMa3NKh3MSxDwGrObfbZU60u4j+Src\n" +
                "5UJ9+Dq7rFONSkrJWQIDAQABox0wGzAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIB\n" +
                "BjANBgkqhkiG9w0BAQsFAAOCAQEAWK8jWuDij/tRLAmWx6t/5NQVBVhzzQRuC9UU\n" +
                "NCZN/HSymJpudRMeY+aeHHA0M/OkhVL1+MZPHrLVAqxHeDkQQAjxV8pidoDNDOCt\n" +
                "ImUqVA89eYNanDw2V7AXFikPznILDoSrN+r/EhdhhKG61dor3u75prnokiO494Hn\n" +
                "+7s03TTBFBmnAMbPwBlVjdm7wwLQejVU8iUmqCUj4IToOnFzrViUoJVosdqKXnVC\n" +
                "9a2qAcXtyj1rDQGRcVPzsW0HrjNL/9m8d8orAwAi8GkmEolTNbSCpeenxP8dRSUJ\n" +
                "TIG2KZxFqEDzO0Fc5sV4sUgttEWFHMyzgFRojwxf0UUB7aZsXg==\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDgzCCAmugAwIBAgIEXqC21zANBgkqhkiG9w0BAQsFADBdMQswCQYDVQQGEwJN\n" +
                "WDEOMAwGA1UECAwFUm9vdDMxDjAMBgNVBAcMBVJvb3QzMQ4wDAYDVQQKDAVSb290\n" +
                "MzEOMAwGA1UECwwFUm9vdDMxDjAMBgNVBAMMBVJvb3QzMB4XDTIwMDQyMjIxMjc1\n" +
                "MVoXDTIxMDQyMjIxMjc1MVowgYoxCzAJBgNVBAYTAm14MRcwFQYDVQQIDA5JbnRl\n" +
                "cm1lZGlhdGUzYTEXMBUGA1UEBwwOSW50ZXJtZWRpYXRlM2ExFzAVBgNVBAoMDklu\n" +
                "dGVybWVkaWF0ZTNhMRcwFQYDVQQLDA5JbnRlcm1lZGlhdGUzYTEXMBUGA1UEAwwO\n" +
                "SW50ZXJtZWRpYXRlM2EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDb\n" +
                "TXkth8ikvnt9skMmRHTj8PTP8lvECKzMCudakNumsHW2UimMpT8NvwOYr5avrL2U\n" +
                "h1z+VFHPjaFSEpWI0q5e6lf8Ezesg5+zwOQg1MhucWBchxO6uoGzkVpbFj0qqyNq\n" +
                "caAr9yrd05umFbs/UvDT+xB8g04xnjo1jlPCDppyY3FEYw9Gkl3BsgmsBTgD2Qyc\n" +
                "yCUBD88z+wJSBMfnQlioMf75w36s/4Ql1fwDJm3HpW8yxRsaEzA0r7kjKMSfPybI\n" +
                "C3MtpHbKUrkU4ZRpS+MA0MpF3Ig1OY+8pgPN2Z0o3FBI3UeRrcSyzfr37oE/0yA3\n" +
                "tIVBWhrvsDN76ECLKRG/AgMBAAGjHTAbMAwGA1UdEwQFMAMBAf8wCwYDVR0PBAQD\n" +
                "AgEGMA0GCSqGSIb3DQEBCwUAA4IBAQB85UgdUiFjWxE53jWO4YMHqCEK8uu327TK\n" +
                "fOVWqzTwUWvTHEHpu2HL+cO+eVpbUv82l8BE9glZytbaDw7TYRmkcuCa0BrgK8fT\n" +
                "FdpCW2qh7zDPxFtbiQ6f4YBEaJgqGcIxAEV40mX8LL9Now81c6abWMEOOCXeZH8I\n" +
                "umc7Vp9GRzKE9CH5jIn00aqmD5GXX+ncc9Xku2xv1RgHUNuNwTblnO7AJOzDXsFw\n" +
                "NqLbSdigV03VPdTBWp0xfLAxQkWKQ4rFtZqVcpOIcMQzq2PjyHw3bqgoUIc3E50q\n" +
                "wmtEx1ViS0uRnTC3dPILqjWs/01WiPoYoTQR/l1jtK8zwx9RRHpC\n" +
                "-----END CERTIFICATE-----"

        const val CLIENT_CERTIFICATE_EXPIRED = "-----BEGIN CERTIFICATE-----\n" +
                "MIIFSzCCBDOgAwIBAgIQSueVSfqavj8QDxekeOFpCTANBgkqhkiG9w0BAQsFADCB\n" +
                "kDELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n" +
                "A1UEBxMHU2FsZm9yZDEaMBgGA1UEChMRQ09NT0RPIENBIExpbWl0ZWQxNjA0BgNV\n" +
                "BAMTLUNPTU9ETyBSU0EgRG9tYWluIFZhbGlkYXRpb24gU2VjdXJlIFNlcnZlciBD\n" +
                "QTAeFw0xNTA0MDkwMDAwMDBaFw0xNTA0MTIyMzU5NTlaMFkxITAfBgNVBAsTGERv\n" +
                "bWFpbiBDb250cm9sIFZhbGlkYXRlZDEdMBsGA1UECxMUUG9zaXRpdmVTU0wgV2ls\n" +
                "ZGNhcmQxFTATBgNVBAMUDCouYmFkc3NsLmNvbTCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                "ggEPADCCAQoCggEBAMIE7PiM7gTCs9hQ1XBYzJMY61yoaEmwIrX5lZ6xKyx2PmzA\n" +
                "S2BMTOqytMAPgLaw+XLJhgL5XEFdEyt/ccRLvOmULlA3pmccYYz2QULFRtMWhyef\n" +
                "dOsKnRFSJiFzbIRMeVXk0WvoBj1IFVKtsyjbqv9u/2CVSndrOfEk0TG23U3AxPxT\n" +
                "uW1CrbV8/q71FdIzSOciccfCFHpsKOo3St/qbLVytH5aohbcabFXRNsKEqveww9H\n" +
                "dFxBIuGa+RuT5q0iBikusbpJHAwnnqP7i/dAcgCskgjZjFeEU4EFy+b+a1SYQCeF\n" +
                "xxC7c3DvaRhBB0VVfPlkPz0sw6l865MaTIbRyoUCAwEAAaOCAdUwggHRMB8GA1Ud\n" +
                "IwQYMBaAFJCvajqUWgvYkOoSVnPfQ7Q6KNrnMB0GA1UdDgQWBBSd7sF7gQs6R2lx\n" +
                "GH0RN5O8pRs/+zAOBgNVHQ8BAf8EBAMCBaAwDAYDVR0TAQH/BAIwADAdBgNVHSUE\n" +
                "FjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwTwYDVR0gBEgwRjA6BgsrBgEEAbIxAQIC\n" +
                "BzArMCkGCCsGAQUFBwIBFh1odHRwczovL3NlY3VyZS5jb21vZG8uY29tL0NQUzAI\n" +
                "BgZngQwBAgEwVAYDVR0fBE0wSzBJoEegRYZDaHR0cDovL2NybC5jb21vZG9jYS5j\n" +
                "b20vQ09NT0RPUlNBRG9tYWluVmFsaWRhdGlvblNlY3VyZVNlcnZlckNBLmNybDCB\n" +
                "hQYIKwYBBQUHAQEEeTB3ME8GCCsGAQUFBzAChkNodHRwOi8vY3J0LmNvbW9kb2Nh\n" +
                "LmNvbS9DT01PRE9SU0FEb21haW5WYWxpZGF0aW9uU2VjdXJlU2VydmVyQ0EuY3J0\n" +
                "MCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5jb21vZG9jYS5jb20wIwYDVR0RBBww\n" +
                "GoIMKi5iYWRzc2wuY29tggpiYWRzc2wuY29tMA0GCSqGSIb3DQEBCwUAA4IBAQBq\n" +
                "evHa/wMHcnjFZqFPRkMOXxQhjHUa6zbgH6QQFezaMyV8O7UKxwE4PSf9WNnM6i1p\n" +
                "OXy+l+8L1gtY54x/v7NMHfO3kICmNnwUW+wHLQI+G1tjWxWrAPofOxkt3+IjEBEH\n" +
                "fnJ/4r+3ABuYLyw/zoWaJ4wQIghBK4o+gk783SHGVnRwpDTysUCeK1iiWQ8dSO/r\n" +
                "ET7BSp68ZVVtxqPv1dSWzfGuJ/ekVxQ8lEEFeouhN0fX9X3c+s5vMaKwjOrMEpsi\n" +
                "8TRwz311SotoKQwe6Zaoz7ASH1wq7mcvf71z81oBIgxw+s1F73hczg36TuHvzmWf\n" +
                "RwxPuzZEaFZcVlmtqoq8\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIGCDCCA/CgAwIBAgIQKy5u6tl1NmwUim7bo3yMBzANBgkqhkiG9w0BAQwFADCB\n" +
                "hTELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n" +
                "A1UEBxMHU2FsZm9yZDEaMBgGA1UEChMRQ09NT0RPIENBIExpbWl0ZWQxKzApBgNV\n" +
                "BAMTIkNPTU9ETyBSU0EgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTQwMjEy\n" +
                "MDAwMDAwWhcNMjkwMjExMjM1OTU5WjCBkDELMAkGA1UEBhMCR0IxGzAZBgNVBAgT\n" +
                "EkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4GA1UEBxMHU2FsZm9yZDEaMBgGA1UEChMR\n" +
                "Q09NT0RPIENBIExpbWl0ZWQxNjA0BgNVBAMTLUNPTU9ETyBSU0EgRG9tYWluIFZh\n" +
                "bGlkYXRpb24gU2VjdXJlIFNlcnZlciBDQTCCASIwDQYJKoZIhvcNAQEBBQADggEP\n" +
                "ADCCAQoCggEBAI7CAhnhoFmk6zg1jSz9AdDTScBkxwtiBUUWOqigwAwCfx3M28Sh\n" +
                "bXcDow+G+eMGnD4LgYqbSRutA776S9uMIO3Vzl5ljj4Nr0zCsLdFXlIvNN5IJGS0\n" +
                "Qa4Al/e+Z96e0HqnU4A7fK31llVvl0cKfIWLIpeNs4TgllfQcBhglo/uLQeTnaG6\n" +
                "ytHNe+nEKpooIZFNb5JPJaXyejXdJtxGpdCsWTWM/06RQ1A/WZMebFEh7lgUq/51\n" +
                "UHg+TLAchhP6a5i84DuUHoVS3AOTJBhuyydRReZw3iVDpA3hSqXttn7IzW3uLh0n\n" +
                "c13cRTCAquOyQQuvvUSH2rnlG51/ruWFgqUCAwEAAaOCAWUwggFhMB8GA1UdIwQY\n" +
                "MBaAFLuvfgI9+qbxPISOre44mOzZMjLUMB0GA1UdDgQWBBSQr2o6lFoL2JDqElZz\n" +
                "30O0Oija5zAOBgNVHQ8BAf8EBAMCAYYwEgYDVR0TAQH/BAgwBgEB/wIBADAdBgNV\n" +
                "HSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwGwYDVR0gBBQwEjAGBgRVHSAAMAgG\n" +
                "BmeBDAECATBMBgNVHR8ERTBDMEGgP6A9hjtodHRwOi8vY3JsLmNvbW9kb2NhLmNv\n" +
                "bS9DT01PRE9SU0FDZXJ0aWZpY2F0aW9uQXV0aG9yaXR5LmNybDBxBggrBgEFBQcB\n" +
                "AQRlMGMwOwYIKwYBBQUHMAKGL2h0dHA6Ly9jcnQuY29tb2RvY2EuY29tL0NPTU9E\n" +
                "T1JTQUFkZFRydXN0Q0EuY3J0MCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5jb21v\n" +
                "ZG9jYS5jb20wDQYJKoZIhvcNAQEMBQADggIBAE4rdk+SHGI2ibp3wScF9BzWRJ2p\n" +
                "mj6q1WZmAT7qSeaiNbz69t2Vjpk1mA42GHWx3d1Qcnyu3HeIzg/3kCDKo2cuH1Z/\n" +
                "e+FE6kKVxF0NAVBGFfKBiVlsit2M8RKhjTpCipj4SzR7JzsItG8kO3KdY3RYPBps\n" +
                "P0/HEZrIqPW1N+8QRcZs2eBelSaz662jue5/DJpmNXMyYE7l3YphLG5SEXdoltMY\n" +
                "dVEVABt0iN3hxzgEQyjpFv3ZBdRdRydg1vs4O2xyopT4Qhrf7W8GjEXCBgCq5Ojc\n" +
                "2bXhc3js9iPc0d1sjhqPpepUfJa3w/5Vjo1JXvxku88+vZbrac2/4EjxYoIQ5QxG\n" +
                "V/Iz2tDIY+3GH5QFlkoakdH368+PUq4NCNk+qKBR6cGHdNXJ93SrLlP7u3r7l+L4\n" +
                "HyaPs9Kg4DdbKDsx5Q5XLVq4rXmsXiBmGqW5prU5wfWYQ//u+aen/e7KJD2AFsQX\n" +
                "j4rBYKEMrltDR5FL1ZoXX/nUh8HCjLfn4g8wGTeGrODcQgPmlKidrv0PJFGUzpII\n" +
                "0fxQ8ANAe4hZ7Q7drNJ3gjTcBpUC2JD5Leo31Rpg0Gcg19hCC0Wvgmje3WYkN5Ap\n" +
                "lBlGGSW4gNfL1IYoakRwJiNiqZ+Gb7+6kHDSVneFeO/qJakXzlByjAA6quPbYzSf\n" +
                "+AZxAeKCINT+b72x\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIF2DCCA8CgAwIBAgIQTKr5yttjb+Af907YWwOGnTANBgkqhkiG9w0BAQwFADCB\n" +
                "hTELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n" +
                "A1UEBxMHU2FsZm9yZDEaMBgGA1UEChMRQ09NT0RPIENBIExpbWl0ZWQxKzApBgNV\n" +
                "BAMTIkNPTU9ETyBSU0EgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTAwMTE5\n" +
                "MDAwMDAwWhcNMzgwMTE4MjM1OTU5WjCBhTELMAkGA1UEBhMCR0IxGzAZBgNVBAgT\n" +
                "EkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4GA1UEBxMHU2FsZm9yZDEaMBgGA1UEChMR\n" +
                "Q09NT0RPIENBIExpbWl0ZWQxKzApBgNVBAMTIkNPTU9ETyBSU0EgQ2VydGlmaWNh\n" +
                "dGlvbiBBdXRob3JpdHkwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQCR\n" +
                "6FSS0gpWsawNJN3Fz0RndJkrN6N9I3AAcbxT38T6KhKPS38QVr2fcHK3YX/JSw8X\n" +
                "pz3jsARh7v8Rl8f0hj4K+j5c+ZPmNHrZFGvnnLOFoIJ6dq9xkNfs/Q36nGz637CC\n" +
                "9BR++b7Epi9Pf5l/tfxnQ3K9DADWietrLNPtj5gcFKt+5eNu/Nio5JIk2kNrYrhV\n" +
                "/erBvGy2i/MOjZrkm2xpmfh4SDBF1a3hDTxFYPwyllEnvGfDyi62a+pGx8cgoLEf\n" +
                "Zd5ICLqkTqnyg0Y3hOvozIFIQ2dOciqbXL1MGyiKXCJ7tKuY2e7gUYPDCUZObT6Z\n" +
                "+pUX2nwzV0E8jVHtC7ZcryxjGt9XyD+86V3Em69FmeKjWiS0uqlWPc9vqv9JWL7w\n" +
                "qP/0uK3pN/u6uPQLOvnoQ0IeidiEyxPx2bvhiWC4jChWrBQdnArncevPDt09qZah\n" +
                "SL0896+1DSJMwBGB7FY79tOi4lu3sgQiUpWAk2nojkxl8ZEDLXB0AuqLZxUpaVIC\n" +
                "u9ffUGpVRr+goyhhf3DQw6KqLCGqR84onAZFdr+CGCe01a60y1Dma/RMhnEw6abf\n" +
                "Fobg2P9A3fvQQoh/ozM6LlweQRGBY84YcWsr7KaKtzFcOmpH4MN5WdYgGq/yapiq\n" +
                "crxXStJLnbsQ/LBMQeXtHT1eKJ2czL+zUdqnR+WEUwIDAQABo0IwQDAdBgNVHQ4E\n" +
                "FgQUu69+Aj36pvE8hI6t7jiY7NkyMtQwDgYDVR0PAQH/BAQDAgEGMA8GA1UdEwEB\n" +
                "/wQFMAMBAf8wDQYJKoZIhvcNAQEMBQADggIBAArx1UaEt65Ru2yyTUEUAJNMnMvl\n" +
                "wFTPoCWOAvn9sKIN9SCYPBMtrFaisNZ+EZLpLrqeLppysb0ZRGxhNaKatBYSaVqM\n" +
                "4dc+pBroLwP0rmEdEBsqpIt6xf4FpuHA1sj+nq6PK7o9mfjYcwlYRm6mnPTXJ9OV\n" +
                "2jeDchzTc+CiR5kDOF3VSXkAKRzH7JsgHAckaVd4sjn8OoSgtZx8jb8uk2Intzna\n" +
                "FxiuvTwJaP+EmzzV1gsD41eeFPfR60/IvYcjt7ZJQ3mFXLrrkguhxuhoqEwWsRqZ\n" +
                "CuhTLJK7oQkYdQxlqHvLI7cawiiFwxv/0Cti76R7CZGYZ4wUAc1oBmpjIXUDgIiK\n" +
                "boHGhfKppC3n9KUkEEeDys30jXlYsQab5xoq2Z0B15R97QNKyvDb6KkBPvVWmcke\n" +
                "jkk9u+UJueBPSZI9FoJAzMxZxuY67RIuaTxslbH9qh17f4a+Hg4yRvv7E491f0yL\n" +
                "S0Zj/gA0QHDBw7mh3aZw4gSzQbzpgJHqZJx64SIDqZxubw5lT2yHh17zbqD5daWb\n" +
                "QOhTsiedSrnAdyGN/4fy3ryM7xfft0kL0fJuMAsaDk527RH89elWsn2/x20Kk4yl\n" +
                "0MC2Hb46TpSi125sC8KKfPog88Tk5c0NqMuRkrF8hey1FGlmDoLnzc7ILaZRfyHB\n" +
                "NVOFBkpdn627G190\n" +
                "-----END CERTIFICATE-----"

        const val CLIENT_CERT_REVOKED = "-----BEGIN CERTIFICATE-----\n" +
                "MIIGvzCCBaegAwIBAgIQA3G1iob2zpw+y3v0L5II/DANBgkqhkiG9w0BAQsFADBN\n" +
                "MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMScwJQYDVQQDEx5E\n" +
                "aWdpQ2VydCBTSEEyIFNlY3VyZSBTZXJ2ZXIgQ0EwHhcNMTkxMDA0MDAwMDAwWhcN\n" +
                "MjExMDA4MTIwMDAwWjB0MQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5p\n" +
                "YTEVMBMGA1UEBxMMV2FsbnV0IENyZWVrMRwwGgYDVQQKExNMdWNhcyBHYXJyb24g\n" +
                "VG9ycmVzMRswGQYDVQQDExJyZXZva2VkLmJhZHNzbC5jb20wggEiMA0GCSqGSIb3\n" +
                "DQEBAQUAA4IBDwAwggEKAoIBAQC0Ljkn9nZW+vmCL6At8tAyGZlV3IlElvdzI6/3\n" +
                "pF4+dL9Zec1fC+eP+wMZv4+eY9L/Anx2/hbpAvyGkF+YXNaaui6V6NilxfScnae5\n" +
                "3rhKcWL9Kih9Aq9G1g0dcWHZTNuXFQA09FOBvI6UOd7YvkJ/JOoCU8ZbgD4RLtLZ\n" +
                "C20Yhqwh1nfZSKlPo1sd86U2ZNZNH0a38zUQ9XtFOt2kGNu9o07DEJsZhOWWlZtd\n" +
                "51ZyqyeFaRTc4V42zWnKc8CCB338fo0u+8vJeS6XNkMPFpRFDr3TCWvZ4AP+KgAQ\n" +
                "m5c48FMRXo165qG+LjKp/2NPoMbqNbhZ5KtDokjAGggRvmzDAgMBAAGjggNyMIID\n" +
                "bjAfBgNVHSMEGDAWgBQPgGEcgjFh1S8o541GOLQs4cbZ4jAdBgNVHQ4EFgQUOE25\n" +
                "xq19bGjCX3XXG27LpumeOq0wNQYDVR0RBC4wLIIScmV2b2tlZC5iYWRzc2wuY29t\n" +
                "ghZ3d3cucmV2b2tlZC5iYWRzc2wuY29tMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUE\n" +
                "FjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwawYDVR0fBGQwYjAvoC2gK4YpaHR0cDov\n" +
                "L2NybDMuZGlnaWNlcnQuY29tL3NzY2Etc2hhMi1nNi5jcmwwL6AtoCuGKWh0dHA6\n" +
                "Ly9jcmw0LmRpZ2ljZXJ0LmNvbS9zc2NhLXNoYTItZzYuY3JsMEwGA1UdIARFMEMw\n" +
                "NwYJYIZIAYb9bAEBMCowKAYIKwYBBQUHAgEWHGh0dHBzOi8vd3d3LmRpZ2ljZXJ0\n" +
                "LmNvbS9DUFMwCAYGZ4EMAQIDMHwGCCsGAQUFBwEBBHAwbjAkBggrBgEFBQcwAYYY\n" +
                "aHR0cDovL29jc3AuZGlnaWNlcnQuY29tMEYGCCsGAQUFBzAChjpodHRwOi8vY2Fj\n" +
                "ZXJ0cy5kaWdpY2VydC5jb20vRGlnaUNlcnRTSEEyU2VjdXJlU2VydmVyQ0EuY3J0\n" +
                "MAwGA1UdEwEB/wQCMAAwggF9BgorBgEEAdZ5AgQCBIIBbQSCAWkBZwB1AKS5CZC0\n" +
                "GFgUh7sTosxncAo8NZgE+RvfuON3zQ7IDdwQAAABbZjwwc8AAAQDAEYwRAIgWPi8\n" +
                "7t5MzJnvLDJGmCppeQwyHa1VkvAG811Mg19KbcsCIDpbsejn8Feo/pD1g3xUHm9y\n" +
                "2a5K3ZT2qOI+FfwaNcm7AHYAh3W/51l8+IxDmV+9827/Vo1HVjb/SrVgwbTq/16g\n" +
                "gw8AAAFtmPDCOgAABAMARzBFAiEAmciNTmK3x9F52b+jyQonojj5PR3UTX7I1EY2\n" +
                "yrbyDVsCIDhrUCuwgpjKzdEkKXC8pTrPT750awtW28nCTZLaCVb1AHYARJRlLrDu\n" +
                "zq/EQAfYqP4owNrmgr7YyzG1P9MzlrW2gagAAAFtmPDBQQAABAMARzBFAiEAwXnV\n" +
                "kwbWLzukEmOVbs8IQHiQaERcC3RD7IrKHt4dUvMCIFfUv6IL18E/ROuuFQYDwZrv\n" +
                "DpbCjJdvFw9Cb++GhzzBMA0GCSqGSIb3DQEBCwUAA4IBAQAXzncD0qMluMFZDLOx\n" +
                "Pzev4B56a0EW7X5YJnyy32UVms+VAp5TDDN1kAxmphecVWRc5DpEn+acXM3hHzx0\n" +
                "hBfbYYpAANy96MRgGg3qYIN14OV8QzGIIxCRVDzH3f7kQR1bgZvCQC6fs3JnRJ8l\n" +
                "OhCFNnktylrwV1p48DxxBULjI1oYtXKikEdxs7ZgulOIoVFCSPtzF+MeSwyqYv8I\n" +
                "OCMAvbctgnsuo0eekLyVlJOTe7Cw+hjz5nYX5yCc2wFu0vlL0kw8d6DaS1isZBZ5\n" +
                "p7fCfVZfW4WLJdgxYgATKoTkxVFpcTOr4TodGE3G8fOu6G/BknS9r3g5pLpWaNc6\n" +
                "NtqK\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIElDCCA3ygAwIBAgIQAf2j627KdciIQ4tyS8+8kTANBgkqhkiG9w0BAQsFADBh\n" +
                "MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n" +
                "d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBD\n" +
                "QTAeFw0xMzAzMDgxMjAwMDBaFw0yMzAzMDgxMjAwMDBaME0xCzAJBgNVBAYTAlVT\n" +
                "MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxJzAlBgNVBAMTHkRpZ2lDZXJ0IFNIQTIg\n" +
                "U2VjdXJlIFNlcnZlciBDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\n" +
                "ANyuWJBNwcQwFZA1W248ghX1LFy949v/cUP6ZCWA1O4Yok3wZtAKc24RmDYXZK83\n" +
                "nf36QYSvx6+M/hpzTc8zl5CilodTgyu5pnVILR1WN3vaMTIa16yrBvSqXUu3R0bd\n" +
                "KpPDkC55gIDvEwRqFDu1m5K+wgdlTvza/P96rtxcflUxDOg5B6TXvi/TC2rSsd9f\n" +
                "/ld0Uzs1gN2ujkSYs58O09rg1/RrKatEp0tYhG2SS4HD2nOLEpdIkARFdRrdNzGX\n" +
                "kujNVA075ME/OV4uuPNcfhCOhkEAjUVmR7ChZc6gqikJTvOX6+guqw9ypzAO+sf0\n" +
                "/RR3w6RbKFfCs/mC/bdFWJsCAwEAAaOCAVowggFWMBIGA1UdEwEB/wQIMAYBAf8C\n" +
                "AQAwDgYDVR0PAQH/BAQDAgGGMDQGCCsGAQUFBwEBBCgwJjAkBggrBgEFBQcwAYYY\n" +
                "aHR0cDovL29jc3AuZGlnaWNlcnQuY29tMHsGA1UdHwR0MHIwN6A1oDOGMWh0dHA6\n" +
                "Ly9jcmwzLmRpZ2ljZXJ0LmNvbS9EaWdpQ2VydEdsb2JhbFJvb3RDQS5jcmwwN6A1\n" +
                "oDOGMWh0dHA6Ly9jcmw0LmRpZ2ljZXJ0LmNvbS9EaWdpQ2VydEdsb2JhbFJvb3RD\n" +
                "QS5jcmwwPQYDVR0gBDYwNDAyBgRVHSAAMCowKAYIKwYBBQUHAgEWHGh0dHBzOi8v\n" +
                "d3d3LmRpZ2ljZXJ0LmNvbS9DUFMwHQYDVR0OBBYEFA+AYRyCMWHVLyjnjUY4tCzh\n" +
                "xtniMB8GA1UdIwQYMBaAFAPeUDVW0Uy7ZvCj4hsbw5eyPdFVMA0GCSqGSIb3DQEB\n" +
                "CwUAA4IBAQAjPt9L0jFCpbZ+QlwaRMxp0Wi0XUvgBCFsS+JtzLHgl4+mUwnNqipl\n" +
                "5TlPHoOlblyYoiQm5vuh7ZPHLgLGTUq/sELfeNqzqPlt/yGFUzZgTHbO7Djc1lGA\n" +
                "8MXW5dRNJ2Srm8c+cftIl7gzbckTB+6WohsYFfZcTEDts8Ls/3HB40f/1LkAtDdC\n" +
                "2iDJ6m6K7hQGrn2iWZiIqBtvLfTyyRRfJs8sjX7tN8Cp1Tm5gr8ZDOo0rwAhaPit\n" +
                "c+LJMto4JQtV05od8GiG7S5BNO98pVAdvzr508EIDObtHopYJeS4d60tbvVS3bR0\n" +
                "j6tJLp07kzQoH3jOlOrHvdPJbRzeXDLz\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDrzCCApegAwIBAgIQCDvgVpBCRrGhdWrJWZHHSjANBgkqhkiG9w0BAQUFADBh\n" +
                "MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n" +
                "d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBD\n" +
                "QTAeFw0wNjExMTAwMDAwMDBaFw0zMTExMTAwMDAwMDBaMGExCzAJBgNVBAYTAlVT\n" +
                "MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j\n" +
                "b20xIDAeBgNVBAMTF0RpZ2lDZXJ0IEdsb2JhbCBSb290IENBMIIBIjANBgkqhkiG\n" +
                "9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4jvhEXLeqKTTo1eqUKKPC3eQyaKl7hLOllsB\n" +
                "CSDMAZOnTjC3U/dDxGkAV53ijSLdhwZAAIEJzs4bg7/fzTtxRuLWZscFs3YnFo97\n" +
                "nh6Vfe63SKMI2tavegw5BmV/Sl0fvBf4q77uKNd0f3p4mVmFaG5cIzJLv07A6Fpt\n" +
                "43C/dxC//AH2hdmoRBBYMql1GNXRor5H4idq9Joz+EkIYIvUX7Q6hL+hqkpMfT7P\n" +
                "T19sdl6gSzeRntwi5m3OFBqOasv+zbMUZBfHWymeMr/y7vrTC0LUq7dBMtoM1O/4\n" +
                "gdW7jVg/tRvoSSiicNoxBN33shbyTApOB6jtSj1etX+jkMOvJwIDAQABo2MwYTAO\n" +
                "BgNVHQ8BAf8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUA95QNVbR\n" +
                "TLtm8KPiGxvDl7I90VUwHwYDVR0jBBgwFoAUA95QNVbRTLtm8KPiGxvDl7I90VUw\n" +
                "DQYJKoZIhvcNAQEFBQADggEBAMucN6pIExIK+t1EnE9SsPTfrgT1eXkIoyQY/Esr\n" +
                "hMAtudXH/vTBH1jLuG2cenTnmCmrEbXjcKChzUyImZOMkXDiqw8cvpOp/2PV5Adg\n" +
                "06O/nVsJ8dWO41P0jmP6P6fbtGbfYmbW0W5BjfIttep3Sp+dWOIrWcBAI+0tKIJF\n" +
                "PnlUkiaY4IBIqDfv8NZ5YBberOgOzW6sRBc4L0na4UU+Krk2U886UAb3LujEV0ls\n" +
                "YSEY1QSteDwsOoBrp+uvFRTp2InBuThs4pFsiv9kuXclVzDAGySj4dzp30d8tbQk\n" +
                "CAUw7C29C79Fv1C5qfPrmAESrciIxpg0X40KPMbp1ZWVbd4=\n" +
                "-----END CERTIFICATE-----"

        const val EV_CERT = "-----BEGIN CERTIFICATE-----\n" +
                "MIIHdDCCBlygAwIBAgIQB0Haxhm5e7comqWUzibAzTANBgkqhkiG9w0BAQsFADB1MQswCQYDVQQ\n" +
                "GEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMT\n" +
                "QwMgYDVQQDEytEaWdpQ2VydCBTSEEyIEV4dGVuZGVkIFZhbGlkYXRpb24gU2VydmVyIENBMB4XD\n" +
                "TIwMDMxMDAwMDAwMFoXDTIyMDMxNTEyMDAwMFowgdwxHTAbBgNVBA8MFFByaXZhdGUgT3JnYW5p\n" +
                "emF0aW9uMRMwEQYLKwYBBAGCNzwCAQMTAlVTMRkwFwYLKwYBBAGCNzwCAQITCERlbGF3YXJlMRA\n" +
                "wDgYDVQQFEwczMDE0MjY3MQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTERMA8GA1\n" +
                "UEBxMIU2FuIEpvc2UxFTATBgNVBAoTDFBheVBhbCwgSW5jLjEUMBIGA1UECxMLQ0ROIFN1cHBvc\n" +
                "nQxFzAVBgNVBAMTDnd3dy5wYXlwYWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\n" +
                "AQEAzV89zboBlCiAoOYvIuxNozHpQYGRrKI2f3JHuJL4wWc+v80i1jvWglmQnI7gBrA9eoB5qSM\n" +
                "HU3+f3ubXqwO5teSn5UYasemZw4wPpfU5w5iviSn7xuDK748x9IRXu6kyCMT/NnLLAE/wuVaNnT\n" +
                "K8PZG50UKNicN3R1i6noAWphNJe98stO4CjD1YX6qUkCID2QRNaewR/q3GPZcXyYGpovabx4JBC\n" +
                "AfoyrwX7MMSashX/HcapZO3wbsF+tO3GE1ZIuTxm3QHYDvDTkUbPtft7S5ggv5Wt9UUYC3PieLt\n" +
                "JFBED3zCiFjWNv97H/ozZdlWC27GHSnfh4OFqNynOta4kwIDAQABo4IDljCCA5IwHwYDVR0jBBg\n" +
                "wFoAUPdNQpdagre7zSmAKZdMh1Pj41g8wHQYDVR0OBBYEFKdHmNESeNtRMvqNvx0ubsMOzcztME\n" +
                "AGA1UdEQQ5MDeCDnd3dy5wYXlwYWwuY29tghF3d3ctc3QucGF5cGFsLmNvbYISaGlzdG9yeS5wY\n" +
                "XlwYWwuY29tMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIw\n" +
                "dQYDVR0fBG4wbDA0oDKgMIYuaHR0cDovL2NybDMuZGlnaWNlcnQuY29tL3NoYTItZXYtc2VydmV\n" +
                "yLWcyLmNybDA0oDKgMIYuaHR0cDovL2NybDQuZGlnaWNlcnQuY29tL3NoYTItZXYtc2VydmVyLW\n" +
                "cyLmNybDBLBgNVHSAERDBCMDcGCWCGSAGG/WwCATAqMCgGCCsGAQUFBwIBFhxodHRwczovL3d3d\n" +
                "y5kaWdpY2VydC5jb20vQ1BTMAcGBWeBDAEBMIGIBggrBgEFBQcBAQR8MHowJAYIKwYBBQUHMAGG\n" +
                "GGh0dHA6Ly9vY3NwLmRpZ2ljZXJ0LmNvbTBSBggrBgEFBQcwAoZGaHR0cDovL2NhY2VydHMuZGl\n" +
                "naWNlcnQuY29tL0RpZ2lDZXJ0U0hBMkV4dGVuZGVkVmFsaWRhdGlvblNlcnZlckNBLmNydDAMBg\n" +
                "NVHRMBAf8EAjAAMIIBgAYKKwYBBAHWeQIEAgSCAXAEggFsAWoAdwDuS723dc5guuFCaR+r4Z5mo\n" +
                "w9+X7By2IMAxHuJeqj9ywAAAXDFcnb8AAAEAwBIMEYCIQDwuzYl2COuAY6OhOQOkKHFwydBzAHq\n" +
                "0nfq+sjx4pMShgIhAMupFpT63PmXJRf9yYmAawHFYfJG42Am1LKIfjcxOdRQAHcAVhQGmi/Xwuz\n" +
                "T9eG9RLI+x0Z2ubyZEVzA75SYVdaJ0N0AAAFwxXJ3HgAABAMASDBGAiEA9sLqzoClOirtBp0Hi2\n" +
                "EbFPMoNsagZ5KJ1lNm1FZrAdcCIQDbXiRH/kFOqNmaszNY/CVCeZaezHyWrDj3piruCc4VEAB2A\n" +
                "LvZ37wfinG1k5Qjl6qSe0c4V5UKq1LoGpCWZDaOHtGFAAABcMVydsAAAAQDAEcwRQIgMAg0E301\n" +
                "jaPus8jRHECx3EB4dmx9i9YGmpm/ewljFBoCIQDtdorg7IAj58ZOUNtassnYFj4cshHP8HqAx0d\n" +
                "sJzngzDANBgkqhkiG9w0BAQsFAAOCAQEALew4jcCp55VpcnPhSzHQSpOV3oHCu1BXeRgvHLk2sg\n" +
                "Fs+DFHjyTnhPlozShKhvgksPMO3BhNGCvYqXNubiFDIJSnM9l8p4d8JY0JTV/kt5GR5S0h+zyHY\n" +
                "NpfDw+zBCS8TjJf4zmGNY1VulJy9JEikJXOqvzAn+uy7KKXZnjYHoPMJkSJ8iH8FF5C3s8mbfmF\n" +
                "jYM1RWSS44pdezTfJJ/mmjpSMyclihBXK1vmFTxDQaxtLhisYbNd5hxxDw2oZTYibruc4ELBmJZ\n" +
                "BbryicaBSbmB4pVFCC5JfykI2dP/TyTCxV+Wy++cjjAUehq19e/LdQ2orgofqpAFKjqT1nSkteA\n" +
                "==" +
                "-----END CERTIFICATE-----\n"
    }

    object Beneficiaries {
        val PRIMARY_BENEFICIARY_PKI_X509SHA256 = BeneficiaryParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val PRIMARY_BENEFICIARY_PKI_X509SHA256_INVALID_CERTIFICATE = BeneficiaryParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256_INVALID_CERTIFICATE, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val PRIMARY_BENEFICIARY_PKI_NONE = BeneficiaryParameters(
            true,
            listOf(PKI_DATA_OWNER_NONE)
        )

        val NO_PRIMARY_BENEFICIARY_PKI_X509SHA256 = BeneficiaryParameters(
            false,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val NO_PRIMARY_BENEFICIARY_PKI_NONE = BeneficiaryParameters(
            false,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val PRIMARY_BENEFICIARY_PKI_X509SHA256_BUNDLED_CERTIFICATE = BeneficiaryParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256_BUNDLE_CERTIFICATE, PKI_DATA_TWO_OWNER_X509SHA256)
        )
    }

    object Originators {
        val PRIMARY_ORIGINATOR_PKI_X509SHA256 = OriginatorParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val PRIMARY_ORIGINATOR_PKI_X509SHA256_INVALID_CERTIFICATE = OriginatorParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256_INVALID_CERTIFICATE, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val PRIMARY_ORIGINATOR_PKI_NONE = OriginatorParameters(
            true,
            listOf(PKI_DATA_OWNER_NONE)
        )

        val NO_PRIMARY_ORIGINATOR_PKI_X509SHA256 = OriginatorParameters(
            false,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val NO_PRIMARY_ORIGINATOR_PKI_NONE = OriginatorParameters(
            false,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val PRIMARY_ORIGINATOR_PKI_X509SHA256_BUNDLED_CERTIFICATE = OriginatorParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256_BUNDLE_CERTIFICATE, PKI_DATA_TWO_OWNER_X509SHA256)
        )
    }

    object Senders {
        val SENDER_PKI_X509SHA256 = SenderParameters(
            pkiDataParameters = PKI_DATA_SENDER_X509SHA256,
            evCertificatePem = EV_CERT
        )

        val SENDER_PKI_NONE = SenderParameters(
            PKI_DATA_SENDER_NONE
        )

        val SENDER_PKI_X509SHA256_INVALID_CERTIFICATE = SenderParameters(
            PKI_DATA_SENDER_X509SHA256_INVALID_CERTIFICATE
        )

        val SENDER_PKI_X509SHA256_WITH_ENCRYPTION = SenderParameters(
            PKI_DATA_SENDER_X509SHA256,
            ENCRYPTION_SENDER
        )
    }

    object Recipients {
        val RECIPIENTS_PARAMETERS = RecipientParameters(
            "VASP_1",
            "1234567890ABCD"
        )

        val RECIPIENTS_PARAMETERS_WITH_ENCRYPTION = RecipientParameters(
            "VASP_1",
            "1234567890ABCD",
            ENCRYPTION_RECIPIENT
        )
    }

    object PkiData {
        val PKI_DATA_ONE_OWNER_X509SHA256 = PkiDataParameters(
            attestation = Attestation.LEGAL_PERSON_NAME,
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_ONE,
            certificatePem = CLIENT_CERTIFICATE_CHAIN_ONE,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_TWO_OWNER_X509SHA256 = PkiDataParameters(
            attestation = Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER,
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_TWO,
            certificatePem = CLIENT_CERTIFICATE_CHAIN_TWO,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_OWNER_NONE = PkiDataParameters(
            attestation = null,
            privateKeyPem = "",
            certificatePem = "",
            type = PkiType.NONE
        )

        val PKI_DATA_ONE_OWNER_X509SHA256_INVALID_CERTIFICATE = PkiDataParameters(
            attestation = INVALID_ATTESTATION,
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_ONE,
            certificatePem = CLIENT_CERTIFICATE_RANDOM,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_SENDER_X509SHA256 = PkiDataParameters(
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_TWO,
            certificatePem = CLIENT_CERTIFICATE_CHAIN_TWO,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_SENDER_NONE = PkiDataParameters(
            privateKeyPem = "",
            certificatePem = "",
            type = PkiType.NONE
        )

        val PKI_DATA_SENDER_X509SHA256_INVALID_CERTIFICATE = PkiDataParameters(
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_TWO,
            certificatePem = CLIENT_CERTIFICATE_RANDOM,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_ONE_OWNER_X509SHA256_BUNDLE_CERTIFICATE = PkiDataParameters(
            attestation = Attestation.LEGAL_PERSON_NAME,
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_TWO,
            certificatePem = CLIENT_CERTIFICATE_CHAIN_TWO_BUNDLE,
            type = PkiType.X509SHA256
        )
    }

    object Attestations {
        val INVALID_ATTESTATION = Attestation.ADDRESS_DISTRICT_NAME

        val REQUESTED_ATTESTATIONS = listOf(
            Attestation.LEGAL_PERSON_NAME,
            Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER,
            Attestation.ADDRESS_DEPARTMENT,
            Attestation.ADDRESS_POSTBOX
        )
    }

    object Address {

        val ADDRESS_INFORMATION = AddressInformation(
            identifier = "1234-5678-9103",
            balance = 6.56,
            currency = 3,
            currencyVerbose = "Bitcoin",
            earliestTransactionTime = "2012-09-10 12:12:12",
            latestTransactionTime = "2019-05-12 12:12:12",
            riskLevel = 1,
            riskLevelVerbose = "risk level 1",
            totalIncomingValue = "10.0",
            totalIncomingValueUsd = "15,0",
            totalOutgoingValue = "8.0",
            totalOutgoingValueUsd = "15.2",
            createdAt = "2010-01-01 12:12:12",
            updatedAt = "2010-01-01 12:12:12"
        )

        const val MERKLE_JSON_RESPONSE = "{\n" +
                "    \"identifier\": \"0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48\",\n" +
                "    \"currency\": 1,\n" +
                "    \"currency_verbose\": \"Ethereum\",\n" +
                "    \"total_incoming_value\": \"0.0000\",\n" +
                "    \"total_incoming_value_usd\": \"397181.45\",\n" +
                "    \"total_outgoing_value\": \"0.0100\",\n" +
                "    \"total_outgoing_value_usd\": \"9.00\",\n" +
                "    \"balance\": 10.0,\n" +
                "    \"earliest_transaction_time\": \"2018-08-03T19:30:30Z\",\n" +
                "    \"latest_transaction_time\": \"2020-10-09T03:22:20Z\",\n" +
                "    \"risk_level\": 3,\n" +
                "    \"risk_level_verbose\": \"High Risk\",\n" +
                "    \"case_status\": 1,\n" +
                "    \"case_status_verbose\": \"Opened\",\n" +
                "    \"created_at\": \"2020-07-06T11:44:19.210445Z\",\n" +
                "    \"updated_at\": \"2020-10-09T03:23:46.836948Z\",\n" +
                "    \"originator\": [\n" +
                "        {\n" +
                "            \"tag_type_verbose\": \"Exchange\",\n" +
                "            \"tag_subtype_verbose\": \"Mandatory KYC and AML\",\n" +
                "            \"tag_name_verbose\": \"Bittrex\",\n" +
                "            \"total_value_usd\": \"23310.30\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"tag_type_verbose\": null,\n" +
                "            \"tag_subtype_verbose\": null,\n" +
                "            \"tag_name_verbose\": null,\n" +
                "            \"total_value_usd\": \"376903.01\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"beneficiary\": [\n" +
                "        {\n" +
                "            \"tag_type_verbose\": null,\n" +
                "            \"tag_subtype_verbose\": null,\n" +
                "            \"tag_name_verbose\": null,\n" +
                "            \"total_value_usd\": \"1855.02\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"tags\": {\n" +
                "        \"owner\": {},\n" +
                "        \"user\": {\n" +
                "            \"tag_type_verbose\": \"Smart Contract Platform\",\n" +
                "            \"tag_subtype_verbose\": \"Token\",\n" +
                "            \"tag_name_verbose\": \"USD Coin\"\n" +
                "        }\n" +
                "    }\n" +
                "}"
    }

    object CertificateGeneration {
        val ATTESTATIONS_REQUESTED = listOf(
            Attestation.LEGAL_PERSON_NAME,
            Attestation.ADDRESS_STREET_NAME,
            Attestation.ADDRESS_ADDRESS_LINE
        )

        val ATTESTATIONS_SUBMITTED = AttestationResponse("message", "123457890")

        val CSRS_ATTESTATIONS = listOf(
            CsrAttestation("csr_1", Attestation.LEGAL_PERSON_NAME, "public_key_1"),
            CsrAttestation("csr_2", Attestation.ADDRESS_STREET_NAME, "public_key_2"),
            CsrAttestation("csr_3", Attestation.ADDRESS_ADDRESS_LINE, "public_key_3")
        )

        const val TRANSACTION_ID = "1234567890xyz"

        val CERTIFICATE_ATTESTATION_RESPONSE = CertificateAttestationResponse(
            count = 3,
            certificates = listOf(
                com.netki.keymanagement.repo.data.Certificate(
                    attestation = Attestation.LEGAL_PERSON_NAME,
                    certificate = CLIENT_CERTIFICATE_CHAIN_ONE,
                    id = 1234,
                    isActive = true
                ),
                com.netki.keymanagement.repo.data.Certificate(
                    attestation = Attestation.ADDRESS_STREET_NAME,
                    certificate = CLIENT_CERTIFICATE_CHAIN_TWO,
                    id = 12345,
                    isActive = true
                ),
                com.netki.keymanagement.repo.data.Certificate(
                    attestation = Attestation.ADDRESS_ADDRESS_LINE,
                    certificate = CLIENT_CERTIFICATE_CHAIN_TWO,
                    id = 12345,
                    isActive = true
                )
            )
        )

        val ATTESTATIONS_INFORMATION = listOf(
            AttestationInformation(
                Attestation.LEGAL_PERSON_NAME,
                IvmsConstraints.LEGL,
                "This is the LEGAL_PERSON_PRIMARY_NAME"
            ),
            AttestationInformation(
                Attestation.ADDRESS_STREET_NAME,
                IvmsConstraints.HOME,
                "This is the ADDRESS_STREET_NAME"
            ),
            AttestationInformation(
                Attestation.ADDRESS_ADDRESS_LINE,
                IvmsConstraints.BIZZ,
                "This is the ADDRESS_ADDRESS_LINE"
            )
        )
    }

    object MessageInformationData {
        val MESSAGE_INFORMATION_CANCEL = MessageInformation(
            StatusCode.CANCEL,
            "Cancel for testing"
        )

        val MESSAGE_INFORMATION_ENCRYPTION = MessageInformation(
            encryptMessage = true
        )
    }

    object Encryption {
        private val keysRecipient = generateKeyPairECDSA()
        val ENCRYPTION_RECIPIENT = EncryptionParameters(
            CryptoModule.objectToPrivateKeyPem(keysRecipient.private),
            CryptoModule.objectToPublicKeyPem(keysRecipient.public)
        )

        private val keysSender = generateKeyPairECDSA()
        val ENCRYPTION_SENDER = EncryptionParameters(
            CryptoModule.objectToPrivateKeyPem(keysSender.private),
            CryptoModule.objectToPublicKeyPem(keysSender.public)
        )
    }

    fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}

