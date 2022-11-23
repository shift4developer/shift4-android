package com.shift4.data.model.checkoutRequestDetails

import com.shift4.data.model.subscription.Subscription

internal data class CheckoutRequestDetails(
    val sessionId: String,
    val threeDSecureRequired: Boolean,
    val subscription: Subscription?
)