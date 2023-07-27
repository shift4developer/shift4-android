package com.shift4.data.model.result

import com.google.gson.Gson
import com.shift4.data.api.Status
import com.shift4.data.model.error.APIError
import com.shift4.data.model.pay.ChargeResult
import java.io.Serializable

data class CheckoutResult(val status: Status, val data: ChargeResult?, val error: APIError?) :
    Serializable {
    fun toMap(): Map<String, Any> {
        return Gson().fromJson(Gson().toJson(this), Map::class.java) as Map<String, Any>
    }
}
