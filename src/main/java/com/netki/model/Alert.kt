package com.netki.model

/**
 * Alerts associated to an address.
 */
@Deprecated("Not used anymore")
data class Alert @JvmOverloads constructor(

    /**
     * Name of the fired alert.
     */
    val ruleName: String? = "",

    /**
     * The risk level for this classifier, [-1] if could not fetch the risk level.
     */
    val riskLevel: Int? = -1,

    /**
     * The risk level for this classifier.
     */
    val riskLevelVerbose: String? = "",

    /**
     * 	The risk types for the fired alert risk.
     */
    val riskTypes: List<RiskType>? = emptyList(),

    /**
     * Classifier specific context which describes why this classifier fired an alert.
     * This is a Json format string.
     */
    val context: String? = "",

    /**
     * UTC Timestamp for when the alert was fired.
     */
    val createdAt: String? = ""
)
