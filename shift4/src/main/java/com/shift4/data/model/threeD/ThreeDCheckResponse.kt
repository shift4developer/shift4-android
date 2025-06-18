package com.shift4.data.model.threeD

import com.google.gson.annotations.SerializedName
import com.shift4.response.token.Token

internal data class ThreeDCheckResponse(
    @SerializedName("version")
    val version: String,
    @SerializedName("token")
    val token: Token,
    @SerializedName("directoryServerCertificate")
    var directoryServerCertificate: DirectoryServerCertificate?,
)