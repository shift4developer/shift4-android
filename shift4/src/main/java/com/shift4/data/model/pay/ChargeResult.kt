package com.shift4.data.model.pay

import com.google.gson.annotations.SerializedName

data class ChargeResult(
    val customer: Customer,
    @SerializedName("chargeId")
    val id: String?,
    val subscriptionId: String?,
    val email: String?
) {
    fun withEmail(email: String): ChargeResult {
        return ChargeResult(
            customer, id, subscriptionId, email
        )
    }
}