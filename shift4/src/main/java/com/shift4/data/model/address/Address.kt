package com.shift4.data.model.address

import com.google.gson.annotations.SerializedName

internal data class Address(
    @SerializedName("line1")
    val line1: String,
    @SerializedName("zip")
    val zip: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (line1 != other.line1) return false
        if (zip != other.zip) return false
        if (city != other.city) return false
        if (country != other.country) return false

        return true
    }

    override fun hashCode(): Int {
        var result = line1.hashCode()
        result = 31 * result + zip.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + country.hashCode()
        return result
    }
}

