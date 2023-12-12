package com.shift4.data.model.token

import com.google.gson.annotations.SerializedName

internal data class SavedTokenRequest(
    @SerializedName("key")
    private val key: String,
    @SerializedName("email")
    private val email: String,
    @SerializedName("paymentUserAgent")
    private val paymentUserAgent: String
)