package com.shift4.utils

import android.text.InputFilter
import android.text.Spanned
import com.shift4.data.model.CreditCard

internal class CVCInputFilter : InputFilter {
    var card = CreditCard.empty

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        var cvc = dest.toString() + source.toString()
        if (cvc.length > card.cvcLength) {
            cvc = cvc.takeLast(1)
        }
        val length = cvc.length - dest.toString().length
        return if (length > 0) {
            cvc.takeLast(length)
        } else {
            String.empty
        }
    }
}