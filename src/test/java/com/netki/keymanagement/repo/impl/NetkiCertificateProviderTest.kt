package com.netki.keymanagement.repo.impl

import com.google.gson.Gson
import com.netki.exceptions.CertificateProviderException
import com.netki.exceptions.CertificateProviderUnauthorizedException
import com.netki.util.TestData.CertificateGeneration.ATTESTATIONS_REQUESTED
import com.netki.util.TestData.CertificateGeneration.ATTESTATIONS_SUBMITTED
import com.netki.util.TestData.CertificateGeneration.CERTIFICATE_ATTESTATION_RESPONSE
import com.netki.util.TestData.CertificateGeneration.CSRS_ATTESTATIONS
import com.netki.util.TestData.CertificateGeneration.TRANSACTION_ID
import com.netki.util.fullUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

private const val NETKI_BASE_URL = "https://kyc.myverify.info/"
private const val ATTESTATION_REQUEST_PATH = "api/attestation-request/"
private const val MAKE_CERTIFICATE_PATH = "api/transactions/%s/make-certificates/"
private const val CERTIFICATE_PATH = "api/transactions/%s/certificates/"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NetkiCertificateProviderTest {
    private lateinit var netkiCertificateProvider: NetkiCertificateProvider
    private lateinit var client: HttpClient

    val key = "fake_key"
    private val gson = Gson()

    @BeforeAll
    fun setUp() {
    }

    @Test
    fun `Create transactionId successfully for a list of attestations`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "$NETKI_BASE_URL$ATTESTATION_REQUEST_PATH" -> {
                            respond(
                                gson.toJson(ATTESTATIONS_SUBMITTED),
                                HttpStatusCode.OK,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val transactionId = netkiCertificateProvider.requestTransactionId(ATTESTATIONS_REQUESTED)

        assertEquals(transactionId, ATTESTATIONS_SUBMITTED.transaction)
    }

    @Test
    fun `Retrieve transactionId with a bad request`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "$NETKI_BASE_URL$ATTESTATION_REQUEST_PATH" -> {
                            respond(
                                "{}",
                                HttpStatusCode.BadRequest,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderException::class.java) {
            netkiCertificateProvider.requestTransactionId(ATTESTATIONS_REQUESTED)
        }

        assertTrue(exception.localizedMessage.contains("400"))
    }

    @Test
    fun `Retrieve transactionId with a provider internal error`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "$NETKI_BASE_URL$ATTESTATION_REQUEST_PATH" -> {
                            respond(
                                "{}",
                                HttpStatusCode.InternalServerError,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderException::class.java) {
            netkiCertificateProvider.requestTransactionId(ATTESTATIONS_REQUESTED)
        }

        assertTrue(exception.localizedMessage.contains("Provider internal error for:"))
    }

    @Test
    fun `Retrieve transactionId with an unauthorized error`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "$NETKI_BASE_URL$ATTESTATION_REQUEST_PATH" -> {
                            respond(
                                "{}",
                                HttpStatusCode.Unauthorized,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderUnauthorizedException::class.java) {
            netkiCertificateProvider.requestTransactionId(ATTESTATIONS_REQUESTED)
        }

        assertTrue(exception.localizedMessage.contains("Provider authorization error."))
    }

    @Test
    fun `Submit CSRs related to a transactionId successfully`() {
        val requestUrl = String.format("$NETKI_BASE_URL$MAKE_CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                gson.toJson(ATTESTATIONS_SUBMITTED),
                                HttpStatusCode.OK,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        netkiCertificateProvider.submitCsrsAttestations(TRANSACTION_ID, CSRS_ATTESTATIONS)
    }

    @Test
    fun `Submit CSRs related to a transactionId with a bad request`() {
        val requestUrl = String.format("$NETKI_BASE_URL$MAKE_CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                "{}",
                                HttpStatusCode.BadRequest,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)


        val exception = assertThrows(CertificateProviderException::class.java) {
            netkiCertificateProvider.submitCsrsAttestations(TRANSACTION_ID, CSRS_ATTESTATIONS)
        }

        assertTrue(exception.localizedMessage.contains("400"))
    }

    @Test
    fun `Submit CSRs related to a transactionId with a provider internal error`() {
        val requestUrl = String.format("$NETKI_BASE_URL$MAKE_CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                "{}",
                                HttpStatusCode.InternalServerError,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderException::class.java) {
            netkiCertificateProvider.submitCsrsAttestations(TRANSACTION_ID, CSRS_ATTESTATIONS)
        }

        assertTrue(exception.localizedMessage.contains("Provider internal error for:"))
    }

    @Test
    fun `Submit CSRs related to a transactionId with an unauthorized error`() {
        val requestUrl = String.format("$NETKI_BASE_URL$MAKE_CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                "{}",
                                HttpStatusCode.Unauthorized,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderUnauthorizedException::class.java) {
            netkiCertificateProvider.submitCsrsAttestations(TRANSACTION_ID, CSRS_ATTESTATIONS)
        }

        assertTrue(exception.localizedMessage.contains("Provider authorization error."))
    }


    @Test
    fun `Fetch certificates for a transactionId successfully`() {
        val requestUrl = String.format("$NETKI_BASE_URL$CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                gson.toJson(CERTIFICATE_ATTESTATION_RESPONSE),
                                HttpStatusCode.OK,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val certificatesAttestations = netkiCertificateProvider.getCertificates(TRANSACTION_ID)

        assertEquals(certificatesAttestations.count, 3)
        assertEquals(certificatesAttestations.certificates.size, 3)
    }

    @Test
    fun `Fetch certificates for a transactionId with a bad request`() {
        val requestUrl = String.format("$NETKI_BASE_URL$CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                "{}",
                                HttpStatusCode.BadRequest,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderException::class.java) {
            netkiCertificateProvider.getCertificates(TRANSACTION_ID)
        }

        assertTrue(exception.localizedMessage.contains("400"))
    }

    @Test
    fun `Fetch certificates for a transactionId with a provider internal error`() {
        val requestUrl = String.format("$NETKI_BASE_URL$CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                "{}",
                                HttpStatusCode.InternalServerError,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderException::class.java) {
            netkiCertificateProvider.getCertificates(TRANSACTION_ID)
        }

        assertTrue(exception.localizedMessage.contains("Provider internal error for:"))
    }


    @Test
    fun `Fetch certificates for a transactionId  with an unauthorized error`() {
        val requestUrl = String.format("$NETKI_BASE_URL$CERTIFICATE_PATH", TRANSACTION_ID)
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        requestUrl -> {
                            respond(
                                "{}",
                                HttpStatusCode.Unauthorized,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }
            }
        }

        netkiCertificateProvider = NetkiCertificateProvider(client, key)

        val exception = assertThrows(CertificateProviderUnauthorizedException::class.java) {
            netkiCertificateProvider.getCertificates(TRANSACTION_ID)
        }

        assertTrue(exception.localizedMessage.contains("Provider authorization error."))

    }

}
