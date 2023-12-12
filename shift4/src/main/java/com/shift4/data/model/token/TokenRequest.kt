package com.shift4.data.model.token

import com.google.gson.annotations.SerializedName

data class TokenRequest (
    @SerializedName("number")
    val number: String = "",
    @SerializedName("expMonth")
    val expirationMonth : String = "",
    @SerializedName("expYear")
    val expirationYear : String = "",
    @SerializedName("cvc")
    val cvc : String = "",
    @SerializedName("cardholderName")
    val cardholder: String? = null
)