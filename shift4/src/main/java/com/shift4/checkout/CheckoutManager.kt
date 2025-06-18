package com.shift4.checkout

import android.app.Activity
import com.shift4.data.api.Result
import com.shift4.data.model.error.APIError
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.repository.SDKRepository
import com.shift4.request.token.TokenRequest
import com.shift4.response.address.BillingRequest
import com.shift4.response.address.ShippingRequest
import com.shift4.response.token.Token
import com.shift4.threed.ThreeDAuthenticator
import kotlinx.coroutines.CoroutineScope

internal class CheckoutManager(
    private val repository: SDKRepository,
    private val signature: String,
    private val packageName: String,
    private val trustedAppStores: List<String>?,
    private val coroutineScope: CoroutineScope
) {
    internal suspend fun pay(
        token: Token,
        checkoutRequest: CheckoutRequest,
        email: String,
        threeDActivity: Activity,
        customAmount: Int? = null,
        customCurrency: String? = null,
        shippingRequest: ShippingRequest?,
        billingRequest: BillingRequest?,
        callback: (Result<ChargeResult>) -> Unit
    ) {
        val threeDAuthenticator = ThreeDAuthenticator(repository.publicKey)
        val checkoutDetailsResult = repository.checkoutRequestDetails(checkoutRequest)
        val checkoutDetails = checkoutDetailsResult.data ?: run {
            callback(Result.error(checkoutDetailsResult.error ?: APIError.unknown, null))
            return@pay
        }

        val computedCustomAmount: Int? = if (checkoutDetails.subscription == null) {
            customAmount
        } else {
            null
        }

        if (!checkoutDetails.threeDSecureRequired) {
            callback(
                repository.pay(
                    token,
                    checkoutRequest,
                    email,
                    computedCustomAmount,
                    shippingRequest = shippingRequest,
                    billingRequest = billingRequest
                )
            )
            return
        }

        val authenticatedToken = threeDAuthenticator.authenticate(
            token,
            null,
            computedCustomAmount ?: checkoutRequest.amount,
            customCurrency ?: checkoutRequest.currency,
            threeDActivity
        ).data!!


        callback(
            repository.pay(
                authenticatedToken,
                checkoutRequest,
                email,
                computedCustomAmount,
                shippingRequest = shippingRequest,
                billingRequest = billingRequest
            )
        )
    }

    internal suspend fun pay(
        tokenRequest: TokenRequest,
        checkoutRequest: CheckoutRequest,
        email: String,
        threeDActivity: Activity,
        customAmount: Int? = null,
        customCurrency: String? = null,
        shippingRequest: ShippingRequest?,
        billingRequest: BillingRequest?,
        callback: (Result<ChargeResult>) -> Unit
    ) {
        val tokenResult = repository.createToken(tokenRequest)
        val token = tokenResult.data ?: run {
            callback(Result.error(tokenResult.error ?: APIError.unknown, null))
            return@pay
        }
        pay(
            token,
            checkoutRequest,
            email,
            threeDActivity,
            customAmount,
            customCurrency,
            shippingRequest = shippingRequest,
            billingRequest = billingRequest,
            callback
        )
    }

    internal suspend fun checkoutRequestDetails(checkoutRequest: CheckoutRequest) =
        repository.checkoutRequestDetails(checkoutRequest)
}