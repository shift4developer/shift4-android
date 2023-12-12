package com.shift4.data.model.checkoutRequestDetails

import com.google.gson.annotations.SerializedName

internal data class CheckoutDetailsRequest(
    @SerializedName("key")
    val key: String,
    @SerializedName("checkoutRequest")
    val checkoutRequest: String
)