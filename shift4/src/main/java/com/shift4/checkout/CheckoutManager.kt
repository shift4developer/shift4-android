package com.shift4.checkout

import android.app.Activity
import com.shift4.data.api.Result
import com.shift4.data.model.result.Status
import com.shift4.data.model.address.Billing
import com.shift4.data.model.address.Shipping
import com.shift4.data.model.error.APIError
import com.shift4.data.model.lookup.LookupResult
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.sms.SMS
import com.shift4.data.model.sms.VerifySMSResponse
import com.shift4.data.model.token.Token
import com.shift4.data.model.token.TokenRequest
import com.shift4.data.repository.SDKRepository
import com.shift4.threed.ThreeDManager
import com.shift4.utils.EmailStorage
import com.shift4.utils.fromBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class CheckoutManager(
    private val repository: SDKRepository,
    internal val emailStorage: EmailStorage,
    private val signature: String,
    private val packageName: String,
    private val trustedAppStores: List<String>?,
    private val coroutineScope: CoroutineScope
) {
    val threeDManager by lazy { ThreeDManager() }

    internal suspend fun pay(
        token: Token,
        checkoutRequest: CheckoutRequest,
        email: String,
        remember: Boolean = false,
        threeDActivity: Activity,
        cvc: String? = null,
        sms: SMS? = null,
        customAmount: Int? = null,
        customCurrency: String? = null,
        shipping: Shipping?,
        billing: Billing?,
        callback: (Result<ChargeResult>) -> Unit
    ) {
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
                    remember,
                    cvc,
                    sms,
                    computedCustomAmount,
                    shipping = shipping,
                    billing = billing
                )
            )
            return
        }
        val threeDCheck =
            repository.threeDCheck(
                token,
                customAmount ?: checkoutRequest.amount,
                customCurrency ?: checkoutRequest.currency
            )
        val threeDCheckData = threeDCheck.data ?: run {
            callback(Result.error(threeDCheck.error ?: APIError.unknown, null))
            return@pay
        }
        val enrolled = threeDCheckData.token.threeDSecureInfo?.enrolled ?: false

        if (checkoutRequest.requireEnrolledCard && !enrolled) {
            callback(Result.error(APIError.enrolledCardIsRequired, null))
            return
        }

        if (!enrolled) {
            callback(
                repository.pay(
                    token = token,
                    checkoutRequest = checkoutRequest,
                    email = email,
                    remember = remember,
                    cvc = cvc,
                    sms = sms,
                    customAmount = computedCustomAmount,
                    shipping = shipping,
                    billing = billing
                )
            )
            return
        }

        val authorizationParameters: String?
        try {
            val warnings = threeDManager.initialize(
                threeDActivity,
                threeDCheckData.token.brand,
                threeDCheckData.directoryServerCertificate,
                threeDCheckData.sdkLicense,
                signature,
                packageName,
                trustedAppStores
            )
            threeDManager.createTransaction(threeDCheckData.version, threeDCheckData.token.brand)
            coroutineScope.launch(Dispatchers.Main) { threeDManager.showProgressDialog() }
            authorizationParameters =
                threeDManager.authenticationRequestParameters()?.authRequest
            if ((authorizationParameters ?: "").isEmpty()) {
                warnings.firstOrNull()?.let {
                    coroutineScope.launch(Dispatchers.Main) {
                        threeDManager.hideProgressDialog()
                        callback(Result.error(APIError.threeDError(it)))
                    }
                    return@pay
                } ?: run {
                    coroutineScope.launch(Dispatchers.Main) {
                        threeDManager.hideProgressDialog()
                        callback(Result.error(APIError.unknownThreeD))
                    }
                    return@pay
                }
            }
        } catch (e: Exception) {
            coroutineScope.launch(Dispatchers.Main) { threeDManager.hideProgressDialog() }
            callback(Result.error(APIError.threeD(e)))
            return@pay
        }

        val threeDAuthentication =
            repository.threeDAuthorize(threeDCheckData.token, authorizationParameters ?: "")
        val ares = threeDAuthentication.data?.ares ?: run {
            coroutineScope.launch(Dispatchers.Main) { threeDManager.hideProgressDialog() }
            callback(Result.error(threeDAuthentication.error ?: APIError.unknown))
            return@pay
        }

        if (ares.transStatus == "C" || ares.transStatus == "D") {
            val response = ares.clientAuthResponse.fromBase64
            threeDManager.startChallenge(response) { success, error ->
                if (error != null) {
                    coroutineScope.launch(Dispatchers.Main) { threeDManager.hideProgressDialog() }
                    callback(Result.error(error, null))
                } else {
                    coroutineScope.launch(Dispatchers.IO) {
                        val challengeCompleteToken =
                            repository.threeDChallengeComplete(threeDCheckData.token)
                        coroutineScope.launch(Dispatchers.Main) { threeDManager.hideProgressDialog() }
                        when (challengeCompleteToken.status) {
                            Status.SUCCESS -> {
                                if (challengeCompleteToken.data?.threeDSecureInfo?.liabilityShift == "failed" && checkoutRequest.requireSuccessfulLiabilityShiftForEnrolledCard) {
                                    callback(
                                        Result.error(
                                            APIError.successfulLiabilityShiftIsRequired,
                                            null
                                        )
                                    )
                                } else {
                                    val paymentResult = repository.pay(
                                        token,
                                        checkoutRequest,
                                        email,
                                        remember,
                                        cvc,
                                        sms,
                                        computedCustomAmount,
                                        shipping = shipping,
                                        billing = billing
                                    )
                                    coroutineScope.launch(Dispatchers.Main) {
                                        callback(
                                            paymentResult
                                        )
                                    }
                                }
                            }

                            Status.ERROR -> {
                                val paymentResult = repository.pay(
                                    token,
                                    checkoutRequest,
                                    email,
                                    remember,
                                    cvc,
                                    sms,
                                    computedCustomAmount,
                                    shipping = shipping,
                                    billing = billing
                                )
                                coroutineScope.launch(Dispatchers.Main) { callback(paymentResult) }
                            }
                        }
                    }
                }
            }
        } else if (ares.transStatus == "N" || ares.transStatus == "U" || ares.transStatus == "R") {
            if (checkoutRequest.requireSuccessfulLiabilityShiftForEnrolledCard) {
                callback(Result.error(APIError.successfulLiabilityShiftIsRequired, null))
                coroutineScope.launch(Dispatchers.Main) { threeDManager.hideProgressDialog() }
                return@pay
            } else {
                repository.threeDChallengeComplete(threeDCheckData.token)
                val paymentResult = repository.pay(
                    token,
                    checkoutRequest,
                    email,
                    remember,
                    cvc,
                    sms,
                    computedCustomAmount,
                    shipping = shipping,
                    billing = billing
                )
                coroutineScope.launch(Dispatchers.Main) {
                    threeDManager.hideProgressDialog()
                    callback(paymentResult)
                }
            }
        } else {
            repository.threeDChallengeComplete(threeDCheckData.token)
            val paymentResult = repository.pay(
                token,
                checkoutRequest,
                email,
                remember,
                cvc,
                sms,
                computedCustomAmount,
                shipping = shipping,
                billing = billing
            )
            coroutineScope.launch(Dispatchers.Main) {
                threeDManager.hideProgressDialog()
                callback(paymentResult)
            }
        }
    }

    internal suspend fun pay(
        tokenRequest: TokenRequest,
        checkoutRequest: CheckoutRequest,
        email: String,
        remember: Boolean = false,
        threeDActivity: Activity,
        cvc: String? = null,
        sms: SMS? = null,
        customAmount: Int? = null,
        customCurrency: String? = null, 
        shipping: Shipping?,
        billing: Billing?,
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
            remember,
            threeDActivity,
            cvc,
            sms,
            customAmount,
            customCurrency,
            shipping = shipping,
            billing = billing,
            callback
        )
    }

    internal suspend fun lookup(email: String): Result<LookupResult> = repository.lookup(email)

    internal suspend fun checkoutRequestDetails(checkoutRequest: CheckoutRequest) =
        repository.checkoutRequestDetails(checkoutRequest)

    internal suspend fun savedToken(email: String): Result<Token> = repository.savedToken(email)

    internal suspend fun sendSMS(email: String): Result<SMS> = repository.sendSMS(email)

    internal suspend fun verifySMS(code: String, sms: SMS): Result<VerifySMSResponse> =
        repository.verifySMS(code, sms)
}