package com.shift4.utils

import android.util.Base64.encodeToString
import java.util.Base64

internal val String.Companion.empty: String get() = ""

internal val String.sanitized: String
    get() = this
        .replace(" ", "")
        .filter { c -> "0123456789•".contains(c) }

internal val String.base64: String get() = Base64.getEncoder().encodeToString(toByteArray())
internal val String.fromBase64: String get() = String(Base64.getDecoder().decode(this))