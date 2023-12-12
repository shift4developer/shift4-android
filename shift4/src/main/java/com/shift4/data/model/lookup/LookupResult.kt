package com.shift4.data.model.lookup

import com.google.gson.annotations.SerializedName

internal data class LookupResult (
    @SerializedName("card")
    val card: Card?,
    @SerializedName("phone")
    val phone: Phone?
)