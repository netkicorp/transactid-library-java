package com.netki.model

/**
 * Types of risks of an address alert.
 */
@Deprecated("Not used anymore")
data class RiskType @JvmOverloads constructor(

    /**
     * 	The risk type of the selected risk_types for the fired alert, [-1] if could not fetch the risk type.
     */
    val riskType: Int? = -1,

    /**
     * The name of the risk type for the detected risk.
     */
    val riskTypeVerbose: String? = ""
)
