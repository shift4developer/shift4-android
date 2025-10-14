package com.shift4.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.shift4.BuildConfig
import org.json.JSONObject

class ThreeDSActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var token: String
    private lateinit var paymentMethod: String
    private lateinit var publicKey: String
    private lateinit var merchantId: String
    private var amount: Int = 0
    private lateinit var currency: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        token = intent.getStringExtra("token").orEmpty()
        paymentMethod = intent.getStringExtra("paymentMethod").orEmpty()
        publicKey = intent.getStringExtra("publicKey").orEmpty()
        merchantId = intent.getStringExtra("merchantId").orEmpty()
        amount = intent.getIntExtra("amount", 0)
        currency = intent.getStringExtra("currency").orEmpty()

        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(JSBridge(), "Android")
        supportActionBar?.hide()

        onBackPressedDispatcher.addCallback(this) {}

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val publicKeyParam = JSONObject.quote(publicKey)
                val tokenParam = JSONObject.quote(token)
                val paymentMethodParam = JSONObject.quote(paymentMethod)
                val currencyParam = JSONObject.quote(currency)
                val merchantIdParam = JSONObject.quote(merchantId)
                val js =
                    "run3ds($publicKeyParam, $tokenParam, $paymentMethodParam, $amount, $currencyParam, $merchantIdParam);"
                webView.evaluateJavascript(js, null)
            }
        }

        webView.loadUrl("${BuildConfig.JS_URL}/3ds-android.html?timestamp=${BuildConfig.BUILD_TIME}")
    }

    @Suppress("unused")
    inner class JSBridge {
        @JavascriptInterface
        fun on3dsResult(resultJson: String) {
            ThreeDSResultHolder.callback?.invoke(resultJson)
            ThreeDSResultHolder.callback = null
            finish()
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}
}

object ThreeDSResultHolder {
    var callback: ((String?) -> Unit)? = null
}