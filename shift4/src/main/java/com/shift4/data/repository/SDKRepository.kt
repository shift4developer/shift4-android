package com.shift4.data.repository

import com.shift4.BuildConfig
import com.shift4.Shift4
import com.shift4.data.api.APIService
import com.shift4.data.api.RequestBuilder
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
        remember: Boolean = false,
        cvc: String? = null,
        sms: SMS? = null,
        customAmount: Int? = null,
        shipping: Shipping?,
        billing: Billing?
    ): Result<ChargeResult> {
        val service = service(BuildConfig.BACKOFFICE_URL, false)

        return try {
            val details = service.checkoutDetails(
                CheckoutDetailsRequest(
                    shift4.publicKey,
                    checkoutRequest.content
                )
            )

            val chargeRequest = ChargeRequest(
                key = shift4.publicKey,
                tokenId = token.id,
                sessionId = details.sessionId,
                checkoutRequest = checkoutRequest.content,
                email = email,
                rememberMe = remember,
                cvc = cvc,
                verificationSmsId = sms?.id,
                customAmount = customAmount,
                shipping = shipping,
                billing = billing
            )
            val result = responseHandler.handleSuccess(service.pay(chargeRequest))
            if (remember) {
                shift4.emailStorage.lastEmail = email
            }
            Result(result.status, result.data?.withEmail(email), result.error)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun lookup(email: String): Result<LookupResult> {
        val service = service(BuildConfig.BACKOFFICE_URL, false)
        val lookupRequest = LookupRequest(shift4.publicKey, email)

        return try {
            responseHandler.handleSuccess(service.lookup(lookupRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun savedToken(email: String): Result<Token> {
        val service = service(BuildConfig.BACKOFFICE_URL, false)
        val savedTokenRequest = SavedTokenRequest(shift4.publicKey, email, "android_sdk")

        return try {
            responseHandler.handleSuccess(service.savedToken(savedTokenRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun sendSMS(email: String): Result<SMS> {
        val service = service(BuildConfig.BACKOFFICE_URL, false)
        val sendSMSRequest = SendSMSRequest(shift4.publicKey, email)

        return try {
            responseHandler.handleSuccess(service.sendSMS(sendSMSRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun verifySMS(code: String, sms: SMS): Result<VerifySMSResponse> {
        val service = service(BuildConfig.BACKOFFICE_URL, false)
        val sendSMSRequest = VerifySMSRequest(code)

        return try {
            responseHandler.handleSuccess(service.verifySMS(sms.id, sendSMSRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun threeDCheck(
        token: Token,
        amount: Int,
        currency: String
    ): Result<ThreeDCheckResponse> {
        val service = service(BuildConfig.API_URL, true)
        val request = ThreeDCheckRequest(
            amount,
            currency,
            token.id,
            UserAgentGenerator().userAgent()
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
        val request = CheckoutDetailsRequest(shift4.publicKey, checkoutRequest.content)

        return try {
            responseHandler.handleSuccess(service.checkoutDetails(request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    private fun service(url: String, authorize: Boolean): APIService {
        val requestBuilder = RequestBuilder(shift4.publicKey, url, authorize)
        return requestBuilder.buildService(APIService::class.java)
    }
}