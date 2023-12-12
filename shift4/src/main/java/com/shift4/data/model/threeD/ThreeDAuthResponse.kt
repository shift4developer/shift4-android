package com.shift4.data.model.threeD

import com.google.gson.annotations.SerializedName

internal data class ThreeDAuthResponse(
    @SerializedName("ares")
    val ares: Ares
)