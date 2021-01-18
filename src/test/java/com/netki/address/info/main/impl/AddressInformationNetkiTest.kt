package com.netki.address.info.main.impl

import com.netki.address.info.repo.impl.MerkleRepo
import com.netki.address.info.service.impl.AddressInformationNetkiService
import com.netki.exceptions.AddressProviderErrorException
import com.netki.exceptions.AddressProviderUnauthorizedException
import com.netki.model.AddressCurrency
import com.netki.util.TestData.Address.MERKLE_JSON_RESPONSE
import com.netki.util.fullUrl
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

    @BeforeAll
    fun setUp() {
        // Nothing to do here
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
                        "https://api.merklescience.com/api/v3/addresses/" -> {
                            respond(
                                MERKLE_JSON_RESPONSE,
                                HttpStatusCode.OK,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }

            }
        }

        merkleRepo = MerkleRepo(client, "mock_key")
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val addressInformation = addressInformation.getAddressInformation(addressCurrency, address)

        assertEquals("0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48", addressInformation.identifier)
        assertEquals(10.0, addressInformation.balance)
        assertEquals(1, addressInformation.currency)
        assertEquals("Ethereum", addressInformation.currencyVerbose)
        assertEquals("2018-08-03T19:30:30Z", addressInformation.earliestTransactionTime)
        assertEquals("2020-10-09T03:22:20Z", addressInformation.latestTransactionTime)
        assertEquals(3, addressInformation.riskLevel)
        assertEquals("High Risk", addressInformation.riskLevelVerbose)
        assertEquals("0.0000", addressInformation.totalIncomingValue)
        assertEquals("397181.45", addressInformation.totalIncomingValueUsd)
        assertEquals("0.0100", addressInformation.totalOutgoingValue)
        assertEquals("9.00", addressInformation.totalOutgoingValueUsd)
        assertEquals("2020-07-06T11:44:19.210445Z", addressInformation.createdAt)
        assertEquals("2020-10-09T03:23:46.836948Z", addressInformation.updatedAt)

        assertEquals(2, addressInformation.originator?.size)
        assertEquals("Exchange", addressInformation.originator?.get(0)?.tagTypeVerbose)
        assertEquals("Mandatory KYC and AML", addressInformation.originator?.get(0)?.tagSubtypeVerbose)
        assertEquals("Bittrex", addressInformation.originator?.get(0)?.tagNameVerbose)
        assertEquals("23310.30", addressInformation.originator?.get(0)?.totalValueUsd)
        assertEquals(null, addressInformation.originator?.get(1)?.tagTypeVerbose)
        assertEquals(null, addressInformation.originator?.get(1)?.tagSubtypeVerbose)
        assertEquals(null, addressInformation.originator?.get(1)?.tagNameVerbose)
        assertEquals("376903.01", addressInformation.originator?.get(1)?.totalValueUsd)

        assertEquals(1, addressInformation.beneficiary?.size)
        assertEquals(null, addressInformation.beneficiary?.get(0)?.tagTypeVerbose)
        assertEquals(null, addressInformation.beneficiary?.get(0)?.tagSubtypeVerbose)
        assertEquals(null, addressInformation.beneficiary?.get(0)?.tagNameVerbose)
        assertEquals("1855.02", addressInformation.beneficiary?.get(0)?.totalValueUsd)

        assertEquals(null, addressInformation.tags?.owner?.tagTypeVerbose)
        assertEquals(null, addressInformation.tags?.owner?.tagSubtypeVerbose)
        assertEquals(null, addressInformation.tags?.owner?.tagNameVerbose)

        assertEquals("Smart Contract Platform", addressInformation.tags?.user?.tagTypeVerbose)
        assertEquals("Token", addressInformation.tags?.user?.tagSubtypeVerbose)
        assertEquals("USD Coin", addressInformation.tags?.user?.tagNameVerbose)

        assertTrue(addressInformation.alerts!!.isEmpty())
    }

    @Test
    fun `Fetch address information that does not exist`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v3/addresses/" -> {
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

        merkleRepo = MerkleRepo(client, "mock_key")
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val addressInformationDetailed = addressInformation.getAddressInformation(addressCurrency, address)

        assert(addressInformationDetailed.identifier.isNullOrBlank())
        assert(addressInformationDetailed.alerts?.size == 0)
    }

    @Test
    fun `Fetch address information with not correct authorization`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v3/addresses/" -> {
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

        merkleRepo = MerkleRepo(client, "mock_key")
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val exception = Assertions.assertThrows(AddressProviderUnauthorizedException::class.java) {
            addressInformation.getAddressInformation(addressCurrency, address)
        }

        assert(exception.message != null && exception.message!!.contains("Provider authorization error for address:"))
    }

    @Test
    fun `Fetch address information with Provider throwing an internal server error`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v3/addresses/" -> {
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

        merkleRepo = MerkleRepo(client, "mock_key")
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        val exception = Assertions.assertThrows(AddressProviderErrorException::class.java) {
            addressInformation.getAddressInformation(addressCurrency, address)
        }

        assert(exception.message != null && exception.message!!.contains("Provider internal error for address:"))
    }

    @Test
    fun `Fetch address information with Provider throwing an unhandled error`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v3/addresses/" -> {
                            respond(
                                "{}",
                                HttpStatusCode.NotImplemented,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }

            }
        }

        merkleRepo = MerkleRepo(client, "mock_key")
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        Assertions.assertThrows(AddressProviderErrorException::class.java) {
            addressInformation.getAddressInformation(addressCurrency, address)
        }
    }

    @Test
    fun `Fetch address information with unhandled ClientRequestException`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v3/addresses/" -> {
                            respond(
                                "{}",
                                HttpStatusCode.NotAcceptable,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }

            }
        }

        merkleRepo = MerkleRepo(client, "mock_key")
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        Assertions.assertThrows(AddressProviderErrorException::class.java) {
            addressInformation.getAddressInformation(addressCurrency, address)
        }
    }

    @Test
    fun `Fetch address information with unhandled RedirectResponseException`() {
        client = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "https://api.merklescience.com/api/v3/addresses/" -> {
                            respond(
                                "{}",
                                HttpStatusCode.PermanentRedirect,
                                headersOf("Content-Type", ContentType.Application.Json.toString())
                            )
                        }
                        else -> error("Unhandled ${request.url.fullUrl}")
                    }
                }

            }
        }

        merkleRepo = MerkleRepo(client, "mock_key")
        addressInformationService = AddressInformationNetkiService(merkleRepo)
        addressInformation = AddressInformationProviderNetki(addressInformationService)

        Assertions.assertThrows(AddressProviderErrorException::class.java) {
            addressInformation.getAddressInformation(addressCurrency, address)
        }
    }
}
