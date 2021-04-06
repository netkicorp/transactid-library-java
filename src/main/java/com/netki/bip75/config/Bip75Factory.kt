package com.netki.bip75.config

import com.netki.bip75.main.Bip75
import com.netki.bip75.main.impl.Bip75Netki
import com.netki.bip75.service.Bip75Service
import com.netki.bip75.service.impl.Bip75ServiceNetki
import com.netki.message.config.MessageFactory
import com.netki.security.CertificateValidator
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
     * @param developmentMode set to true if you are using this library in a sandbox environment.
     * @return Bip75 instance.
     */
    @JvmOverloads
    fun getInstance(
        trustStoreLocation: String,
        authorizationKey: String? = null,
        developmentMode: Boolean = false
    ): Bip75 {
        Security.addProvider(BouncyCastleProvider())

        val messageInstance = MessageFactory.getInstance(authorizationKey)

        val certificateValidator = CertificateValidator(trustStoreLocation, developmentMode)

        val bip75Service: Bip75Service = Bip75ServiceNetki(messageInstance, certificateValidator)

        return Bip75Netki(bip75Service)
    }
}
