package com.netki.address.info.main.impl

import com.google.gson.Gson
import com.netki.address.info.repo.impl.MerkleRepo
import com.netki.address.info.service.impl.AddressInformationNetkiService
import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.util.TestData.Address.MERKLE_ADDRESS_INFORMATION
import com.netki.util.fullUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AddressInformationNetkiTest {

    private lateinit var addressInformation: AddressInformationProviderNetki
    private lateinit var addressInformationService: AddressInformationNetkiService
    private lateinit var merkleRepo: MerkleRepo
    private lateinit var client: HttpClient

    private val addressCurrency = AddressCurrency.BITCOIN
    private val address = "0x021r02389jsdf908234234"
    private val gson = Gson()

    @BeforeAll
    fun setUp() {
    }

    @Test
    fun `Fetch the information of an address successfully`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v2.1/addresses/" -> {
                            respond(
                                gson.toJson(MERKLE_ADDRESS_INFORMATION),
                                HttpStatusCode.OK,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }

            }
        }

        merkleRepo = MerkleRepo(client, "mock_key", Gson())
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val addressInformation = addressInformation.getAddressInformation(addressCurrency, address)

        assertEquals(addressInformation.identifier, MERKLE_ADDRESS_INFORMATION.identifier)
        assertEquals(addressInformation.alerts?.size, MERKLE_ADDRESS_INFORMATION.merkleAddressAlerts?.size)
        assertEquals(addressInformation.balance, MERKLE_ADDRESS_INFORMATION.balance)
        assertEquals(addressInformation.currency, MERKLE_ADDRESS_INFORMATION.currency)
        assertEquals(addressInformation.currencyVerbose, MERKLE_ADDRESS_INFORMATION.currencyVerbose)
        assertEquals(addressInformation.earliestTransactionTime, MERKLE_ADDRESS_INFORMATION.earliestTransactionTime)
        assertEquals(addressInformation.latestTransactionTime, MERKLE_ADDRESS_INFORMATION.latestTransactionTime)
        assertEquals(addressInformation.riskLevel, MERKLE_ADDRESS_INFORMATION.riskLevel)
        assertEquals(addressInformation.riskLevelVerbose, MERKLE_ADDRESS_INFORMATION.riskLevelVerbose)
        assertEquals(addressInformation.totalIncomingValue, MERKLE_ADDRESS_INFORMATION.totalIncomingValue)
        assertEquals(addressInformation.totalIncomingValueUsd, MERKLE_ADDRESS_INFORMATION.totalIncomingValueUsd)
        assertEquals(addressInformation.totalOutgoingValue, MERKLE_ADDRESS_INFORMATION.totalOutgoingValue)
        assertEquals(addressInformation.totalOutgoingValueUsd, MERKLE_ADDRESS_INFORMATION.totalOutgoingValueUsd)
        assertEquals(addressInformation.createdAt, MERKLE_ADDRESS_INFORMATION.createdAt)
        assertEquals(addressInformation.updatedAt, MERKLE_ADDRESS_INFORMATION.updatedAt)

        addressInformation.alerts?.forEach { alert ->
            run {
                MERKLE_ADDRESS_INFORMATION.merkleAddressAlerts?.forEach { merkleAlert ->
                    if (alert.ruleName == merkleAlert.ruleName) {
                        assertEquals(alert.riskLevel, merkleAlert.riskLevel)
                        assertEquals(alert.riskLevelVerbose, merkleAlert.riskLevelVerbose)
                        assertEquals(alert.riskTypes?.size, merkleAlert.merkleAddressRiskTypes?.size)
                        assertEquals(alert.context, "{}")
                        assertEquals(alert.createdAt, merkleAlert.createdAt)
                    }
                }
            }
        }
    }

    @Test
    fun `fetch address information that does not exist`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v2.1/addresses/" -> {
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

        merkleRepo = MerkleRepo(client, "mock_key", Gson())
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val addressInformationDetailed = addressInformation.getAddressInformation(addressCurrency, address)

        assert(addressInformationDetailed.identifier.isNullOrBlank())
        assert(addressInformationDetailed.alerts?.size == 0)
    }

    @Test
    fun `fetch address information with not correct authorization`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v2.1/addresses/" -> {
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

        merkleRepo = MerkleRepo(client, "mock_key", Gson())
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val exception = Assertions.assertThrows(AddressProviderUnauthorizedException::class.java) {
            addressInformation.getAddressInformation(addressCurrency, address)
        }

        assert(exception.message != null && exception.message!!.contains("Provider authorization error for address:"))
    }

    @Test
    fun `fetch address information with Provider throwing an error`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v2.1/addresses/" -> {
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

        merkleRepo = MerkleRepo(client, "mock_key", Gson())
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val exception = Assertions.assertThrows(AddressProviderErrorException::class.java) {
            addressInformation.getAddressInformation(addressCurrency, address)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }
}
