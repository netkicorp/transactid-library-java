package com.netki.util

import com.netki.model.*
import com.netki.util.TestData.KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_TWO
import com.netki.util.TestData.KeyPairs.EV_CERT
import com.netki.util.TestData.PkiData.PKI_DATA_ONE_OWNER_X509SHA256
import com.netki.util.TestData.PkiData.PKI_DATA_ONE_OWNER_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.PkiData.PKI_DATA_SENDER_X509SHA256
import com.netki.util.TestData.PkiData.PKI_DATA_SENDER_X509SHA256_INVALID_CERTIFICATE
import com.netki.util.TestData.PkiData.PKI_DATA_TWO_OWNER_X509SHA256

internal object TestData {

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
                "MIIEnzCCA4egAwIBAgIUCmIDIdIkg7fo6jRhJTuScXplynswDQYJKoZIhvcNAQEL\n" +
                "BQAwfzELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRQwEgYDVQQHEwtMb3MgQW5n\n" +
                "ZWxlczEZMBcGA1UECxMQTmV0a2kgT3BlcmF0aW9uczEyMDAGA1UEAxMpVHJhbnNh\n" +
                "Y3RJRCBJbnRlcm1lZGlhdGUgQ0FpIC0gREVWRUxPUE1FTlQwHhcNMjEwMzIzMDAy\n" +
                "NTAwWhcNMjQwMzIyMDAyNTAwWjCBtjEJMAcGA1UEBhMAMQ0wCwYDVQQIEwRMRUdM\n" +
                "MRwwGgYDVQQHExNsZWdhbFBlcnNvbk5hbWVUeXBlMRgwFgYDVQQKEw9sZWdhbFBl\n" +
                "cnNvbk5hbWUxJDAiBgNVBAsTG2xlZ2FsUGVyc29uLmxlZ2FsUGVyc29uTmFtZTE8\n" +
                "MDoGA1UEAxMzVGhpcyBpcyB0aGUgZGF0YSBmb3IgbmF0dXJhbFBlcnNvblByaW1h\n" +
                "cnlJZGVudGlmaWVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsckS\n" +
                "quo9OVMvs0J/z6OqRJfCeHy54+mysQZMOtUrRT/yF9OIe+EVQxGlEZpCYg3AsG2O\n" +
                "P1G8RnV2gXpNgw3stiKfL3gZO+vVcRbrjk5XzoXyWDNdM18H5azgnpN54i+na3u5\n" +
                "4uDIIC40fdwNRU3A005ZiToLCz2iSQ64K43PhqGGOYN5ZLam/mA26Ac3wp42X6vT\n" +
                "KUoL8rnJ7Ct+SeXMudmHEEKgbi9PE8rRJpx912DqvQm+UFlbYcar+8R7dJ9CtbCT\n" +
                "CVo3YqHRjIVsTDtcowyh8g7RDLpGjuTQwLHICD1hdvLfrzOvcVfjjlEOXlafq6y/\n" +
                "jdBjWgEBcCExdHdQUwIDAQABo4HaMIHXMA4GA1UdDwEB/wQEAwIFoDATBgNVHSUE\n" +
                "DDAKBggrBgEFBQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTraRFfPUv2YVrQ\n" +
                "ubW0vQ1aBMdavTAfBgNVHSMEGDAWgBQaYTgDPKdAkbtPdww4nGGJG7HOwTA0Bggr\n" +
                "BgEFBQcBAQQoMCYwJAYIKwYBBQUHMAGGGGh0dHBzOi8vb2NzcC5teXZlcmlmeS5p\n" +
                "bzAsBgNVHR8EJTAjMCGgH6AdhhtodHRwczovL2NybC5teXZlcmlmeS5pby9jcmww\n" +
                "DQYJKoZIhvcNAQELBQADggEBALl3od7Cj1i1pEhOQYw6X9UYkEoA26kp2vJ+wrj5\n" +
                "re/0Gv03XVz71L0eeoSpvmYxDxzzw0FtixuUa5kHgjPbZyuCdnTXU9wBZsFbyFPJ\n" +
                "2SquQp+jDb0mdNMXHdGtAhshZsOuFOOM164XCZnSboagp7VfAk9293Tsji1qTgyW\n" +
                "xp8tuDcryHBkUJpudQ+p5MtVpUPpp9R3uEEcFdbxP9kRRo0eKyDdYh/6r3988Wln\n" +
                "1PjMSayXf0Uih4wNfGr5wMHpkpUYbPwFSAf80hi7havtfh98Q882rTS5NGgy8jV1\n" +
                "0k92WFtteQcAvqM7pbssp8T8E3rJj5aZElSltGZAxqvOS/8=\n" +
                "-----END CERTIFICATE-----\n"

        const val CLIENT_PRIVATE_KEY_CHAIN_ONE = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCxyRKq6j05Uy+z\n" +
                "Qn/Po6pEl8J4fLnj6bKxBkw61StFP/IX04h74RVDEaURmkJiDcCwbY4/UbxGdXaB\n" +
                "ek2DDey2Ip8veBk769VxFuuOTlfOhfJYM10zXwflrOCek3niL6dre7ni4MggLjR9\n" +
                "3A1FTcDTTlmJOgsLPaJJDrgrjc+GoYY5g3lktqb+YDboBzfCnjZfq9MpSgvyucns\n" +
                "K35J5cy52YcQQqBuL08TytEmnH3XYOq9Cb5QWVthxqv7xHt0n0K1sJMJWjdiodGM\n" +
                "hWxMO1yjDKHyDtEMukaO5NDAscgIPWF28t+vM69xV+OOUQ5eVp+rrL+N0GNaAQFw\n" +
                "ITF0d1BTAgMBAAECggEBAITKslXVJivGNa/IcNzv20Lms8v5JYPVz7GoCZI8HNjZ\n" +
                "vYMMbjpRUedJq6jtNr40lYNyITisXVunav+lEXZdFTypuYrkQrzeFwwkWYduful0\n" +
                "ZSJ6Ixg22Bg2O4RWlUhb3cpLnPmYegKHYI/NqF/mhquOLxRvtUYNIEU/aFKn1qUw\n" +
                "i+HrkpHlfNZaDgUO0X6eEceqmhzBfNi5deNgD3nfL7/lLmhzl15II8FcqTIlmFiw\n" +
                "9f6mojDX3/9g0GfDqk7RIiM1gFlzY8vZv9OPs6IAZSRauVEOA2eJtQ5cmaxEeI/d\n" +
                "GB5lhSiah5juKpQ+tU1VhOdRZMedircRMu2Xy6gJgDECgYEA2g5r5f7sMeiIgynM\n" +
                "7sAEAJB4f+P/oSmzB4dTvAaUd0NZa9CFDVwOJMyiBxDezHNZn76Dg67c8VmlMqxN\n" +
                "YVLlNAkQDLCygqgASJbY5kTw8tbBnP0vyekfJk62kEuhfHL0r4Y/lXPGMhdwP0RW\n" +
                "Zc57NTZBv0dv2njSymFyA1j2HskCgYEA0Li8ligNv+MH/HY0b3uhfjy1dSGYP3YO\n" +
                "J/zAMHQY2YCeyvgwAG902y0Dh6siXiB61bRA+QF7fUabIPTdfo+NxwvaByI1uRLb\n" +
                "1m0GGOAVVLPgTa1LqD+INlMynlDU1uavtHgGxjkQxAND7DdlfQDd9iPdOmSKWYwm\n" +
                "pc7ncgLQeDsCgYEAjUOUgR7CM576eUamPfHlZdwyRGAXpnfWRMVV6NS2cAEQuDkR\n" +
                "SVNe0lZDjaJPRFJiOIv6tV+eQTkbPZXEV42VcT2ByUbbjqt564zWHW+CTT/1lFeu\n" +
                "EvdUt8N8oERu7KmofOHS5WZoeuEWVdZWxoOa7CEnPNzxyK5HmNbCPwrt/4kCgYEA\n" +
                "yI/r76H3bF79apQvWL0E9qfhefdZNAn+GmCeUTEOO9qDO+h3P8PaF05O6QwCT06I\n" +
                "mlfGY0AQaNXy9R02xYmuJAl4bYhq9Tdw9b/3rumMtcLPE/UlETxTaFhT+JsVmpc7\n" +
                "WYBIiiuFt8SnfRHSPOcbYo0d5SF9bATnkkaaUgzwQ8cCgYA6LztDaIUag/Xvb3/j\n" +
                "LGbo8KpzTQkH+Z4enZQeigKnYa1cohKkeHGRsonYq3JDJIiO87PTU1XGo1u7U7XB\n" +
                "5rr/WkJekbfJfPsRMVXXYrxWTb/9RjmOFNBrQN185XJD/R2hnP+P5mOAv40X9bFB\n" +
                "kEuyQ4F87wb1N6Xo6Q+MQc993w==\n" +
                "-----END PRIVATE KEY-----\n"

        const val CLIENT_CERTIFICATE_CHAIN_TWO = "-----BEGIN CERTIFICATE-----\n" +
                "MIIEnzCCA4egAwIBAgIUPxuB4M+yvTVP6ls52hfp6rgPbdkwDQYJKoZIhvcNAQEL\n" +
                "BQAwfzELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRQwEgYDVQQHEwtMb3MgQW5n\n" +
                "ZWxlczEZMBcGA1UECxMQTmV0a2kgT3BlcmF0aW9uczEyMDAGA1UEAxMpVHJhbnNh\n" +
                "Y3RJRCBJbnRlcm1lZGlhdGUgQ0FpIC0gREVWRUxPUE1FTlQwHhcNMjEwMzIzMDAw\n" +
                "OTAwWhcNMjQwMzIyMDAwOTAwWjCBtjEJMAcGA1UEBhMAMQ0wCwYDVQQIEwRMRUdM\n" +
                "MRwwGgYDVQQHExNsZWdhbFBlcnNvbk5hbWVUeXBlMRgwFgYDVQQKEw9sZWdhbFBl\n" +
                "cnNvbk5hbWUxJDAiBgNVBAsTG2xlZ2FsUGVyc29uLmxlZ2FsUGVyc29uTmFtZTE8\n" +
                "MDoGA1UEAxMzVGhpcyBpcyB0aGUgZGF0YSBmb3IgbmF0dXJhbFBlcnNvblByaW1h\n" +
                "cnlJZGVudGlmaWVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlYfD\n" +
                "Kfz0tqIEd78Xftj84PNpr++bQ5k3Zrzg4HdjwFbCiGRWCRX8VhpFsx1wM5F1ytpG\n" +
                "fRxiJSPZDTj1nfK4TF1ZMqHOlgyVFjRAlyzDCGsErVH7tppmYLFiX5/oCUvO7bIY\n" +
                "6EDUvm8TAO1DpVlake/Rmam/LaPYuEoBemxa9JwRNQIBuPnBCfcWyYqME/Bh8kqR\n" +
                "6BX/whDXumCR4xcuGZ/2KwJNIS/OoD79wvSbUNvahI88DfnzodP4YEs24fVcDZB5\n" +
                "wXFYWt6VkwPfdLWnxk3mzlHOmpjrT1YIua9DHRqQNq5DYqMwiozbMxBUfHdBEfHj\n" +
                "Z63d6ezE232OYSDzkwIDAQABo4HaMIHXMA4GA1UdDwEB/wQEAwIFoDATBgNVHSUE\n" +
                "DDAKBggrBgEFBQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBSPT6mFkPdqjt0P\n" +
                "QZtbAkV2DrDixjAfBgNVHSMEGDAWgBQaYTgDPKdAkbtPdww4nGGJG7HOwTA0Bggr\n" +
                "BgEFBQcBAQQoMCYwJAYIKwYBBQUHMAGGGGh0dHBzOi8vb2NzcC5teXZlcmlmeS5p\n" +
                "bzAsBgNVHR8EJTAjMCGgH6AdhhtodHRwczovL2NybC5teXZlcmlmeS5pby9jcmww\n" +
                "DQYJKoZIhvcNAQELBQADggEBAGM+j6BUb/w9ga+C8xM+T3PowEs7fZhmOmDXJyaL\n" +
                "cO6V/qfRR4xg1CrK1M0uADCyqT2M5X67bwt7vNgCt92uH5Z/0t79HCK00Yn39cu/\n" +
                "YGdR2itiTwrtLDj48ESXe6V7kqTYWH4fpSRE1TdnHq5PD61XFzox5Pa5dVIggY/K\n" +
                "MWbxeAbEx7v+Ou1QG6ie99tYlWuNz4fKAPLMUYZ3ky/0m1dGMQqyi/R3AD0fUZVO\n" +
                "6EhyhJm6j5KCgHlJJxng7NxpSu8UZiHEkvWwuD06Dhqk2fETyxsbaizM/bFOkYXe\n" +
                "RLE0biO8RsfOKKFnL3Y9VFo4HYWClH1OmcJQY6tNQdv7lnY=\n" +
                "-----END CERTIFICATE-----\n"

        const val CLIENT_PRIVATE_KEY_CHAIN_TWO = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCVh8Mp/PS2ogR3\n" +
                "vxd+2Pzg82mv75tDmTdmvODgd2PAVsKIZFYJFfxWGkWzHXAzkXXK2kZ9HGIlI9kN\n" +
                "OPWd8rhMXVkyoc6WDJUWNECXLMMIawStUfu2mmZgsWJfn+gJS87tshjoQNS+bxMA\n" +
                "7UOlWVqR79GZqb8to9i4SgF6bFr0nBE1AgG4+cEJ9xbJiowT8GHySpHoFf/CENe6\n" +
                "YJHjFy4Zn/YrAk0hL86gPv3C9JtQ29qEjzwN+fOh0/hgSzbh9VwNkHnBcVha3pWT\n" +
                "A990tafGTebOUc6amOtPVgi5r0MdGpA2rkNiozCKjNszEFR8d0ER8eNnrd3p7MTb\n" +
                "fY5hIPOTAgMBAAECggEASEpubCpDFNiXWF0mOskk2IxVmB067x9vzVebUGnn6+EG\n" +
                "A3KetZ3PdMEW2VVuHUBBtmR4l5vVRydhlCbpeAcUWrb2nKflfF1w5l80quGVGMjE\n" +
                "ZhawnsNeo3iemqRwRa5EyF3F9OMC914zzcrnXVUpmExdBPEv4BzKda4xsMIZ5w+d\n" +
                "ybHBsbp+lHiSEAoxe6G/vOdxgGw0E3zkzdrTGu4GvKfussPz/DPxoE7LOv6TUVdy\n" +
                "fajePkcw/92/l39XeYu9lx30j53PFixz9FrEqDsG/UyobwTr5Yf65zh76jFe2ZpV\n" +
                "Jw6bFAumf3G7PWkTbzz+/pkxRahVGBS4jK352q3RAQKBgQDf8SyzXuo8LmSqzgd5\n" +
                "pznC3N8WNhW9rtF9Tl+xsNLIRfCKvSoqdPSMNklOTFaOpideEwvxyl4oQvardHyf\n" +
                "+ggPWd5oZg6KDkEXsnoI6yL7dt7VIP566MAps+NcTr/8EeaUkLSxZboUUiiFiGfy\n" +
                "XeADinH8pZDh4XwtbRRwHMB6MwKBgQCq75z6jWoK3HvXsoA/dnreT5GRajTWW6Jw\n" +
                "/aLy1JCXxUgZvJ3wUXrZRxV9vfamYJtwq5h1vYE74ZK+nB3YJrJ0E223JDr3gZ9R\n" +
                "E4Qb/9TN62iwLDta2YkeThSyRzYtZAGsET59YRPx47bCVJ8iF7xTHywAflv8D9vJ\n" +
                "/9vUq80BIQKBgCpMqM/cvsvNS5CDyB+veZaYF79fSe4BRmqv0h2DM91GcLAUGRHZ\n" +
                "85NEccZLXxIkykzXtireubhLJcKvBxdEqB8WL49yr45eMOdj++8RUxNCmcaSK99V\n" +
                "dW6rHugBq/vV+cLYLnlPqL1L44GNiWzbVIP2s58wOtSfvc/qybB/jc/HAoGAZ46y\n" +
                "87govlvFS3AA8nG9DmH2Nrq5OARb7Ug8KBFPaCNFAxKaPLWgT3IZOwyTGUj94syS\n" +
                "mQIuATEvzfqWuhT3mAsNNR7l+ny1IFFKgAwFyJsN2W1yqB+SSqHTOA6ca/Nib/Qi\n" +
                "f6MIiksCtci+f9ERbuo7pjDnWVXiOgagD7/lewECgYEAj2DtOiQFjxNFRCp9jx7J\n" +
                "xF1zvY7gYy5a3EW3yoxM7lWr7bqsb+c1m4APGlKIdqKRLpwBcKV/XMVj62d6r/Cm\n" +
                "V4MuMShwdRHLG0QzwGiIyNg7qmZTxDmLE/drbo/QFPTqzH1H5oBBz28fjW9B2L8Q\n" +
                "iSvGJLMkgCIhUDf9fEal9Pw=\n" +
                "-----END PRIVATE KEY-----\n"

        const val CLIENT_CERTIFICATE_CHAIN_THREE_BUNDLE = "-----BEGIN CERTIFICATE-----\n" +
                "MIIEnzCCA4egAwIBAgIUPxuB4M+yvTVP6ls52hfp6rgPbdkwDQYJKoZIhvcNAQEL\n" +
                "BQAwfzELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRQwEgYDVQQHEwtMb3MgQW5n\n" +
                "ZWxlczEZMBcGA1UECxMQTmV0a2kgT3BlcmF0aW9uczEyMDAGA1UEAxMpVHJhbnNh\n" +
                "Y3RJRCBJbnRlcm1lZGlhdGUgQ0FpIC0gREVWRUxPUE1FTlQwHhcNMjEwMzIzMDAw\n" +
                "OTAwWhcNMjQwMzIyMDAwOTAwWjCBtjEJMAcGA1UEBhMAMQ0wCwYDVQQIEwRMRUdM\n" +
                "MRwwGgYDVQQHExNsZWdhbFBlcnNvbk5hbWVUeXBlMRgwFgYDVQQKEw9sZWdhbFBl\n" +
                "cnNvbk5hbWUxJDAiBgNVBAsTG2xlZ2FsUGVyc29uLmxlZ2FsUGVyc29uTmFtZTE8\n" +
                "MDoGA1UEAxMzVGhpcyBpcyB0aGUgZGF0YSBmb3IgbmF0dXJhbFBlcnNvblByaW1h\n" +
                "cnlJZGVudGlmaWVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlYfD\n" +
                "Kfz0tqIEd78Xftj84PNpr++bQ5k3Zrzg4HdjwFbCiGRWCRX8VhpFsx1wM5F1ytpG\n" +
                "fRxiJSPZDTj1nfK4TF1ZMqHOlgyVFjRAlyzDCGsErVH7tppmYLFiX5/oCUvO7bIY\n" +
                "6EDUvm8TAO1DpVlake/Rmam/LaPYuEoBemxa9JwRNQIBuPnBCfcWyYqME/Bh8kqR\n" +
                "6BX/whDXumCR4xcuGZ/2KwJNIS/OoD79wvSbUNvahI88DfnzodP4YEs24fVcDZB5\n" +
                "wXFYWt6VkwPfdLWnxk3mzlHOmpjrT1YIua9DHRqQNq5DYqMwiozbMxBUfHdBEfHj\n" +
                "Z63d6ezE232OYSDzkwIDAQABo4HaMIHXMA4GA1UdDwEB/wQEAwIFoDATBgNVHSUE\n" +
                "DDAKBggrBgEFBQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBSPT6mFkPdqjt0P\n" +
                "QZtbAkV2DrDixjAfBgNVHSMEGDAWgBQaYTgDPKdAkbtPdww4nGGJG7HOwTA0Bggr\n" +
                "BgEFBQcBAQQoMCYwJAYIKwYBBQUHMAGGGGh0dHBzOi8vb2NzcC5teXZlcmlmeS5p\n" +
                "bzAsBgNVHR8EJTAjMCGgH6AdhhtodHRwczovL2NybC5teXZlcmlmeS5pby9jcmww\n" +
                "DQYJKoZIhvcNAQELBQADggEBAGM+j6BUb/w9ga+C8xM+T3PowEs7fZhmOmDXJyaL\n" +
                "cO6V/qfRR4xg1CrK1M0uADCyqT2M5X67bwt7vNgCt92uH5Z/0t79HCK00Yn39cu/\n" +
                "YGdR2itiTwrtLDj48ESXe6V7kqTYWH4fpSRE1TdnHq5PD61XFzox5Pa5dVIggY/K\n" +
                "MWbxeAbEx7v+Ou1QG6ie99tYlWuNz4fKAPLMUYZ3ky/0m1dGMQqyi/R3AD0fUZVO\n" +
                "6EhyhJm6j5KCgHlJJxng7NxpSu8UZiHEkvWwuD06Dhqk2fETyxsbaizM/bFOkYXe\n" +
                "RLE0biO8RsfOKKFnL3Y9VFo4HYWClH1OmcJQY6tNQdv7lnY=\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDfDCCAmSgAwIBAgIUHc495E2/hVTOTGms5/wmSGUHywYwDQYJKoZIhvcNAQEL\n" +
                "BQAwVjELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRQwEgYDVQQHEwtMb3MgQW5n\n" +
                "ZWxlczEkMCIGA1UEAxMbVHJhbnNhY3RJRCBDQSAtIERFVkVMT1BNRU5UMB4XDTIw\n" +
                "MDgyMTIwMDkwMFoXDTI1MDgyMDIwMDkwMFowVjELMAkGA1UEBhMCVVMxCzAJBgNV\n" +
                "BAgTAkNBMRQwEgYDVQQHEwtMb3MgQW5nZWxlczEkMCIGA1UEAxMbVHJhbnNhY3RJ\n" +
                "RCBDQSAtIERFVkVMT1BNRU5UMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\n" +
                "AQEAuL1mlAsIxDqihuQ3a3MQiowwdoL3PlaVdKgCNVnbwOnmsxb6pMlaUddG/wPA\n" +
                "OSICewm/Ss36FvTeRCKfBrYb95/+/b7nHkKKI2Cy+pGGyKVXZrOH/g8YqnwXqirY\n" +
                "vso1u9zx5BPr6xlx/ycWlw0bqLMxHeH8EKmvR1F3CRMlr0/ZJPAdLoADDqSql/Bh\n" +
                "bmhBRn2kInS+rsqnehAbLFFMkQmbqesxpD0Gx/5aoOsAAzLzntTvtiW5m6qzFOO2\n" +
                "pP0AORcx9BKn4v1/rDwky3lSVYhxy7Tt86rDIdwY/oxDg83EVX2Z9B0LRNwrVNOc\n" +
                "/EtrplmEhnG1Y0OCYqqdbs9YAwIDAQABo0IwQDAOBgNVHQ8BAf8EBAMCAQYwDwYD\n" +
                "VR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUgqHDe4vXWJaIXLGU/L6/aHkJq1cwDQYJ\n" +
                "KoZIhvcNAQELBQADggEBAC7AnTzrGMF0pkV8gJQ8zanoWfJgYzJEYp7Cyu/23FwB\n" +
                "88Govd3zly3iYtlj3iyN73Ejf1keAnjTWhXinpz/PD7JrKJaTKN6gVzOnZyuJkoS\n" +
                "TEUnmTy7nIqVtFG/ty78wB5uPLV/490bvplNQoqOHdHpUG6NlhF7wJ9IwO7k6BgO\n" +
                "1iLgZ2gzZqxroe+G3T2zSFcwJNySpOU6w1vSYZ0JXzubjhDj5TjsHwNWSBC3CNue\n" +
                "1GPqxI3+W+Z8JgT3HcjCgxiCSd8SzSHAGcXtt/rSrLAKTVqJ3me52q88S0xL5ZLo\n" +
                "coNJ2JRCtcmUeyd/bMcYtME+s2oA3cnd327Ww68GmjM=\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDyTCCArGgAwIBAgIUEsd5/1wD9wI2pJrnegdVzWHzv6EwDQYJKoZIhvcNAQEL\n" +
                "BQAwVjELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRQwEgYDVQQHEwtMb3MgQW5n\n" +
                "ZWxlczEkMCIGA1UEAxMbVHJhbnNhY3RJRCBDQSAtIERFVkVMT1BNRU5UMB4XDTIw\n" +
                "MDgyMTIwMzYwMFoXDTMwMDgxOTIwMzYwMFowfzELMAkGA1UEBhMCVVMxCzAJBgNV\n" +
                "BAgTAkNBMRQwEgYDVQQHEwtMb3MgQW5nZWxlczEZMBcGA1UECxMQTmV0a2kgT3Bl\n" +
                "cmF0aW9uczEyMDAGA1UEAxMpVHJhbnNhY3RJRCBJbnRlcm1lZGlhdGUgQ0FpIC0g\n" +
                "REVWRUxPUE1FTlQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDIdHsT\n" +
                "PeapL9PlskArENjwfZb5/Q/BE6ty2tgv1CPkAwn8vDKUyrqiBqjoYloikcIbtKW3\n" +
                "+C/KJbZauCqhRUskpMJhfgvMmy/ZtXz133VyWtwyP1yQI+T7AzTy4liOxxOUnU41\n" +
                "iFPvxi87rY8pFLOPRDaOO9ysSqxmTr+NYvRK98psGfb6bI0mTSeo1SnbbD9SvOyc\n" +
                "Esrad0epUjEORT9yDLQHVFJSdTytfMq/aRPpaSvSTCXVrbHE4GYw4K2sSM8Z/5qZ\n" +
                "PkkNWM7d9s0B8q0kNE0YUEwgBnwcTFOpaLvSRWj8xk2jzp2eYFscw0IV4PcfVQ0n\n" +
                "ddgYEgYR0euU0AYtAgMBAAGjZjBkMA4GA1UdDwEB/wQEAwIBhjASBgNVHRMBAf8E\n" +
                "CDAGAQH/AgEAMB0GA1UdDgQWBBQaYTgDPKdAkbtPdww4nGGJG7HOwTAfBgNVHSME\n" +
                "GDAWgBSCocN7i9dYlohcsZT8vr9oeQmrVzANBgkqhkiG9w0BAQsFAAOCAQEAcA8B\n" +
                "ZC5nrP7UmUmvJKU01/Kbgz5ynNVOpItz3Nux1rYWhMl7tAHVWoaJpJuPKWsQ8YVu\n" +
                "LacspQqp1iDTgZLfKVAtFaPKWKAQf+Uvr9jva1SSFwVzL8YCWxlg6oM6BRMayxdC\n" +
                "Atitogq0L8wqVHNWP6TMCWyqiGk5Rt7+TEM1lCXbtGyrxYo3gZEZgbui6trCMXAc\n" +
                "Ggz50Yc6frvK2v8qIXlfF6gipDOUHBWkjjRVedYfyZAMLnDFpz/hvZRE2S3D2oPE\n" +
                "IM35GPBjK3/GXbkwDlJw3zaetzfYn3Ra8CE/KsYKS1Mr6/cwO4bnxlbERawUwF5g\n" +
                "VTpsOzZSAtU+EPzGDA==\n" +
                "-----END CERTIFICATE-----\n"

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

    object Originators {
        val PRIMARY_ORIGINATOR_PKI_X509SHA256 = OriginatorParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val NO_PRIMARY_ORIGINATOR_PKI_X509SHA256 = OriginatorParameters(
            false,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )

        val PRIMARY_ORIGINATOR_PKI_X509SHA256_INVALID_CERTIFICATE = OriginatorParameters(
            true,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256_INVALID_CERTIFICATE, PKI_DATA_TWO_OWNER_X509SHA256)
        )
    }

    object PkiData {
        val PKI_DATA_ONE_OWNER_X509SHA256 = PkiDataParameters(
            attestation = Attestation.LEGAL_PERSON_NAME,
            privateKeyPem = KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE,
            certificatePem = KeyPairs.CLIENT_CERTIFICATE_CHAIN_ONE,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_TWO_OWNER_X509SHA256 = PkiDataParameters(
            attestation = Attestation.LEGAL_PERSON_PHONETIC_NAME_IDENTIFIER,
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_TWO,
            certificatePem = KeyPairs.CLIENT_CERTIFICATE_CHAIN_TWO,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_SENDER_X509SHA256_INVALID_CERTIFICATE = PkiDataParameters(
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_TWO,
            certificatePem = KeyPairs.CLIENT_CERTIFICATE_RANDOM,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_ONE_OWNER_X509SHA256_INVALID_CERTIFICATE = PkiDataParameters(
            attestation = Attestations.INVALID_ATTESTATION,
            privateKeyPem = KeyPairs.CLIENT_PRIVATE_KEY_CHAIN_ONE,
            certificatePem = KeyPairs.CLIENT_CERTIFICATE_RANDOM,
            type = PkiType.X509SHA256
        )

        val PKI_DATA_SENDER_X509SHA256 = PkiDataParameters(
            privateKeyPem = CLIENT_PRIVATE_KEY_CHAIN_TWO,
            certificatePem = KeyPairs.CLIENT_CERTIFICATE_CHAIN_TWO,
            type = PkiType.X509SHA256
        )
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

        val NO_PRIMARY_BENEFICIARY_PKI_X509SHA256 = BeneficiaryParameters(
            false,
            listOf(PKI_DATA_ONE_OWNER_X509SHA256, PKI_DATA_TWO_OWNER_X509SHA256)
        )
    }
    object Senders {
        val SENDER_PKI_X509SHA256 = SenderParameters(
            pkiDataParameters = PKI_DATA_SENDER_X509SHA256,
            evCertificatePem = EV_CERT
        )

        val SENDER_PKI_X509SHA256_INVALID_CERTIFICATE = SenderParameters(
            PKI_DATA_SENDER_X509SHA256_INVALID_CERTIFICATE
        )
    }

    object Output {
        val OUTPUTS = listOf(
            Output(1000, "Script 1", AddressCurrency.BITCOIN),
            Output(2000, "Script 2", AddressCurrency.BITCOIN)
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
}

