package com.shift4.utils

import android.text.TextUtils
import android.util.Patterns

internal class EmailValidator {
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target ?: "").matches()
    }
}