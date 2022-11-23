package com.shift4.data.model.pay

import com.shift4.utils.CurrencyFormatter

internal data class Donation(val amount: Int, val currency: String) {
    val readable: String get() {
        return CurrencyFormatter.format(amount.toBigDecimal(), currency, true)
    }
}