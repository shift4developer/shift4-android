package com.shift4.data.model.address

import com.google.gson.annotations.SerializedName

internal data class Shipping(
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: Address
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Shipping

        if (name != other.name) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}