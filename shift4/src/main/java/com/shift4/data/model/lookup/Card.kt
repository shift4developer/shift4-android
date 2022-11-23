package com.shift4.data.model.lookup

internal data class Card(
    val last2: String?,
    val last4: String?,
    val brand: String,
    val expiration: CardExpiration?
)