package com.shift4.request.token

import com.shift4.request.address.BillingRequest
import com.shift4.request.address.ShippingRequest

data class TokenRequest(
    val number: String? = null,
    val expMonth: String? = null,
    val expYear: String? = null,
    val cvc: String? = null,
    val cardholderName: String? = null,
    val googlePay: GooglePayRequest? = null,
    val billingRequest: BillingRequest? = null,
    val shippingRequest: ShippingRequest? = null
) {
    data class GooglePayRequest(
        val token: String
    )
}