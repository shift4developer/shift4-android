package com.shift4.response.token

import com.google.gson.annotations.SerializedName

enum class GooglePayAuthMethod {
    @SerializedName("pan_only")
    PAN_ONLY,
    @SerializedName("cryptogram_3ds")
    CRYPTOGRAM_3DS
}