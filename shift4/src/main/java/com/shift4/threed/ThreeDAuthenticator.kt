package com.shift4.threed


import android.app.Activity
import android.content.Intent
import com.google.gson.Gson
import com.shift4.checkout.ThreeDSActivity
import com.shift4.checkout.ThreeDSResultHolder
import com.shift4.data.api.Result
import com.shift4.response.token.GooglePayAuthMethod
import com.shift4.response.token.Token
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class ThreeDAuthenticator(
    private val publicKey: String
) {
    suspend fun authenticate(
        token: Token?, paymentMethod: Token?, amount: Int, currency: String, activity: Activity
    ): Result<Token> = suspendCancellableCoroutine { cont ->
        if (paymentMethod?.googlePay?.authMethod == GooglePayAuthMethod.CRYPTOGRAM_3DS) {
            cont.resume(Result.success(paymentMethod))
            return@suspendCancellableCoroutine
        }

        ThreeDSResultHolder.callback = {
            val result = Gson().fromJson(it, ThreeDSecureResult::class.java)
            cont.resume(Result.success(result.result))
        }

        val intent = Intent(activity, ThreeDSActivity::class.java).apply {
            putExtra("publicKey", publicKey)
            putExtra("token", token?.id)
            putExtra("paymentMethod", paymentMethod?.id)
            putExtra("amount", amount)
            putExtra("currency", currency)
        }
        activity.startActivity(intent)
    }

    private data class ThreeDSecureResult(
        val success: Boolean, val result: Token
    )
}