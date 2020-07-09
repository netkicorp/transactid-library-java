package com.netki.model

data class Alert(

    /**
     * Name of the fired alert.
     */
    val ruleName: String,

    /**
     * The risk level for this classifier.
     */
    val riskLevel: Int,

    /**
     * The risk level for this classifier.
     */
    val riskLevelVerbose: String,

    /**
     * 	The risk types for the fired alert risk.
     */
    val riskTypes: List<RiskType>,

    /**
     * Classifier specific context which describes why this classifier fired an alert.
     */
    val context: String,

    /**
     * UTC Timestamp for when the alert was fired.
     */
    val createdAt: String
)
