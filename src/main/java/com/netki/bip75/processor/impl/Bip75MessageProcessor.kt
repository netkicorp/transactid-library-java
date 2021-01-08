package com.netki.bip75.processor.impl

import com.netki.address.info.service.AddressInformationService
import com.netki.bip75.processor.ProtocolMessageProcessor
import com.netki.model.AddressCurrency
import com.netki.model.PkiType
import com.netki.security.CertificateValidator

internal abstract class Bip75MessageProcessor(
    private val addressInformationService: AddressInformationService,
    private val certificateValidator: CertificateValidator
) : ProtocolMessageProcessor {

    internal fun getAddressInformation(currency: AddressCurrency, address: String) =
        addressInformationService.getAddressInformation(currency, address)

    internal fun validateCertificate(pkiType: PkiType, certificate: String) = when (pkiType) {
        PkiType.NONE -> true
        PkiType.X509SHA256 -> {
            certificateValidator.validateCertificate(certificate)
        }
    }

    internal fun isEvCertificate(certificate: String) = certificateValidator.isEvCertificate(certificate)
}
