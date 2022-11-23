package com.shift4.data.api

import com.shift4.data.model.lookup.LookupRequest
import com.shift4.data.model.lookup.LookupResult
import com.shift4.data.model.pay.ChargeRequest
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.checkoutRequestDetails.CheckoutRequestDetails
import com.shift4.data.model.checkoutRequestDetails.CheckoutDetailsRequest
import com.shift4.data.model.sms.SMS
import com.shift4.data.model.sms.SendSMSRequest
import com.shift4.data.model.sms.VerifySMSRequest
import com.shift4.data.model.sms.VerifySMSResponse
import com.shift4.data.model.threeD.ThreeDAuthRequest
import com.shift4.data.model.threeD.ThreeDAuthResponse
import com.shift4.data.model.threeD.ThreeDChallengeCompleteRequest
import com.shift4.data.model.threeD.ThreeDCheckRequest
import com.shift4.data.model.threeD.ThreeDCheckResponse
import com.shift4.data.model.token.SavedTokenRequest
import com.shift4.data.model.token.Token
import com.shift4.data.model.token.TokenRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

internal interface APIService {
    @Headers("Content-Type: application/json")
    @POST("tokens")
    suspend fun createToken(@Body tokenRequest: TokenRequest): Token

    @Headers("Content-Type: application/json")
    @POST("checkout/pay")
    suspend fun pay(@Body chargeRequest: ChargeRequest): ChargeResult

    @Headers("Content-Type: application/json")
    @POST("checkout/forms")
    suspend fun checkoutDetails(@Body checkoutDetailsRequest: CheckoutDetailsRequest): CheckoutRequestDetails

    @Headers("Content-Type: application/json")
    @POST("checkout/lookup")
    suspend fun lookup(@Body lookupRequest: LookupRequest): LookupResult

    @Headers("Content-Type: application/json")
    @POST("checkout/tokens")
    suspend fun savedToken(@Body savedTokenRequest: SavedTokenRequest): Token

    @Headers("Content-Type: application/json")
    @POST("checkout/verification-sms")
    suspend fun sendSMS(@Body sendSMSRequest: SendSMSRequest): SMS

    @Headers("Content-Type: application/json")
    @POST("checkout/verification-sms/{id}")
    suspend fun verifySMS(
        @Path("id") id: String,
        @Body verifySMSRequest: VerifySMSRequest
    ): VerifySMSResponse

    @Headers("Content-Type: application/json")
    @POST("3d-secure")
    suspend fun threeDCheck(
        @Body threeDCheckRequest: ThreeDCheckRequest
    ): ThreeDCheckResponse

    @Headers("Content-Type: application/json")
    @POST("3d-secure/v2/authenticate")
    suspend fun threeDAuthenticate(
        @Body threeDAuthRequest: ThreeDAuthRequest
    ): ThreeDAuthResponse

    @Headers("Content-Type: application/json")
    @POST("3d-secure/v2/challenge-complete")
    suspend fun threeDChallengeComplete(
        @Body threeDChallengeCompleteRequest: ThreeDChallengeCompleteRequest
    ): Token
}