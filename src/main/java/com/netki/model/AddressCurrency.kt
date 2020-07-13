package com.netki.model

/**
 * Type of currency for an address.
 */
enum class AddressCurrency(val id: Int) {
    BITCOIN(0),
    ETHEREUM(1),
    LITECOIN(2),
    BITCOIN_CASH(3);
}

