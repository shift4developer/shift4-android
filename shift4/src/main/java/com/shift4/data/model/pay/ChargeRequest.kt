package com.shift4.data.model.pay

import com.google.gson.annotations.SerializedName
import com.shift4.response.address.BillingRequest
import com.shift4.response.address.ShippingRequest

internal data class ChargeRequest(
    @SerializedName("key")
    val key: String,
    @SerializedName("tokenId")
    val tokenId: String,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("checkoutRequest")
    val checkoutRequest: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("customAmount")
    val customAmount: Int?,
    @SerializedName("shipping")
    val shippingRequest: ShippingRequest?,
    @SerializedName("billing")
    val billingRequest: BillingRequest?
)