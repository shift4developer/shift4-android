package com.shift4.data.model.pay

import com.shift4.data.model.address.Billing
import com.shift4.data.model.address.Shipping

internal data class ChargeRequest(
    val key: String,
    val tokenId: String,
    val sessionId: String,
    val checkoutRequest: String,
    val email: String,
    val rememberMe: Boolean,
    val cvc: String?,
    val verificationSmsId: String?,
    val customAmount: Int?,
    val shipping: Shipping?,
    val billing: Billing?
)