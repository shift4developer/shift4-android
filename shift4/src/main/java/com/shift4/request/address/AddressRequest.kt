package com.shift4.request.address

data class AddressRequest(
    val line1: String,
    val line2: String? = null,
    val zip: String,
    val city: String,
    val country: String
)