package com.netki.bip75.config

import com.netki.bip75.main.Bip75
import com.netki.bip75.main.impl.Bip75Netki
import com.netki.bip75.service.Bip75Service
import com.netki.bip75.service.impl.Bip75ServiceNetki

/**
 * Factory to generate Bip75 instance.
 */
internal object Bip75Factory {

    /**
     * Get instance of Bip75 instance.
     *
     * @return Bip75 instance.
     */
    fun getInstance(): Bip75 {
        val bip75Service: Bip75Service = Bip75ServiceNetki()

        return Bip75Netki(bip75Service)
    }
}
