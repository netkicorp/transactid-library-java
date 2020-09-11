package com.netki.keymanagement.repo

import com.netki.exceptions.CertificateProviderException
import com.netki.exceptions.CertificateProviderUnauthorizedException
import com.netki.keymanagement.repo.data.CertificateAttestationResponse
import com.netki.keymanagement.repo.data.CsrAttestation
import com.netki.model.Attestation

internal interface CertificateProvider {

    /**
     * Request a transactionId to start the process to create certificates for each one of a list of attestations.
     *
     * @param attestations required attestations.
     * @return the transactionId.
     * @throws CertificateProviderException if there is an error requesting the transactionId.
     * @throws CertificateProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
     */
    @Throws(CertificateProviderException::class, CertificateProviderUnauthorizedException::class)
    fun requestTransactionId(attestations: List<Attestation>): String

    /**
     * Submit a list of CSR to the provider to create certificates for each one.
     *
     * @param transactionId that will be associated to these CSR.
     * @param csrsAttestations list of CSRs corresponding to each attestation.
     * @throws CertificateProviderException if there is an error sending the CSRs.
     * @throws CertificateProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
     */
    @Throws(CertificateProviderException::class, CertificateProviderUnauthorizedException::class)
    fun submitCsrsAttestations(transactionId: String, csrsAttestations: List<CsrAttestation>)

    /**
     * Fetch all the certificates associated to a transactionId.
     *
     * @param transactionId to fetch the certificates
     * @return list of certificates per attestation
     * @throws CertificateProviderException if there is an error fetching the certificates.
     * @throws CertificateProviderUnauthorizedException if there is an error with the authorization to connect to the provider.
     */
    @Throws(CertificateProviderException::class, CertificateProviderUnauthorizedException::class)
    fun getCertificates(transactionId: String): CertificateAttestationResponse
}
