package com.shift4.data.model.threeD

import com.google.gson.annotations.SerializedName

internal data class ThreeDAuthRequest(
    @SerializedName("clientAuthRequest")
    val clientAuthRequest: String,
    @SerializedName("token")
    val token: String
)