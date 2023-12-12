package com.shift4.data.model.token

import com.google.gson.annotations.SerializedName

data class ThreeDInfo(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("enrolled")
    val enrolled: Boolean,
    @SerializedName("liabilityShift")
    val liabilityShift: String?,
    @SerializedName("version")
    val version: String
)