package com.shift4.data.model.address

internal data class Billing(
    val name: String,
    val address: Address,
    val vat: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Billing

        if (name != other.name) return false
        if (address != other.address) return false
        if (vat != other.vat) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (vat?.hashCode() ?: 0)
        return result
    }
}