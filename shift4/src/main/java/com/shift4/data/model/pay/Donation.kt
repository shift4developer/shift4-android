package com.shift4.data.model.pay

import com.google.gson.annotations.SerializedName
import com.shift4.utils.CurrencyFormatter

internal data class Donation(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String
) {
    val readable: String
        get() {
            return CurrencyFormatter.format(amount.toBigDecimal(), currency, true)
        }
}