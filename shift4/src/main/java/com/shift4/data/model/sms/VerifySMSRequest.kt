package com.shift4.data.model.sms

import com.shift4.utils.empty

internal data class VerifySMSRequest(
    private val code: String = String.empty
)