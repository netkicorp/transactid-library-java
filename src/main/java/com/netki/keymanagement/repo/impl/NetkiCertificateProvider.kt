package com.netki.keymanagement.repo.impl

import com.netki.exceptions.CertificateProviderException
import com.netki.exceptions.CertificateProviderUnauthorizedException
import com.netki.keymanagement.repo.CertificateProvider
import com.netki.keymanagement.repo.data.*
import com.netki.model.Attestation
import com.netki.util.ErrorInformation.CERTIFICATE_INFORMATION_INTERNAL_ERROR_PROVIDER
import com.netki.util.ErrorInformation.CERTIFICATE_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking

const val NETKI_BASE_URL = "https://kyc.myverify.info/"
const val ATTESTATION_REQUEST_PATH = "api/attestation-request/"
const val MAKE_CERTIFICATE_PATH = "api/transactions/%s/make-certificates/"
const val CERTIFICATE_PATH = "api/transactions/%s/certificates/"

class NetkiCertificateProvider(
    private val client: HttpClient,
    private val authorizationKey: String
) : CertificateProvider {

    /**
     * {@inheritDoc}
     */
    override fun requestTransactionId(attestations: List<Attestation>): String {
        val attestationsField = attestations.map { AttestationField(it) }
        val attestationsRequested = AttestationsRequest(attestationsField)

        val attestationSubmitted = client.postHandlingExceptions<AttestationResponse>(
            authorizationKey,
            "$NETKI_BASE_URL$ATTESTATION_REQUEST_PATH",
            attestationsRequested
        )
        return attestationSubmitted.transaction
    }

    /**
     * {@inheritDoc}
     */
    override fun submitCsrsAttestations(transactionId: String, csrsAttestations: List<CsrAttestation>) {
        client.postHandlingExceptions<String>(
            authorizationKey,
            String.format("$NETKI_BASE_URL$MAKE_CERTIFICATE_PATH", transactionId),
            CsrAttestationRequest(csrsAttestations)
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun getCertificates(transactionId: String) = client.getHandlingExceptions<CertificateAttestationResponse>(
        authorizationKey,
        String.format("$NETKI_BASE_URL$CERTIFICATE_PATH", transactionId)
    )
}

inline fun <reified T> HttpClient.postHandlingExceptions(
    authorizationKey: String,
    url: String,
    bodyRequest: Any
): T {
    val client = this
    return try {
        runBlocking {
            client.post<T>(url) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Token $authorizationKey")
                body = bodyRequest
            }
        }
    } catch (exception: Exception) {
        when (exception) {
            is ServerResponseException -> {
                when (exception.response.status.value) {
                    HttpStatusCode.InternalServerError.value -> throw CertificateProviderException(
                        String.format(
                            CERTIFICATE_INFORMATION_INTERNAL_ERROR_PROVIDER,
                            bodyRequest,
                            exception.localizedMessage
                        )
                    )
                    else -> {
                        throw CertificateProviderException(exception.message)
                    }
                }
            }
            is ClientRequestException -> {
                when (exception.response.status.value) {
                    HttpStatusCode.BadRequest.value -> throw CertificateProviderException(
                        String.format(
                            CERTIFICATE_INFORMATION_INTERNAL_ERROR_PROVIDER,
                            bodyRequest,
                            exception.localizedMessage
                        )
                    )
                    HttpStatusCode.Unauthorized.value -> throw CertificateProviderUnauthorizedException(
                        CERTIFICATE_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER
                    )
                    else -> throw CertificateProviderException(exception.message)
                }
            }
            else -> throw CertificateProviderException(exception.message)
        }
    }
}

inline fun <reified T> HttpClient.getHandlingExceptions(
    authorizationKey: String,
    url: String
): T {
    val client = this
    return try {
        runBlocking {
            client.get<T>(url) {
                header(HttpHeaders.Authorization, "Token $authorizationKey")
            }
        }
    } catch (exception: Exception) {
        when (exception) {
            is ServerResponseException -> {
                when (exception.response.status.value) {
                    HttpStatusCode.InternalServerError.value -> throw CertificateProviderException(
                        String.format(
                            CERTIFICATE_INFORMATION_INTERNAL_ERROR_PROVIDER,
                            url,
                            exception.localizedMessage
                        )
                    )
                    else -> {
                        throw CertificateProviderException(exception.message)
                    }
                }
            }
            is ClientRequestException -> {
                when (exception.response.status.value) {
                    HttpStatusCode.BadRequest.value -> throw CertificateProviderException(
                        String.format(
                            CERTIFICATE_INFORMATION_INTERNAL_ERROR_PROVIDER,
                            url,
                            exception.localizedMessage
                        )
                    )
                    HttpStatusCode.Unauthorized.value -> throw CertificateProviderUnauthorizedException(
                        CERTIFICATE_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER
                    )
                    else -> throw CertificateProviderException(exception.message)
                }
            }
            else -> throw CertificateProviderException(exception.message)
        }
    }
}

