package com.shift4.data.model.lookup

import com.google.gson.annotations.SerializedName

internal data class LookupRequest(
    @SerializedName("key")
    private val key: String,
    @SerializedName("email")
    private val email: String
)