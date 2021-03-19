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

