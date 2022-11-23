package com.shift4.utils

import android.text.InputFilter
import android.text.Spanned

internal class SMSCodeInputFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        var cvc = dest.toString() + source.toString()
        if (cvc.length > 6) {
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