package com.netki.bip75.config

import com.netki.address.info.repo.impl.MerkleRepo
import com.netki.address.info.service.impl.AddressInformationNetkiService
import com.netki.bip75.main.Bip75
import com.netki.bip75.main.impl.Bip75Netki
import com.netki.bip75.processor.impl.InvoiceRequestProcessor
import com.netki.bip75.processor.impl.PaymentAckProcessor
import com.netki.bip75.processor.impl.PaymentProcessor
import com.netki.bip75.processor.impl.PaymentRequestProcessor
import com.netki.bip75.service.Bip75Service
import com.netki.bip75.service.impl.Bip75ServiceNetki
import com.netki.security.CertificateValidator
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

/**
 * Factory to generate Bip75 instance.
 */
internal object Bip75Factory {

    /**
     * Get an instance of Bip75.
     * @param trustStoreLocation is a path in the server where the app is running that contains the valid certificate chains.
     * @param authorizationKey pass this parameter if address information will be required.
     * @return Bip75 instance.
     */
    @JvmOverloads
    fun getInstance(
        trustStoreLocation: String,
        authorizationKey: String? = null,
        developmentMode: Boolean = false
    ): Bip75 {
        Security.addProvider(BouncyCastleProvider())

        val client: HttpClient by lazy {
            HttpClient(OkHttp) {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
            }
        }

        val certificateValidator = CertificateValidator(trustStoreLocation, developmentMode)

        val addressInformationRepo = MerkleRepo(client, authorizationKey ?: "")

        val addressInformationService = AddressInformationNetkiService(addressInformationRepo)

        val invoiceRequestProcessor = InvoiceRequestProcessor(addressInformationService, certificateValidator)
        val paymentRequestProcessor = PaymentRequestProcessor(addressInformationService, certificateValidator)
        val paymentProcessor = PaymentProcessor(addressInformationService, certificateValidator)
        val paymentAckProcessor = PaymentAckProcessor(addressInformationService, certificateValidator)

        val bip75Service: Bip75Service =
            Bip75ServiceNetki(invoiceRequestProcessor, paymentRequestProcessor, paymentProcessor, paymentAckProcessor)

        return Bip75Netki(bip75Service)
    }
}
