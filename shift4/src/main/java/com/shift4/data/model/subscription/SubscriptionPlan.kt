package com.shift4.data.model.subscription

internal data class SubscriptionPlan(
    val id: String,
    val amount: Int,
    val currency: String
)