package com.shift4.example

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.result.CheckoutResult
class MainActivity : AppCompatActivity(), Shift4.CheckoutDialogFragmentResultListener {
    private lateinit var shift4: Shift4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shift4 = Shift4(
            applicationContext,
            findViewById<TextInputEditText>(R.id.textEditPublicKeyCheckout).text.toString(),
            "com.shift4.example",
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
        findViewById<TextInputEditText>(R.id.textEditCheckoutRequest).setText("ZjkyMGE5ODllMWE2MTFlODNlNTMzYzJjMjk5MGE0YmFhMzdkOWIzMjI5NTFmYTMwZjg3OTYxNmQyYzA5OTVkY3x7ImNoYXJnZSI6eyJhbW91bnQiOjEwMCwiY3VycmVuY3kiOiJFVVIifSwidGhyZWVEU2VjdXJlIjp7ImVuYWJsZSI6dHJ1ZSwicmVxdWlyZUVucm9sbGVkQ2FyZCI6dHJ1ZSwicmVxdWlyZVN1Y2Nlc3NmdWxMaWFiaWxpdHlTaGlmdEZvckVucm9sbGVkQ2FyZCI6dHJ1ZX19")
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

    override fun onDestroy() {
        super.onDestroy()
        shift4.cleanUp()
    }
}