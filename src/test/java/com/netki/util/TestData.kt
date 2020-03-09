package com.netki.util

import com.netki.model.*
import com.netki.util.TestData.KeyPairs.CLIENT_CERTIFICATE_PEM
import com.netki.util.TestData.KeyPairs.CLIENT_PRIVATE_KEY_PEM
import com.netki.util.TestData.PkiData.PKI_DATA_OWNER_X509SHA256
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
        val PAYMENT_DETAILS = PaymentParameters(
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

    object KeyPairs {

        const val ROOT_CERTIFICATE_PEM = "-----BEGIN CERTIFICATE-----\n" +
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

        const val INTERMEDIATE_CERTIFICATE_PEM = "-----BEGIN CERTIFICATE-----\n" +
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

        const val CLIENT_CERTIFICATE_PEM = "-----BEGIN CERTIFICATE-----\n" +
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

        const val CLIENT_PRIVATE_KEY_PEM = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDMomtGumnzsB4c\n" +
                "+sE0t7rEkTfjsrOFDCY25tuBS10sqSEzUINVpgEgkffDnJfdgXLdAaiIuxx+I0YI\n" +
                "FPvvvQgo4TbbXFS9hnG9sns1bbjjIqbL6qqcxaA0h+ehiP2ezVbaJYeUno8y2kYH\n" +
                "HzttiPA5oBYh87SKYX/ikObqdyp4iQaVQMGpHNtO+VPjVJehp2kqVySB4qjyDoKl\n" +
                "G2ZcLcTEsvuyvaQHKtueSZUPxS6fL+y2dIUWbgxHqPrvcgClbKQXfEkKPzY0eKTL\n" +
                "4WJRSqAWRV+zJA2CayWPyvJw8pS7gkk+CGbbUw7tDn+O9mjvxDp+gitioMHtKhsJ\n" +
                "HBE5ODulAgMBAAECggEAJH0Zr+Txm5hd5kD4TpQsY3yZgKqOxDykW8nSfj2YAayu\n" +
                "1N2YbZ9KOCqPCXxUwBSjDLGNAcSLkhpsFGjZe6gzWka+Z2MRYTIl+fNncOF9xFKb\n" +
                "d2UCwy2iIXvSW8V3o+dtgzyJ7oBPfHvbXM2+5Qsz+rKG/8ra+InmKdo4srpJAnDq\n" +
                "5Nzw2E84UerkjFSdY+AbexaIdcZHGlFnxn7vGQcMvIsBqfBqChc9vnSFxCmR6mqV\n" +
                "zxjO33A5qf5h/Epoj0ui5stqa15WhCgc8JsHH8SckTh9Agd7zqhk/V/doYlg8C0E\n" +
                "anvFbW4TM//awWfCaASkGqVdOwfvAiI4FECWrb50AQKBgQDs+ca5I6CTmHwrEPkZ\n" +
                "ucKcVLf/LxzZPm0e7lhqedp94OHfWMhTkLaDqSRrFin/E+WnN0ZS+qp7w5uiOrAf\n" +
                "0XbFMYQRzUtR5ezbqAxoW19WTb30gCGXm44F6khIw7JSCuP6zG22qMOkiAW+zpMB\n" +
                "89rTOjE+YQIa/fSESI8M3G/yJQKBgQDdD/q4AmUtRMRu5VjIEhjGzk34Ug43Fmr4\n" +
                "uXZoUocGflnvPzsFfMeBJgjmUAUq/OL0iYG/fOrXObNEZ+aH+0GpvlsIE/iJ3aA4\n" +
                "726iiTl8vdv5GTyaqGz4Q3tS8J0L2H1CJKoRkVbAWrcGYwLi2TuM38bUX+yB0r9r\n" +
                "lf5ijacrgQKBgCaaedjnCN2CVZfeZ/Xc4Or+kgqr7hMlrDkBsr2FpCYlYCY2HEwg\n" +
                "otrHzvry3VyELUEULAyQcP1AXDYNQWutf5+X9V/BBagNwIv30C2f1OQGPg96X/6G\n" +
                "hJhKFgRkfMQIqiLM8oJy84v17JmspR4IT3lhXWw/+UvUWuTBvSvnLQVRAoGBAIVN\n" +
                "/YK47wo9TbcR1lfPkeFQxvPXh3rwqdETBbQjEAl6aAE9v/mvJR9cMEGyP2uM089i\n" +
                "nDs8uODQiqnVfc7CVPZnM73LTTTV0KiEudKJrYDrfJrZ/RHGPu/2wYdiUVGzWtVo\n" +
                "BqZRXl7gVT4ktrjVBnQM/XlT3urqi0P1T1Fe2lABAoGAH5ijkdj1FPB81bfV7r+H\n" +
                "oFaofZqhT6Il7v6CmgYeP30t0PjembiQT1zphu5fyOq6x8rSKj21otpx/13ZoJXw\n" +
                "5PAOL3vC/sNf7byEm6sb7EHYX989NYvlJoGEAf8lVfsHbEhslsG19yHZA9I3K2Ps\n" +
                "JaAIObkazBwQYUqLeoRRf38=\n" +
                "-----END PRIVATE KEY-----"

        const val CLIENT_CERTIFICATE_RANDOM_PEM = "-----BEGIN CERTIFICATE-----\n" +
                "MIIKDDCCCPSgAwIBAgIQBegYi5QMRcwIAAAAACmWhTANBgkqhkiG9w0BAQsFADBC\n" +
                "MQswCQYDVQQGEwJVUzEeMBwGA1UEChMVR29vZ2xlIFRydXN0IFNlcnZpY2VzMRMw\n" +
                "EQYDVQQDEwpHVFMgQ0EgMU8xMB4XDTIwMDExNDA5MjcxNFoXDTIwMDQwNzA5Mjcx\n" +
                "NFowZjELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcT\n" +
                "DU1vdW50YWluIFZpZXcxEzARBgNVBAoTCkdvb2dsZSBMTEMxFTATBgNVBAMMDCou\n" +
                "Z29vZ2xlLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHbu3WA\n" +
                "IXRlPDfkmc+Y9NmdecL/+D9Pmdghd5bJA/+vkrfuwbrEU6ww1A2YBwl+GZ4zXzK4\n" +
                "a4HuXWzK0HByB1JFrX2ve7XY/esqAWVoCrdkPXU8R03V7Wym4aWUQSU+xfaH6JDI\n" +
                "NfRYvg3bKajQuzabEWroxrrq4EVIT72ObR3X3sIeNBAqb3MryA6rPHaKcBSzcRTv\n" +
                "fvKuGGvDsynTza5nEjiiyS0SRIjSBHnAQCyus0CErRqjYcl5L8BOqOlpW5Yyrb3E\n" +
                "d5eAxg8TJpW03ELVicsBcNBuYxfNSdoEFzyc+YVGducMP3lgOrCcAwyq6Bht+uB9\n" +
                "DdQcV3mNXxTNZt0CAwEAAaOCBtgwggbUMA4GA1UdDwEB/wQEAwIFoDATBgNVHSUE\n" +
                "DDAKBggrBgEFBQcDATAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTe41yl8Cv8OljP\n" +
                "jz5QiClEvIwhJzAfBgNVHSMEGDAWgBSY0fhuEOvPm+xgnxiQG6DrfQn9KzBkBggr\n" +
                "BgEFBQcBAQRYMFYwJwYIKwYBBQUHMAGGG2h0dHA6Ly9vY3NwLnBraS5nb29nL2d0\n" +
                "czFvMTArBggrBgEFBQcwAoYfaHR0cDovL3BraS5nb29nL2dzcjIvR1RTMU8xLmNy\n" +
                "dDCCBJ0GA1UdEQSCBJQwggSQggwqLmdvb2dsZS5jb22CDSouYW5kcm9pZC5jb22C\n" +
                "FiouYXBwZW5naW5lLmdvb2dsZS5jb22CEiouY2xvdWQuZ29vZ2xlLmNvbYIYKi5j\n" +
                "cm93ZHNvdXJjZS5nb29nbGUuY29tggYqLmcuY2+CDiouZ2NwLmd2dDIuY29tghEq\n" +
                "LmdjcGNkbi5ndnQxLmNvbYIKKi5nZ3BodC5jboIOKi5na2VjbmFwcHMuY26CFiou\n" +
                "Z29vZ2xlLWFuYWx5dGljcy5jb22CCyouZ29vZ2xlLmNhggsqLmdvb2dsZS5jbIIO\n" +
                "Ki5nb29nbGUuY28uaW6CDiouZ29vZ2xlLmNvLmpwgg4qLmdvb2dsZS5jby51a4IP\n" +
                "Ki5nb29nbGUuY29tLmFygg8qLmdvb2dsZS5jb20uYXWCDyouZ29vZ2xlLmNvbS5i\n" +
                "coIPKi5nb29nbGUuY29tLmNvgg8qLmdvb2dsZS5jb20ubXiCDyouZ29vZ2xlLmNv\n" +
                "bS50coIPKi5nb29nbGUuY29tLnZuggsqLmdvb2dsZS5kZYILKi5nb29nbGUuZXOC\n" +
                "CyouZ29vZ2xlLmZyggsqLmdvb2dsZS5odYILKi5nb29nbGUuaXSCCyouZ29vZ2xl\n" +
                "Lm5sggsqLmdvb2dsZS5wbIILKi5nb29nbGUucHSCEiouZ29vZ2xlYWRhcGlzLmNv\n" +
                "bYIPKi5nb29nbGVhcGlzLmNughEqLmdvb2dsZWNuYXBwcy5jboIUKi5nb29nbGVj\n" +
                "b21tZXJjZS5jb22CESouZ29vZ2xldmlkZW8uY29tggwqLmdzdGF0aWMuY26CDSou\n" +
                "Z3N0YXRpYy5jb22CEiouZ3N0YXRpY2NuYXBwcy5jboIKKi5ndnQxLmNvbYIKKi5n\n" +
                "dnQyLmNvbYIUKi5tZXRyaWMuZ3N0YXRpYy5jb22CDCoudXJjaGluLmNvbYIQKi51\n" +
                "cmwuZ29vZ2xlLmNvbYITKi53ZWFyLmdrZWNuYXBwcy5jboIWKi55b3V0dWJlLW5v\n" +
                "Y29va2llLmNvbYINKi55b3V0dWJlLmNvbYIWKi55b3V0dWJlZWR1Y2F0aW9uLmNv\n" +
                "bYIRKi55b3V0dWJla2lkcy5jb22CByoueXQuYmWCCyoueXRpbWcuY29tghphbmRy\n" +
                "b2lkLmNsaWVudHMuZ29vZ2xlLmNvbYILYW5kcm9pZC5jb22CG2RldmVsb3Blci5h\n" +
                "bmRyb2lkLmdvb2dsZS5jboIcZGV2ZWxvcGVycy5hbmRyb2lkLmdvb2dsZS5jboIE\n" +
                "Zy5jb4IIZ2dwaHQuY26CDGdrZWNuYXBwcy5jboIGZ29vLmdsghRnb29nbGUtYW5h\n" +
                "bHl0aWNzLmNvbYIKZ29vZ2xlLmNvbYIPZ29vZ2xlY25hcHBzLmNughJnb29nbGVj\n" +
                "b21tZXJjZS5jb22CGHNvdXJjZS5hbmRyb2lkLmdvb2dsZS5jboIKdXJjaGluLmNv\n" +
                "bYIKd3d3Lmdvby5nbIIIeW91dHUuYmWCC3lvdXR1YmUuY29tghR5b3V0dWJlZWR1\n" +
                "Y2F0aW9uLmNvbYIPeW91dHViZWtpZHMuY29tggV5dC5iZTAhBgNVHSAEGjAYMAgG\n" +
                "BmeBDAECAjAMBgorBgEEAdZ5AgUDMC8GA1UdHwQoMCYwJKAioCCGHmh0dHA6Ly9j\n" +
                "cmwucGtpLmdvb2cvR1RTMU8xLmNybDCCAQIGCisGAQQB1nkCBAIEgfMEgfAA7gB1\n" +
                "ALIeBcyLos2KIE6HZvkruYolIGdr2vpw57JJUy3vi5BeAAABb6OX41kAAAQDAEYw\n" +
                "RAIgKvQpasniC0ZqlvWQPlS0rbmkWIjc98HJOhGm9y4DTsACICHa+JIi/5bGjk0d\n" +
                "O3+yWI+uuTvfE38JE+dMsBcyEm96AHUAXqdz+d9WwOe1Nkh90EngMnqRmgyEoRIS\n" +
                "hBh1loFxRVgAAAFvo5fjcgAABAMARjBEAiBkbD1ky1I/WEXHEtGh/ATcakhFgMJv\n" +
                "aZ/A/EOru14/YgIgC+hHDlUkY1jeWBTWFmI65+Zn//y5iYIQLa2R4MXGFCgwDQYJ\n" +
                "KoZIhvcNAQELBQADggEBADU7629MxtqP/O+W9HvI+AI+p0ch2iIvW483cxCvkbGF\n" +
                "L2OoFB75aLHkik8PRR+b+cT4nG8Swnb/DM0253NlwSf4SlKTh2aB+dZCYU1YAa0a\n" +
                "ZX7AxLefAWd6JEmYXp9HHvJev8q3xeKP2ITzcUpzwsFL3lGws3xHTYppGUXSEw0k\n" +
                "NTHEuQZvBG+3N/fQyZoEpcumulACk+5GaRhZD37FTa0pZNLFh8Cbu6wsbVNEHtlP\n" +
                "QQaG87Sk3847WJruAr1aZZG8ZWL5Nh5/a+gk5HspO02cZc9rCb/ZrxnduKRgKOzk\n" +
                "cMq173vFYQTtL5fN7PGXTcwjRxt7MsqCCSSPFkTMc84=\n" +
                "-----END CERTIFICATE-----"
    }

    object Owners {
        val PRIMARY_OWNER = OwnerParameters(
            true,
            listOf(PKI_DATA_OWNER_X509SHA256)
        )
        val NO_PRIMARY_OWNER = OwnerParameters(
            false,
            listOf(PKI_DATA_OWNER_X509SHA256)
        )
    }

    object PkiData {
        val PKI_DATA_OWNER_X509SHA256 = PkiDataParameters(
            attestation = "address_attestation",
            privateKeyPem = CLIENT_PRIVATE_KEY_PEM,
            certificatePem = CLIENT_CERTIFICATE_PEM,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_OWNER_NONE = PkiDataParameters(
            attestation = "address_attestation",
            privateKeyPem = "",
            certificatePem = "",
            type = PkiType.NONE
        )

        val PKI_DATA_SENDER_X509SHA256 = PkiDataParameters(
            privateKeyPem = CLIENT_PRIVATE_KEY_PEM,
            certificatePem = CLIENT_CERTIFICATE_PEM,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_SENDER_NONE = PkiDataParameters(
            privateKeyPem = "",
            certificatePem = "",
            type = PkiType.NONE
        )
    }

}
