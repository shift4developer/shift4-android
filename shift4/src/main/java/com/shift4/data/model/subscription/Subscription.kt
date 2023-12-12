package com.shift4.data.model.subscription

import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.util.*

internal data class Subscription(
    @SerializedName("plan")
    val plan: SubscriptionPlan
) {
    fun readable(): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(plan.currency)
        return format.format(plan.amount.toDouble()/100).replace(" ", " ")
    }
}