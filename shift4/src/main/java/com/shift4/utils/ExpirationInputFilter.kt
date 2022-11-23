package com.shift4.utils

import android.text.InputFilter
import android.text.Spanned

internal class ExpirationInputFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val formatted =
            ExpirationDateFormatter().format(dest.toString() + source.toString(), dstart < dend)
        val length = (formatted.text?.length ?: 0) - dest.toString().length
        return if (length > 0) {
            formatted.text?.takeLast(length) ?: String.empty
        } else {
            String.empty
        }
    }
}