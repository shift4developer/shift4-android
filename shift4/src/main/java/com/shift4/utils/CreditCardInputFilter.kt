package com.shift4.utils

import android.text.InputFilter
import android.text.Spanned
import com.shift4.data.model.CreditCard

internal class CreditCardInputFilter : InputFilter {
    var card = CreditCard.empty

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val number = if (source.isEmpty()) {
            dest.removeRange(dend, dend).toString()
        } else {
            dest.toString() + source.toString()
        }
        card = CreditCard(number.filter { it != ' ' })
        val readable = card.readable
        val length = readable.length - dest.toString().length
        return if (length > 0) {
            readable.takeLast(length)
        } else {
            String.empty
        }
    }
}