package com.shift4

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.shift4.checkout.CheckoutDialogFragment
import com.shift4.data.api.Result
import com.shift4.data.model.error.APIError
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.token.Token
import com.shift4.data.model.token.TokenRequest
import com.shift4.data.repository.SDKRepository
import com.shift4.threed.ThreeDAuthenticator
import com.shift4.utils.EmailStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Shift4(
    context: Context,
    var publicKey: String,
    var signature: String,
    private val trustedAppStores: List<String>? = null
) {
    internal val repository = SDKRepository(this)
    internal val emailStorage = EmailStorage(context)
    private var threeDAuthenticator: ThreeDAuthenticator? = null

    public interface CheckoutDialogFragmentResultListener {
        public fun onCheckoutFinish(result: Result<ChargeResult>?)
    }

    fun <T> showCheckoutDialog(
        activity: T,
        checkoutRequest: CheckoutRequest,
        merchantName: String,
        description: String,
        collectShippingAddress: Boolean = false,
        collectBillingAddress: Boolean = false,
        email: String? = null,
        @DrawableRes merchantLogo: Int? = null
    ) where T : AppCompatActivity, T : CheckoutDialogFragmentResultListener {
        if (!checkoutRequest.correct) {
            activity.onCheckoutFinish(Result.error(APIError.invalidCheckoutRequest))
            return
        }
        if (checkoutRequest.termsAndConditions != null) {
            activity.onCheckoutFinish(Result.error(APIError.unsupportedValue("termsAndConditions")))
            return
        }
        if (checkoutRequest.crossSaleOfferIds != null) {
            activity.onCheckoutFinish(Result.error(APIError.unsupportedValue("crossSaleOfferIds")))
            return
        }

        val checkoutDialogFragment = CheckoutDialogFragment()
        checkoutDialogFragment.arguments = Bundle().apply {
            putString("checkoutRequest", checkoutRequest.content)
            putString("signature", signature)
            putString("publicKey", publicKey)
            putString("merchantName", merchantName)
            putString("description", description)
            putBoolean("collectShippingAddress", collectShippingAddress)
            putBoolean("collectBillingAddress", collectBillingAddress)
            merchantLogo?.let { putInt("merchantLogoRes", it) }
            email?.let {
                if (it.isNotEmpty()) { putString("initialEmail", it) }
            }
            trustedAppStores?.let { putStringArray("trustedAppStores", it.toTypedArray()) }
        }
        checkoutDialogFragment.show(activity.supportFragmentManager, "checkoutDialogFragment")
    }

    fun cleanSavedCards() {
        emailStorage.cleanSavedEmails()
    }

    fun createToken(tokenRequest: TokenRequest, callback: (Result<Token>) -> Unit) {
        GlobalScope.launch {
            val token = repository.createToken(tokenRequest)
            GlobalScope.launch(Dispatchers.Main) {
                callback(token)
            }
        }
    }

    fun authenticate(
        token: Token,
        amount: Int,
        currency: String,
        activity: Activity,
        callback: (Result<Token>) -> Unit
    ) {
        if (threeDAuthenticator != null) {
            callback(Result.error(APIError.busy))
            return
        }
        threeDAuthenticator = ThreeDAuthenticator(repository, signature, trustedAppStores, activity) {
            threeDAuthenticator = null
            callback(it)
        }
        threeDAuthenticator?.authenticate(
            token,
            amount,
            currency
        )
    }
}