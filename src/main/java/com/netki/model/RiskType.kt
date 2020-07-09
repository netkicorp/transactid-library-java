package com.netki.model

data class RiskType(

    /**
     * 	The risk type of the selected risk_types for the fired alert.
     */
    val riskType: Int,

    /**
     * The name of the risk type for the detected risk.
     */
    val riskTypeVerbose: String
)
