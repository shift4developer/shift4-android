package com.shift4.data.model.threeD

import com.shift4.data.model.token.Token

internal data class ThreeDCheckResponse(
    val version: String,
    val token: Token,
    val directoryServerCertificate: DirectoryServerCertificate,
    val sdkLicense: String
)