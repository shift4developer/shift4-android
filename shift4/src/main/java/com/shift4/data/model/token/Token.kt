package com.shift4.data.model.token

import com.google.gson.annotations.SerializedName

data class Token(
    val id: String = "",
    val created: Int? = null,
    val objectType: String? = null,
    val first6: String? = null,
    val last4: String? = null,
    val fingerprint: String? = null,
    @SerializedName("expMonth")
    val expirationMonth: String? = null,
    @SerializedName("expYear")
    val expirationYear: String? = null,
    val cardholder: String? = null,
    val brand: String = "",
    val type: String? = null,
    val country: String? = null,
    val threeDSecureInfo: ThreeDInfo? = null
)