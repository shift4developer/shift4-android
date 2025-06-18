package com.shift4.data.repository

import com.shift4.BuildConfig
import com.shift4.Shift4
import com.shift4.data.api.APIService
import com.shift4.data.api.RequestBuilder
import com.shift4.data.api.Result
import com.shift4.response.address.BillingRequest
import com.shift4.response.address.ShippingRequest
import com.shift4.data.model.checkoutRequestDetails.CheckoutDetailsRequest
import com.shift4.data.model.checkoutRequestDetails.CheckoutRequestDetails
import com.shift4.data.model.pay.ChargeRequest
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.threeD.*
import com.shift4.response.token.Token
import com.shift4.request.token.TokenRequest
import com.shift4.utils.UserAgentGenerator
import com.shift4.utils.base64

internal class SDKRepository(val publicKey: String) {
    private val responseHandler = ResponseHandler()

    suspend fun createToken(tokenRequest: TokenRequest): Result<Token> {
        val service = service(BuildConfig.API_URL, true)

        return try {
            responseHandler.handleSuccess(service.createToken(tokenRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun pay(
        token: Token,
        checkoutRequest: CheckoutRequest,
        email: String,
        customAmount: Int? = null,
        shippingRequest: ShippingRequest?,
        billingRequest: BillingRequest?
    ): Result<ChargeResult> {
        val service = service(BuildConfig.BACKOFFICE_URL, false)

        return try {
            val details = service.checkoutDetails(
                CheckoutDetailsRequest(
                    publicKey,
                    checkoutRequest.content
                )
            )

            val chargeRequest = ChargeRequest(
                key = publicKey,
                tokenId = token.id,
                sessionId = details.sessionId,
                checkoutRequest = checkoutRequest.content,
                email = email,
                customAmount = customAmount,
                shippingRequest = shippingRequest,
                billingRequest = billingRequest
            )
            val result = responseHandler.handleSuccess(service.pay(chargeRequest))
            Result(result.status, result.data?.withEmail(email), result.error)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun threeDCheck(
        token: Token?,
        paymentMethod: Token?,
        amount: Int,
        currency: String,
        clientAuthRequest: String,
    ): Result<ThreeDCheckResponse> {
        val service = service(BuildConfig.API_URL, true)
        val request = ThreeDCheckRequest(
            amount = amount,
            currency = currency,
            card = token?.id,
            paymentMethod = paymentMethod?.id,
            paymentUserAgent = UserAgentGenerator().userAgent(),
            options = Options(clientAuthRequest.base64)
        )

        return try {
            responseHandler.handleSuccess(service.threeDCheck(request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun threeDAuthorize(
        token: Token,
        authorizationParameters: String
    ): Result<ThreeDAuthResponse> {
        val service = service(BuildConfig.API_URL, true)
        val request = ThreeDAuthRequest(authorizationParameters.base64, token.id)

        return try {
            responseHandler.handleSuccess(service.threeDAuthenticate(request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun threeDChallengeComplete(token: Token): Result<Token> {
        val service = service(BuildConfig.API_URL, true)
        val request = ThreeDChallengeCompleteRequest(token.id)

        return try {
            responseHandler.handleSuccess(service.threeDChallengeComplete(request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun checkoutRequestDetails(checkoutRequest: CheckoutRequest): Result<CheckoutRequestDetails> {
        val service = service(BuildConfig.BACKOFFICE_URL, false)
        val request = CheckoutDetailsRequest(publicKey, checkoutRequest.content)

        return try {
            responseHandler.handleSuccess(service.checkoutDetails(request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    private fun service(url: String, authorize: Boolean): APIService {
        val requestBuilder = RequestBuilder(publicKey, url, authorize)
        return requestBuilder.buildService(APIService::class.java)
    }
}