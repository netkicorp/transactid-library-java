package com.netki.keymanagement.config

import com.netki.keymanagement.driver.KeyManagementDriver
import com.netki.keymanagement.driver.impl.VaultDriver
import com.netki.keymanagement.main.KeyManagement
import com.netki.keymanagement.main.impl.KeyManagementNetki
import com.netki.keymanagement.service.KeyManagementService
import com.netki.keymanagement.service.impl.KeyManagementNetkiService

/**
 * Factory to generate KeyManagement instance.
 */
object KeyManagementFactory {

    /**
     * Get an instance of KeyManagement.
     *
     * @return KeyManagement instance.
     */
    fun getInstance(): KeyManagement {

        val keyManagementDriver: KeyManagementDriver = VaultDriver()

        val keyManagementService: KeyManagementService = KeyManagementNetkiService(keyManagementDriver)

        return KeyManagementNetki(keyManagementService)
    }
}
