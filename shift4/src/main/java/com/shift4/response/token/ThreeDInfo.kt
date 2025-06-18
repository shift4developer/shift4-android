package com.shift4.response.token

data class ThreeDInfo(
    val amount: Int,
    val currency: String,
    val enrolled: Boolean,
    val liabilityShift: String?,
    val version: String
)