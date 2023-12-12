package com.shift4.data.model.sms

import com.google.gson.annotations.SerializedName

internal data class SendSMSRequest(
    @SerializedName("key")
    private val key: String,
    @SerializedName("email")
    private val email: String
)