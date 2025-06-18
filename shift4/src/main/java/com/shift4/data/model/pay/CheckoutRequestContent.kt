package com.shift4.data.model.pay

import com.google.gson.annotations.SerializedName

internal data class CheckoutRequestContent(
    @SerializedName("charge")
    val charge: Charge?,
    @SerializedName("customCharge")
    val customCharge: CustomCharge?,
    @SerializedName("customerId")
    val customerId: String?,
    @SerializedName("termsAndConditionsUrl")
    val termsAndConditionsUrl: String?,
    @SerializedName("threeDSecure")
    val threeDSecure: ThreeDSecure?,
    @SerializedName("subscription")
    val subscription: Subscription?
) {
    data class Subscription(val planId: String)

    data class ThreeDSecure(
        val enable: Boolean,
        val requireEnrolledCard: Boolean,
        val requireSuccessfulLiabilityShiftForEnrolledCard: Boolean
    )

    data class Charge(
        val amount: Int,
        val currency: String
    )

    data class CustomAmount(
        val min: Int,
        val max: Int
    )

    data class CustomCharge(
        val amountOptions: Array<Int>?,
        val customAmount: CustomAmount?,
        val currency: String
    )
}