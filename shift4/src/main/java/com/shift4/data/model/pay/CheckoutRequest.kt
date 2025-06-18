package com.shift4.data.model.pay

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.shift4.utils.CurrencyFormatter
import java.util.*

data class CheckoutRequest(@SerializedName("content") internal val content: String) {
    internal val correct: Boolean
        get() {
            return try {
                checkoutRequestContent
                true
            } catch (_: Exception) {
                false
            }
        }

    internal val amount: Int
        get() {
            return checkoutRequestContent.charge?.amount ?: 0
        }

    internal val currency: String
        get() {
            return checkoutRequestContent.charge?.currency ?: checkoutRequestContent.customCharge?.currency ?: ""
        }

    internal val readable: String
        get() {
            val amount = checkoutRequestContent.charge?.amount ?: return ""
            val currency = checkoutRequestContent.charge?.currency ?: return ""
            return CurrencyFormatter.format(
                amount.toBigDecimal(),
                currency,
                divideMinorUnits = true
            )
        }

    internal val termsAndConditions: String?
        get() {
            return checkoutRequestContent.termsAndConditionsUrl
        }

    internal val customerId: String?
        get() {
            return checkoutRequestContent.customerId
        }

    internal val donations: List<Donation>?
        get() {
            if (checkoutRequestContent.customCharge?.amountOptions?.isEmpty() != false) {
                return null
            }
            return checkoutRequestContent.customCharge?.amountOptions?.map {
                Donation(it, checkoutRequestContent.customCharge?.currency ?: "")
            }
        }

    internal val customDonation: Pair<Int, Int>?
        get() {
            val min = checkoutRequestContent.customCharge?.customAmount?.min ?: return null
            val max = checkoutRequestContent.customCharge?.customAmount?.max ?: return null
            return Pair(min, max)
        }

    internal val threeDSecure: Boolean
        get() {
            return checkoutRequestContent.threeDSecure?.enable ?: false
        }

    internal val requireEnrolledCard: Boolean
        get() {
            return checkoutRequestContent.threeDSecure?.requireEnrolledCard ?: false
        }

    internal val requireSuccessfulLiabilityShiftForEnrolledCard: Boolean
        get() {
            return checkoutRequestContent.threeDSecure?.requireSuccessfulLiabilityShiftForEnrolledCard ?: false
        }

    internal val subscriptionPlanId: String?
        get() {
            return checkoutRequestContent.subscription?.planId
        }

    private val checkoutRequestContent: CheckoutRequestContent
        get() {
            val decoded = String(Base64.getDecoder().decode(content))
            val json = decoded.split("|").last()
            try {
                return Gson().fromJson(json, CheckoutRequestContent::class.java)
            } catch (e: Exception) {
                throw(Exception("Invalid Checkout Request"))
            }
        }
}