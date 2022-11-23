package com.shift4.data.model.pay

internal data class CheckoutRequestContent(
    val charge: Charge?,
    val customCharge: CustomCharge?,
    val rememberMe: Boolean?,
    val customerId: String?,
    val termsAndConditionsUrl: String?,
    val crossSaleOfferIds: Array<String>?,
    val threeDSecure: ThreeDSecure?,
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
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CustomCharge

            if (amountOptions != null) {
                if (other.amountOptions == null) return false
                if (!amountOptions.contentEquals(other.amountOptions)) return false
            } else if (other.amountOptions != null) return false
            if (customAmount != other.customAmount) return false
            if (currency != other.currency) return false

            return true
        }

        override fun hashCode(): Int {
            var result = amountOptions?.contentHashCode() ?: 0
            result = 31 * result + (customAmount?.hashCode() ?: 0)
            result = 31 * result + currency.hashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CheckoutRequestContent

        if (charge != other.charge) return false
        if (customCharge != other.customCharge) return false
        if (rememberMe != other.rememberMe) return false
        if (customerId != other.customerId) return false
        if (termsAndConditionsUrl != other.termsAndConditionsUrl) return false
        if (crossSaleOfferIds != null) {
            if (other.crossSaleOfferIds == null) return false
            if (!crossSaleOfferIds.contentEquals(other.crossSaleOfferIds)) return false
        } else if (other.crossSaleOfferIds != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = charge?.hashCode() ?: 0
        result = 31 * result + (customCharge?.hashCode() ?: 0)
        result = 31 * result + (rememberMe?.hashCode() ?: 0)
        result = 31 * result + (customerId?.hashCode() ?: 0)
        result = 31 * result + (termsAndConditionsUrl?.hashCode() ?: 0)
        result = 31 * result + (crossSaleOfferIds?.contentHashCode() ?: 0)
        return result
    }
}