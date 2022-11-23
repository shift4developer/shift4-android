package com.shift4.data.model.address

internal data class Address(
    val line1: String,
    val zip: String,
    val city: String,
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

