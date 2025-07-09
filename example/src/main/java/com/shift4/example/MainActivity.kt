package com.shift4.example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.shift4.Shift4
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.result.CheckoutResult
import com.shift4.request.token.TokenRequest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Shift4.CheckoutDialogFragmentResultListener {
    private lateinit var shift4: Shift4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shift4 = Shift4(findViewById<TextInputEditText>(R.id.textEditPublicKeyCheckout).text.toString())

        findViewById<Button>(R.id.buttonPaymentDialog).setOnClickListener {
            shift4.publicKey =
                findViewById<TextInputEditText>(R.id.textEditPublicKeyCheckout).text.toString()
            shift4.showCheckoutDialog(
                this,
                checkoutRequest = CheckoutRequest(findViewById<TextInputEditText>(R.id.textEditCheckoutRequest).text.toString()),
                merchantName = "Example Merchant",
                description = "Example payment",
                merchantLogo = R.drawable.ic_example_merchant_logo,
                collectShippingAddress = findViewById<SwitchMaterial>(R.id.switchShipping).isChecked,
                collectBillingAddress = findViewById<SwitchMaterial>(R.id.switchBilling).isChecked,
                email = findViewById<TextInputEditText>(R.id.textEditEmail).text.toString()
            )
        }

        findViewById<Button>(R.id.buttonPay).setOnClickListener {
            shift4.publicKey =
                findViewById<TextInputEditText>(R.id.textEditPublicKeyCustomForm).text.toString()

            val gpToken = findViewById<TextInputEditText>(R.id.textEditGooglePayToken).text.toString().takeIf { it.isNotBlank() }

            val tokenRequest = TokenRequest(
                number = findViewById<TextInputEditText>(R.id.textEditCardNumber).text.toString().takeIf { it.isNotBlank() },
                expMonth = findViewById<TextInputEditText>(R.id.textEditExpMonth).text.toString().takeIf { it.isNotBlank() },
                expYear = findViewById<TextInputEditText>(R.id.textEditExpYear).text.toString().takeIf { it.isNotBlank() },
                cvc = findViewById<TextInputEditText>(R.id.textEditCVC).text.toString().takeIf { it.isNotBlank() },
                googlePay = gpToken?.let { googlePayToken -> TokenRequest.GooglePayRequest(googlePayToken) }
            )

            val isGooglePayFlow = gpToken != null

            lifecycleScope.launch {
                val token = shift4.createToken(tokenRequest).data!!
                val authenticatedToken = shift4.authenticate(
                    token = if (isGooglePayFlow) null else token,
                    paymentMethod = if (isGooglePayFlow) token else null,
                    amount = 100,
                    currency = "USD",
                    activity = this@MainActivity
                )

                findViewById<TextView>(R.id.lastTokenIdCustomForm).text = "Last token: " + authenticatedToken.data?.id
            }
        }
    }

    override fun onCheckoutFinish(result: CheckoutResult?) {
        if (result == null) {
            Snackbar.make(
                findViewById(R.id.mainActivityLayout),
                "Cancelled!",
                Snackbar.LENGTH_SHORT
            ).show()
            findViewById<TextView>(R.id.lastChargeId).text = null
        }
        if (result?.data != null) {
            Snackbar.make(
                findViewById(R.id.mainActivityLayout),
                "Charge succeeded: ${result.data!!.id}, Subscription id: ${result.data!!.subscriptionId ?: "-"}",
                Snackbar.LENGTH_SHORT
            ).show()
            findViewById<TextView>(R.id.lastChargeId).text =
                result.data?.id?.let { id -> "Last charge: $id" }
            findViewById<TextView>(R.id.lastSubscriptionId).text =
                result.data?.subscriptionId?.let { id -> "Last subscription: $id" }
            findViewById<TextView>(R.id.lastEmail).text =
                result.data?.email?.let { id -> "Last email: $id" }
            findViewById<TextView>(R.id.lastCustomerId).text =
                result.data?.customer?.id?.let { id -> "Last customer: $id" }
        }
        if (result?.error != null) {
            Snackbar.make(
                findViewById(R.id.mainActivityLayout),
                result.error?.message(this)!!,
                Snackbar.LENGTH_SHORT
            ).show()
            findViewById<TextView>(R.id.lastChargeId).text = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Shift4.SHIFT4_RESULT_CODE) {
            onCheckoutFinish(data?.getSerializableExtra("result") as CheckoutResult?)
        }
    }
}