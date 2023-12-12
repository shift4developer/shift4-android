package com.shift4.data.model.lookup

import com.google.gson.annotations.SerializedName

internal data class CardExpiration(
    @SerializedName("month")
    val month: String,
    @SerializedName("year")
    val year: String
)