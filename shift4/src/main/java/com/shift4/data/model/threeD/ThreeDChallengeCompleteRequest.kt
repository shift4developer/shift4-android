package com.shift4.data.model.threeD

import com.google.gson.annotations.SerializedName

internal data class ThreeDChallengeCompleteRequest(
    @SerializedName("token")
    val token: String
)