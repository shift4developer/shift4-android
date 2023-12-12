package com.shift4.data.model.token

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("created")
    val created: Int? = null,
    @SerializedName("objectType")
    val objectType: String? = null,
    @SerializedName("first6")
    val first6: String? = null,
    @SerializedName("last4")
    val last4: String? = null,
    @SerializedName("fingerprint")
    val fingerprint: String? = null,
    @SerializedName("expMonth")
    val expirationMonth: String? = null,
    @SerializedName("expYear")
    val expirationYear: String? = null,
    @SerializedName("cardholder")
    val cardholder: String? = null,
    @SerializedName("brand")
    val brand: String = "",
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("threeDSecureInfo")
    val threeDSecureInfo: ThreeDInfo? = null
)