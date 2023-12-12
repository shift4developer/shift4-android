package com.shift4.data.model.result

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.shift4.data.model.error.APIError
import com.shift4.data.model.pay.ChargeResult
import java.io.Serializable

data class CheckoutResult(
    @SerializedName("data")
    val data: ChargeResult?,
    @SerializedName("error")
    val error: APIError?
) :
    Serializable {
    fun toMap(): Map<String, Any> {
        return Gson().fromJson(Gson().toJson(this), Map::class.java) as Map<String, Any>
    }
}
