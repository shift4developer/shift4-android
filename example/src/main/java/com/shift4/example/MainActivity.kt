package com.shift4.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.shift4.Shift4
import com.shift4.data.api.Status
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.result.CheckoutResult
import com.shift4.data.model.token.TokenRequest


class MainActivity : AppCompatActivity(), Shift4.CheckoutDialogFragmentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val shift4 = Shift4(
            applicationContext,
            findViewById<TextInputEditText>(R.id.textEditPublicKeyCheckout).text.toString(),
            "8D:1D:B1:DC:EF:45:8E:16:55:BA:AE:C3:FA:16:CE:B0:8C:DE:85:EA:BE:57:5F:2E:BE:AC:E9:62:97:E0:55:37",
            listOf("com.google.android.packageinstaller")
        )

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

        findViewById<Button>(R.id.buttonCleanSavedCards).setOnClickListener {
            shift4.cleanSavedCards()
            Toast.makeText(applicationContext, "Cards cleaned!", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextInputEditText>(R.id.textEditPublicKeyCheckout).setText("pk_test_MMVbA8QlH8Eynb37DHWxX12Z")
        findViewById<TextInputEditText>(R.id.textEditCheckoutRequest).setText("NjkzNWY4ZDlhNDk2ZTg2YjdmN2E4ZjRmYWE5MjgwODdjYmU3MDI1YzE2YTkyNzhmNDYyNjg3YTljNWViNmY3Znx7ImNoYXJnZSI6eyJhbW91bnQiOjEwMCwiY3VycmVuY3kiOiJVU0QifX0")

        findViewById<Button>(R.id.buttonPay).setOnClickListener {
            shift4.publicKey =
                findViewById<TextInputEditText>(R.id.textEditPublicKeyCustomForm).text.toString()
            val tokenRequest = TokenRequest(
                findViewById<TextInputEditText>(R.id.textEditCardNumber).text.toString(),
                findViewById<TextInputEditText>(R.id.textEditExpMonth).text.toString(),
                findViewById<TextInputEditText>(R.id.textEditExpYear).text.toString(),
                findViewById<TextInputEditText>(R.id.textEditCVC).text.toString()
            )

            shift4.createToken(tokenRequest) { token ->
                when (token.status) {
                    Status.SUCCESS -> {
                        shift4.authenticate(
                            token.data!!,
                            10000,
                            "EUR",
                            this
                        ) { authenticatedToken ->
                            when (authenticatedToken.status) {
                                Status.SUCCESS -> {
                                    Snackbar.make(
                                        findViewById(R.id.mainActivityLayout),
                                        "3ds finished: ${authenticatedToken.data?.id}",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    findViewById<TextView>(R.id.lastTokenId).text =
                                        "Last token: ${authenticatedToken.data?.id}"
                                }
                                Status.ERROR -> {
                                    Snackbar.make(
                                        findViewById(R.id.mainActivityLayout),
                                        authenticatedToken.error?.message(this)!!,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    findViewById<TextView>(R.id.lastTokenId).text = null
                                    Log.e("ERROR", authenticatedToken.error?.message(this)!!)
                                }
                            }
                        }
                    }
                    Status.ERROR -> {
                        Snackbar.make(
                            findViewById(R.id.mainActivityLayout),
                            token.error?.message(null)!!,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        findViewById<TextView>(R.id.lastTokenId).text = null
                    }
                }
            }
        }
    }

    override fun onCheckoutFinish(result: CheckoutResult?) {
        result?.let {
            when (it.status) {
                Status.SUCCESS -> {
                    Snackbar.make(
                        findViewById(R.id.mainActivityLayout),
                        "Charge succeeded: ${it.data?.id}, Subscription id: ${it.data?.subscriptionId ?: "-"}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    findViewById<TextView>(R.id.lastChargeId).text =
                        it.data?.id?.let { id -> "Last charge: $id" }
                    findViewById<TextView>(R.id.lastSubscriptionId).text =
                        it.data?.subscriptionId?.let { id -> "Last subscription: $id" }
                    findViewById<TextView>(R.id.lastEmail).text =
                        it.data?.email?.let { id -> "Last email: $id" }
                    findViewById<TextView>(R.id.lastCustomerId).text =
                        it.data?.customer?.id?.let { id -> "Last customer: $id" }
                }
                Status.ERROR -> {
                    Snackbar.make(
                        findViewById(R.id.mainActivityLayout),
                        it.error?.message(this)!!,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    findViewById<TextView>(R.id.lastChargeId).text = null
                }
            }
        } ?: run {
            Snackbar.make(
                findViewById(R.id.mainActivityLayout),
                "Cancelled!",
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