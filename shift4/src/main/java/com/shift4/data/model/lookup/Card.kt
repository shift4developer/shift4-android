package com.shift4.data.model.lookup

import com.google.gson.annotations.SerializedName

internal data class Card(
    @SerializedName("last2")
    val last2: String?,
    @SerializedName("last4")
    val last4: String?,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("expiration")
    val expiration: CardExpiration?
)