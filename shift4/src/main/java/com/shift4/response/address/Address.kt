package com.shift4.response.address

data class Address(
    val line1: String,
    val line2: String?,
    val zip: String,
    val city: String,
    val country: String
)