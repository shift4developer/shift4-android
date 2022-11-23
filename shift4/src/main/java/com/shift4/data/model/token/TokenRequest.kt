package com.shift4.data.model.token

import com.google.gson.annotations.SerializedName

data class TokenRequest (
    val number: String = "",
    @SerializedName("expMonth")
    val expirationMonth : String = "",
    @SerializedName("expYear")
    val expirationYear : String = "",
    val cvc : String = "",
    @SerializedName("cardholderName")
    val cardholder: String? = null
)