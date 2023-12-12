package com.shift4.data.model.pay

import com.google.gson.annotations.SerializedName
import com.shift4.data.model.address.Billing
import com.shift4.data.model.address.Shipping

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
    @SerializedName("rememberMe")
    val rememberMe: Boolean,
    @SerializedName("cvc")
    val cvc: String?,
    @SerializedName("verificationSmsId")
    val verificationSmsId: String?,
    @SerializedName("customAmount")
    val customAmount: Int?,
    @SerializedName("shipping")
    val shipping: Shipping?,
    @SerializedName("billing")
    val billing: Billing?
)