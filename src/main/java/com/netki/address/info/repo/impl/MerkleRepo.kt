package com.netki.address.info.repo.impl

import com.google.gson.Gson
import com.netki.address.info.repo.AddressInformationRepo
import com.netki.address.info.repo.data.MerkleAddress
import com.netki.address.info.repo.data.toAddressInformation
import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.model.AddressInformation
import com.netki.util.ErrorInformation.ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER
import com.netki.util.ErrorInformation.ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import kotlinx.coroutines.runBlocking

/**
 * Implementation to fetch the address information from Merkle provider.
 */
private const val X_API_KEY_HEADER = "X-API-KEY"
private const val IDENTIFIER_PARAM = "identifier"
private const val CURRENCY_PARAM = "currency"
private const val MERKLE_BASE_URL = "https://api.merklescience.com/"
private const val ADDRESS_INFO_PATH = "api/v3/addresses/"

internal class MerkleRepo(
    private val client: HttpClient,
    private val authorizationKey: String,
    private val gson: Gson
) : AddressInformationRepo {

    /**
     * {@inheritDoc}
     */
    override fun getAddressInformation(currency: AddressCurrency, address: String): AddressInformation {
        return try {
            runBlocking {
                client.post<MerkleAddress>("$MERKLE_BASE_URL$ADDRESS_INFO_PATH") {
                    header(X_API_KEY_HEADER, authorizationKey)
                    body = MultiPartFormDataContent(formData {
                        append(IDENTIFIER_PARAM, address)
                        append(CURRENCY_PARAM, currency.id)
                    })
                }
            }.toAddressInformation()
        } catch (exception: Exception) {
            when (exception) {
                is ServerResponseException -> {
                    when (exception.response.status.value) {
                        InternalServerError.value -> throw AddressProviderErrorException(
                            String.format(
                                ADDRESS_INFORMATION_INTERNAL_ERROR_PROVIDER,
                                address,
                                exception.localizedMessage
                            )
                        )
                        else -> {
                            throw AddressProviderErrorException(exception.message)
                        }
                    }
                }
                is ClientRequestException -> {
                    when (exception.response.status.value) {
                        BadRequest.value -> return AddressInformation()
                        Unauthorized.value -> throw AddressProviderUnauthorizedException(
                            String.format(
                                ADDRESS_INFORMATION_NOT_AUTHORIZED_ERROR_PROVIDER,
                                address
                            )
                        )
                        else -> throw AddressProviderErrorException(exception.message)
                    }
                }
                else -> throw AddressProviderErrorException(exception.message)
            }
        }
    }
}
