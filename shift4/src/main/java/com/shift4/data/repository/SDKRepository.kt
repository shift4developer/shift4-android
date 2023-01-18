package com.shift4.data.repository

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.interceptors.LogRequestAsCurlInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogRequestInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogResponseInterceptor
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import com.github.kittinunf.fuel.gson.gsonDeserializer
import com.github.kittinunf.fuel.gson.jsonBody
import com.shift4.BuildConfig
import com.shift4.Shift4
import com.shift4.data.api.Result
import com.shift4.data.model.address.Billing
import com.shift4.data.model.address.Shipping
import com.shift4.data.model.checkoutRequestDetails.CheckoutDetailsRequest
import com.shift4.data.model.checkoutRequestDetails.CheckoutRequestDetails
import com.shift4.data.model.lookup.LookupRequest
import com.shift4.data.model.lookup.LookupResult
import com.shift4.data.model.pay.ChargeRequest
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.sms.SMS
import com.shift4.data.model.sms.SendSMSRequest
import com.shift4.data.model.sms.VerifySMSRequest
import com.shift4.data.model.sms.VerifySMSResponse
import com.shift4.data.model.threeD.*
import com.shift4.data.model.token.SavedTokenRequest
import com.shift4.data.model.token.Token
import com.shift4.data.model.token.TokenRequest
import com.shift4.utils.UserAgentGenerator
import com.shift4.utils.base64

internal class SDKRepository(private val shift4: Shift4) {
    private val manager = FuelManager()

    init {
        manager.baseHeaders = mapOf(
            "User-Agent" to UserAgentGenerator().userAgent(),
            "Referer" to BuildConfig.BACKOFFICE_URL
        )
        if (BuildConfig.DEBUG) {
            manager.addRequestInterceptor(LogRequestAsCurlInterceptor)
            manager.addRequestInterceptor(LogRequestInterceptor)
            manager.addResponseInterceptor(LogResponseInterceptor)
        }
        manager.timeoutInMillisecond = 60000
        manager.timeoutReadInMillisecond = 60000
    }

    private fun <T> handleResponse(result: com.github.kittinunf.result.Result<T, FuelError>): Result<T> {
        return when (result) {
            is com.github.kittinunf.result.Result.Success ->
                (Result.success(result.value))
            is com.github.kittinunf.result.Result.Failure -> {
                val error = ErrorUtils().parseError(
                    result.error.response.body().asString("application/json")
                )
                (Result.error(error, null))
            }
        }
    }

    suspend fun createToken(tokenRequest: TokenRequest): Result<Token> {
        manager.basePath = BuildConfig.API_URL

        val (_, _, result) = manager
            .post("tokens")
            .authentication().basic(shift4.publicKey, "")
            .jsonBody(tokenRequest)
            .awaitObjectResponseResult<Token>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun pay(
        token: Token,
        checkoutRequest: CheckoutRequest,
        email: String,
        remember: Boolean = false,
        cvc: String? = null,
        sms: SMS? = null,
        customAmount: Int? = null,
        shipping: Shipping?,
        billing: Billing?
    ): Result<ChargeResult> {
        val details = checkoutRequestDetails(checkoutRequest)
        details.error?.let { return@pay Result.error(it) }

        manager.basePath = BuildConfig.BACKOFFICE_URL

        val chargeRequest = ChargeRequest(
            key = shift4.publicKey,
            tokenId = token.id,
            sessionId = details.data?.sessionId ?: "",
            checkoutRequest = checkoutRequest.content,
            email = email,
            rememberMe = remember,
            cvc = cvc,
            verificationSmsId = sms?.id,
            customAmount = customAmount,
            shipping = shipping,
            billing = billing
        )

        val (_, _, result) = manager
            .post("checkout/pay")
            .jsonBody(chargeRequest)
            .awaitObjectResponseResult<ChargeResult>(gsonDeserializer())

        if (remember) {
            shift4.emailStorage.lastEmail = email
        }

        return when (result) {
            is com.github.kittinunf.result.Result.Success ->
                (Result.success(result.value.withEmail(email)))
            is com.github.kittinunf.result.Result.Failure -> {
                val error = ErrorUtils().parseError(
                    result.error.response.body().asString("application/json")
                )
                (Result.error(error, null))
            }
        }
    }

    suspend fun lookup(email: String): Result<LookupResult> {
        manager.basePath = BuildConfig.BACKOFFICE_URL

        val (_, _, result) = manager
            .post("checkout/lookup")
            .jsonBody(LookupRequest(shift4.publicKey, email))
            .awaitObjectResponseResult<LookupResult>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun savedToken(email: String): Result<Token> {
        manager.basePath = BuildConfig.BACKOFFICE_URL

        val (_, _, result) = manager
            .post("checkout/tokens")
            .jsonBody(SavedTokenRequest(shift4.publicKey, email, "android_sdk"))
            .awaitObjectResponseResult<Token>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun sendSMS(email: String): Result<SMS> {
        manager.basePath = BuildConfig.BACKOFFICE_URL

        val (_, _, result) = manager
            .post("checkout/verification-sms")
            .jsonBody(SendSMSRequest(shift4.publicKey, email))
            .awaitObjectResponseResult<SMS>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun verifySMS(code: String, sms: SMS): Result<VerifySMSResponse> {
        manager.basePath = BuildConfig.BACKOFFICE_URL

        val (_, _, result) = manager
            .post("checkout/verification-sms/${sms.id}")
            .jsonBody(VerifySMSRequest(code))
            .awaitObjectResponseResult<VerifySMSResponse>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun threeDCheck(
        token: Token,
        amount: Int,
        currency: String
    ): Result<ThreeDCheckResponse> {
        manager.basePath = BuildConfig.API_URL
        val request = ThreeDCheckRequest(
            amount,
            currency,
            token.id,
            UserAgentGenerator().userAgent()
        )
        val (_, _, result) = manager
            .post("3d-secure")
            .authentication().basic(shift4.publicKey, "")
            .jsonBody(request)
            .awaitObjectResponseResult<ThreeDCheckResponse>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun threeDAuthorize(
        token: Token,
        authorizationParameters: String
    ): Result<ThreeDAuthResponse> {
        manager.basePath = BuildConfig.API_URL

        val request = ThreeDAuthRequest(authorizationParameters.base64, token.id)
        val (_, _, result) = manager
            .post("3d-secure/v2/authenticate")
            .authentication().basic(shift4.publicKey, "")
            .jsonBody(request)
            .awaitObjectResponseResult<ThreeDAuthResponse>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun threeDChallengeComplete(token: Token): Result<Token> {
        manager.basePath = BuildConfig.API_URL

        val request = ThreeDChallengeCompleteRequest(token.id)
        val (_, _, result) = manager
            .post("challenge-complete")
            .authentication().basic(shift4.publicKey, "")
            .jsonBody(request)
            .awaitObjectResponseResult<Token>(gsonDeserializer())

        return handleResponse(result)
    }

    suspend fun checkoutRequestDetails(checkoutRequest: CheckoutRequest): Result<CheckoutRequestDetails> {
        manager.basePath = BuildConfig.BACKOFFICE_URL

        val (_, _, result) = manager
            .post("checkout/forms")
            .jsonBody(CheckoutDetailsRequest(shift4.publicKey, checkoutRequest.content))
            .awaitObjectResponseResult<CheckoutRequestDetails>(gsonDeserializer())

        return handleResponse(result)
    }
}