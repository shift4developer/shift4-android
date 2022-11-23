package com.shift4.data.model.threeD

internal data class ThreeDCheckRequest(
    val amount: Int,
    val currency: String,
    val card: String,
    val paymentUserAgent: String,
    val platform: String = "android"
)