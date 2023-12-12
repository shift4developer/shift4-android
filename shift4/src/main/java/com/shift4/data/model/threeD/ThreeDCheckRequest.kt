package com.shift4.data.model.threeD

import com.google.gson.annotations.SerializedName

internal data class ThreeDCheckRequest(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("card")
    val card: String,
    @SerializedName("paymentUserAgent")
    val paymentUserAgent: String,
    @SerializedName("platform")
    val platform: String = "android"
)