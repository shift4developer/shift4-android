package com.shift4.data.model.pay

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ChargeResult(
    @SerializedName("customer")
    val customer: Customer,
    @SerializedName("chargeId")
    val id: String?,
    @SerializedName("subscriptionId")
    val subscriptionId: String?,
    @SerializedName("email")
    val email: String?
): java.io.Serializable {
    fun withEmail(email: String): ChargeResult {
        return ChargeResult(
            customer, id, subscriptionId, email
        )
    }
}