package com.shift4.data.model.threeD

import com.google.gson.annotations.SerializedName

data class DirectoryServerCertificate(
    @SerializedName("certificate")
    val certificate: String,
    @SerializedName("caCertificates")
    val caCertificates: List<String>
)