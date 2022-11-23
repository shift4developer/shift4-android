package com.shift4.data.model.sms

internal data class SendSMSRequest(
    private val key: String,
    private val email: String
)