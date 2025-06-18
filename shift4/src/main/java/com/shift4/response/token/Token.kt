package com.shift4.response.token

data class Token(
    val id: String,
    val created: Int,
    val objectType: String,
    val first6: String,
    val last4: String,
    val fingerprint: String,
    val expMonth: String,
    val expYear: String,
    val cardholder: String?,
    val brand: String,
    val type: String,
    val threeDSecureInfo: ThreeDInfo?
)