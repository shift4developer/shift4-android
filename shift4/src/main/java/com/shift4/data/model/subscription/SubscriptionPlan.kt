package com.shift4.data.model.subscription

import com.google.gson.annotations.SerializedName

internal data class SubscriptionPlan(
    @SerializedName("id")
    val id: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String
)