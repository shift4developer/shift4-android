package com.shift4.utils

import android.util.Base64

internal val String.Companion.empty: String get() = ""

internal val String.sanitized: String
    get() = this
        .replace(" ", "")
        .filter { c -> "0123456789•".contains(c) }

internal val String.base64: String get() = Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP)
internal val String.fromBase64: String get() = String(Base64.decode(this, Base64.NO_WRAP))