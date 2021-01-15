package com.netki

import com.netki.model.InvoiceRequestParameters
import com.netki.model.MessageType
import com.netki.model.PaymentRequestParameters
import com.netki.model.StatusCode
import com.netki.util.TestData.Attestations.REQUESTED_ATTESTATIONS
import com.netki.util.TestData.Beneficiaries.NO_PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.Beneficiaries.PRIMARY_BENEFICIARY_PKI_X509SHA256
import com.netki.util.TestData.MessageInformationData.MESSAGE_INFORMATION_ENCRYPTION
import com.netki.util.TestData.Originators.NO_PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Originators.PRIMARY_ORIGINATOR_PKI_X509SHA256
import com.netki.util.TestData.Payment.Output.OUTPUTS
import com.netki.util.TestData.Recipients.RECIPIENTS_PARAMETERS_WITH_ENCRYPTION
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256
import com.netki.util.TestData.Senders.SENDER_PKI_X509SHA256_WITH_ENCRYPTION
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Timestamp

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TransactIdTest {

    private val transactId = TransactId.getInstance("src/test/resources/certificates")

    @BeforeAll
    fun setUp() {
        // Nothing to do here
    }
}
